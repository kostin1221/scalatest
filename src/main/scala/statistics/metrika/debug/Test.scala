package statistics.metrika.debug

import cats.effect._
import com.typesafe.scalalogging.LazyLogging
import doobie._
import doobie.implicits._
import fs2._

object Test extends IOApp with LazyLogging {
  override def run(args: List[String]): IO[ExitCode] = {

    val xa = Transactor.fromDriverManager[IO](
      "org.postgresql.Driver", // driver classname
      "jdbc:postgresql://localhost:5442/metrika_fetcher", // connect URL (driver-specific)
      "guest", // user
      "guest", // password
      Blocker
        .liftExecutionContext(ExecutionContexts.synchronous) // just for testing
    )
    val stream = Stream.eval(insert1("dsdsd", "dsdsdsd").run)

    xa.transP.apply(stream).compile.drain.map(_ => ExitCode.Success)
  }

  def insert1(first: String, second: String): Update0 =
    sql"insert into metrika_visits (first, second) values ($first, $second)".update

}
