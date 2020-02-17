package statistics.metrika.db

import cats.effect._
import cats.implicits._
import doobie._
import doobie.implicits._
import doobie.hikari._

object DoobieManager {
  def initTransactor(implicit cs: ContextShift[IO]): Resource[IO, HikariTransactor[IO]] = {
    for {
      ce <- ExecutionContexts.fixedThreadPool[IO](10) // our connect EC
      be <- Blocker[IO]    // our blocking EC
      xa <- HikariTransactor.newHikariTransactor[IO](
        "org.postgresql.Driver",                        // driver classname
        "jdbc:postgresql://localhost:5442/metrika_fetcher",   // connect URL
        "guest",                                   // username
        "guest",                                     // password
        ce,                                     // await connection here
        be                                      // execute JDBC operations here
      )
    } yield xa
  }
}
