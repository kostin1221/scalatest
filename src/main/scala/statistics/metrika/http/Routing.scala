package statistics.metrika.http

import cats.data.Kleisli
import cats.effect._
import doobie.hikari.HikariTransactor
import statistics.metrika.api.MetrikaClient
import statistics.metrika.downloader.MetrikaDataStreamer
//import cats.implicits._

import org.http4s._
import org.http4s.circe._
//import org.http4s.dsl.io._
//import org.http4s.implicits._

import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.server.Router
import statistics.metrika.api.dto.FetcherApi.DownloadStatisticsTask

object Routing {
  private implicit val downloadTaskDecoder
    : EntityDecoder[IO, DownloadStatisticsTask] =
    jsonOf[IO, DownloadStatisticsTask]
  private implicit val downloadTaskEnсoder
    : EntityEncoder[IO, DownloadStatisticsTask] =
    jsonEncoderOf[IO, DownloadStatisticsTask]

  def getRouter(transactor: HikariTransactor[IO])(
    implicit metrikaClient: MetrikaClient,
    cs: ContextShift[IO],
    timer: Timer[IO]
  ): Kleisli[IO, Request[IO], Response[IO]] = {

    implicit val imTransactor: HikariTransactor[IO] = transactor

    val helloWorldService = HttpRoutes.of[IO] {
      case req @ POST -> Root / "direct-download-task" =>
        for {
          // Decode a User request
          downloadStatisticsTask <- req.as[DownloadStatisticsTask]

          fiber <- MetrikaDataStreamer
            .createMetrikaTaskAndStreamToDb(downloadStatisticsTask)
            .start

          // Encode a hello response
          resp <- Ok("Started")
        } yield (resp)
    }

    Router("/" -> helloWorldService).orNotFound
  }
}
