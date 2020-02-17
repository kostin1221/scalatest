package statistics.metrika.debug

import statistics.metrika.api.MetrikaClient
import statistics.metrika.api.dto.MetrikaApi._
import cats.effect._
import cats._
import doobie.free.connection._
import statistics.metrika.db.{DbStreamer, MetrikaFieldsMap}
//import doobie.util.transactor.{Transactor, _}
//import cats.implicits._

import fs2.interop.reactivestreams._

import cats.syntax.show._
import com.typesafe.scalalogging.{LazyLogging, Logger}
import fs2._
import sttp.client.{DeserializationError, HttpError}
import cats.syntax.parallel._
import cats.data.NonEmptyList
import statistics.metrika.api.dto.FetcherApi._
import statistics.metrika.db.DoobieManager.initTransactor
import statistics.metrika.downloader.MetrikaDataStreamer._
//import doobie._
import doobie.implicits._
import doobie.free.connection._
//import doobie.syntax.stream._
import doobie.hikari._

import scala.concurrent.ExecutionContext

object RunRequestMetrikaData extends IOApp with LazyLogging {
//  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.Implicits.global)
//  implicit val ioContextShift: ContextShift[IO] = IO.contextShift(scala.concurrent.ExecutionContext.Implicits.global)

  override def run(args: List[String]): IO[ExitCode] = {

    implicit val metrikaClient = new MetrikaClient()

    println(MetrikaFieldsMap.generateCreateTableSql)
    // account id 461269
    val token = "AgAAAAAoCRsJAAAdEIMpaBFdk0N9iw_7QiczytA"
    val counter = 44977411

//    val count = new java.util.concurrent.atomic.AtomicLong(0)

    val transactor = initTransactor
//    val xa = Transactor.fromDriverManager[IO](
//      "org.postgresql.Driver",     // driver classname
//      "jdbc:postgresql://localhost:5442/metrika_fetcher",     // connect URL (driver-specific)
//      "guest",                  // user
//      "guest",                          // password
//      Blocker.liftExecutionContext(ExecutionContexts.synchronous) // just for testing
//    )


//    val task = DownloadStatisticsTask(token, counter, "2020-02-01", "2020-02-10", List("ym:s:visitID", "ym:s:UTMMedium"), Visits)
//    return createTaskAndStreamWhenReady(task)
//      .flatMap(stream => {
//        stream
//
//          .take(300)
////          .evalTap(str => IO(println(str)))
//          .map(line => line.split("\t"))
//
//          .onFinalize(IO(println("Count " + count.get())))
//
//          //      .chunkN(100)
//          //      .covary[IO]
//          //      .evalTap(chunk => IO(println(chunk)))
//
//          .compile
//          .drain
//      })
//      .flatMap(_ => IO.pure(ExitCode.Success))


//    val metrikaLogGet = MetrikaLogGet(token, counter, 7304311)
//    val io = for {
//      resp <- metrikaClient.getLogRequest(metrikaLogGet)
//      logRequest <- resp.body match {
//        case Right(value) => IO.pure(value.logRequest)
//        case Left(error) => error match {
//          case DeserializationError(body, error) => logger.error(s"Failed to deserialize a response with error ${error.getMessage} ${body}"); IO.raiseError(error)
//          case HttpError(body) => logger.error(s"Failed to make a http request: the response is ${body}"); IO.raiseError(error)
//        }
//      }
//    } yield (logRequest)
//
//    //    val metrikaLogCreate = MetrikaLogCreate(
//    //      token,
//    //      counter,
//    //      "2020-02-01",
//    //      "2020-02-05",
//    //      List("ym:s:visitID", "ym:s:UTMMedium"),
//    //      Visits
//    //    )
//    //
//    //    val io = for {
//    //      resp <- metrikaClient.createLogRequest(metrikaLogCreate)
//    //      body <- resp.body match {
//    //        case Right(value) => IO.pure(value)
//    //        case Left(error) => error match {
//    //            case DeserializationError(body, _) => logger.error(s"Failed to decerialize response ${body}"); IO.raiseError(new Exception("ddd"))
//    //            case HttpError(body) => logger.error(s"Failed to make a http request: the response is ${body}"); IO.raiseError(new Exception("ddd"))
//    //        }
//    //      }
//    //    } yield (body)
//    //
//    //  io.map(r => IO(println(r)))

    val req = MetrikaLogStreamPart(token, counter, 7380382, 0)
    val streamIO = metrikaClient.streamLogRequestPartData(req)

    val r = for {
      stream <- streamIO
//      upd <- stream.evalTap { p => IO(println(p)) }.compile.drain
      upd <- DbStreamer.streamToDb(stream)
    } yield upd

    return r.map(_ => ExitCode.Success)
    val count = new java.util.concurrent.atomic.AtomicLong(0)

//    val st: Stream[Pure, List[String]] = Stream.emits(Seq(List("dsdsd", "4444"),List("dsdsd111", "222")))

//    val testStream = st.evalMap(chunk => insert1(chunk(0), chunk(1)).run)

//    return transactor.use(xa => {
//      toDoobieStreamOps(testStream).transact(xa).compile.drain
//    }.map(_ => ExitCode.Success))

//    val io = transactor.use(xa => {
////        val noCommitTransactionXa = Transactor.after.set(xa, unit)
//
//      val noCommitTransactionXa = xa
////        val noCommitTransactionXa = Transactor.strategy.set(xa, Strategy.default.copy(after = unit, always = unit))
//
//        for {
//          stream <- stream
//          stream <- IO.pure(stream
//            .map(line => line.split("\t"))
//            //          .onFinalize(IO(println("Count " + count.get())))
//            //          .chunkN(100)
//            .take(5)
//            .evalMap(chunk => {
//              println(chunk(0))
//              insert1(chunk(0), chunk(1)).run.transact(xa)
//            })
////            .onFinalize(commit.transact(noCommitTransactionXa))
//          )
//          stream <- stream.compile.drain
//        } yield stream
//      }
//    )

//    io.unsafeRunSync()
    IO.pure(ExitCode.Success)
//    io.map(_ => ExitCode.Success)
  }

  def insert1(first: String, second: String) =
    sql"insert into metrika_visits (first, second) values ($first, $second)".update

}
