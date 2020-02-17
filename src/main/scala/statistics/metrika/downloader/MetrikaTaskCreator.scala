package statistics.metrika.downloader

import cats.effect.IO
import com.typesafe.scalalogging.LazyLogging
import statistics.metrika.api.MetrikaClient
import statistics.metrika.api.dto.FetcherApi._
import statistics.metrika.api.dto.MetrikaApi._
import sttp.client.{DeserializationError, HttpError}

object MetrikaTaskCreator extends LazyLogging {

  def createMetrikaTask(downloadStatisticsTask: DownloadStatisticsTask)(implicit metrikaClient: MetrikaClient): IO[MetrikaLogRequest] = {
    for {
      resp <- metrikaClient.createLogRequest(downloadStatisticsTask)
      createdLogRequest <- resp.body match {
        case Right(value) => IO.pure(value.logRequest)
        case Left(error) => error match {
          case DeserializationError(body, error) => logger.error(s"Failed to deserialize a response with error ${error.getMessage} ${body}"); IO.raiseError(error)
          case HttpError(body) => logger.error(s"Failed to make a http request: the response is ${body}"); IO.raiseError(error)
        }
      }
    } yield (createdLogRequest)
  }
}
