package statistics.metrika.downloader

import cats.effect._
import cats.implicits._

//import scala.concurrent.ExecutionContext
import com.typesafe.scalalogging.LazyLogging
import statistics.metrika.api.MetrikaClient
import statistics.metrika.api.dto.MetrikaApi._
import statistics.metrika.downloader.Exceptions.LogRequestIsInWrongStatus
import sttp.client.{DeserializationError, HttpError}

import scala.concurrent.duration._

object MetrikaTaskProgress extends LazyLogging {
  private val retryTimeout = 10.seconds

  def getLogRequestWhenReady(metrikaLogRequest: MetrikaLogGet)(
    implicit metrikaClient: MetrikaClient,
    timer: Timer[IO]
  ): IO[MetrikaLogRequest] = {
    for {
      resp <- metrikaClient.getLogRequest(metrikaLogRequest)
      logRequest <- resp.body match {
        case Right(value) => IO.pure(value.logRequest)
        case Left(error) =>
          error match {
            case DeserializationError(body, error) =>
              logger.error(
                s"Failed to deserialize a response with error ${error.getMessage} ${body}"
              ); IO.raiseError(error)
            case HttpError(body) =>
              logger.error(
                s"Failed to make a http request: the response is ${body}"
              ); IO.raiseError(error)
          }
      }
      logRequest <- logRequest.status match {
        case Processed =>
          IO(
            logger.debug(
              s"Request ${logRequest.requestId} are ready to provide the data"
            )
          ) *> IO.pure(logRequest)
        case Created =>
          IO(
            logger.info(
              s"Request ${logRequest.requestId} has not yet proceeded, start sleeping until the next attempt"
            )
          ) *> timer.sleep(retryTimeout) *> getLogRequestWhenReady(
            metrikaLogRequest
          )
        case otherStatus =>
          IO.raiseError(
            new LogRequestIsInWrongStatus(
              s"Log request is in ${otherStatus.toString} status so no data is available"
            )
          )
      }
    } yield (logRequest)
  }
}
