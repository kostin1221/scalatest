package statistics.metrika.db

import cats.effect.{ContextShift, IO}
import doobie.hikari.HikariTransactor
import doobie.implicits._
import doobie.util.update.Update

object DbStreamer {
  def streamToDb(
    source: fs2.Stream[IO, (Array[String], Array[String])]
  )(implicit cs: ContextShift[IO], xa: HikariTransactor[IO]): IO[Int] = {

    source
      .chunkN(100)
      .evalMap(chunk => {
        val csvHead = chunk.head.get._1
        val data = chunk.map(_._2)

        insert1(csvHead, data.toList).run.transact(xa)
      })
      .compile
      .fold(0)(_ + _)
  }

  private def insert1(csvHead: Array[String], data: List[Array[String]]) = {
    val columnsList = MetrikaFieldsMap.mapMetrikaFieldsToColumns(csvHead)
    val cols = columnsList.mkString(", ")

    data.foreach(row => assert(row.length == csvHead.length))
//    def getQuestionsList(n: Int): List[String] = if (n <= 1) List("?") else getQuestionsList(n-1) ::: List("?")
//    val questionsPart = getQuestionsList(csvHead.length)

    val valuesSqlPart = data
      .map(row => "(" + row.map(col => s"'$col'").mkString(",") + ")")
      .mkString(",")

    Update(s"insert into metrika_visits ($cols) values $valuesSqlPart")
      .toUpdate0()
  }

}
