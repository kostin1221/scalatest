package statistics.metrika

import cats.effect._
import cats.implicits._
import doobie.util.transactor._
import statistics.metrika.api.MetrikaClient
//import org.http4s.syntax._
//import org.http4s.dsl.io._
import statistics.metrika.db.DoobieManager._
import org.http4s.implicits._
import org.http4s.server.blaze._

import statistics.metrika.http.Routing

object Main extends IOApp {
//  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.Implicits.global)
//  implicit val sttpBackend = AsyncHttpClientFs2Backend()
  implicit val metrikaClient = new MetrikaClient;

  override def run(args: List[String]): IO[ExitCode] = {


    val resources = for {
      xa <- initTransactor
      httpServer <-
        BlazeServerBuilder[IO]
          .bindHttp(8080)
          .withHttpApp(Routing.getRouter(xa))
          .resource

    } yield (xa, httpServer)

    resources.use { _ => IO.never }.as(ExitCode(0))
  }

//    args.headOption match {
//      case Some(name) =>
//        IO(println(s"Hello, $name.")).as(ExitCode.Success)
//      case None =>
//        IO(System.err.println("Usage: MyApp name")).as(ExitCode(2))
//    }
//    sys.addShutdownHook(argos.stop())
}
