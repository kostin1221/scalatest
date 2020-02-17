package statistics.metrika.api

import sttp.client.asynchttpclient.fs2.AsyncHttpClientFs2Backend
import sttp.client._
import cats.effect.{ContextShift, IO}
import statistics.metrika.api.dto._
import sttp.client.circe._
import MetrikaApi._
import com.typesafe.scalalogging.LazyLogging
import io.circe
import fs2.{Chunk, Pipe, Pull, Pure, Stream, text}
import java.nio.ByteBuffer

import scala.concurrent.duration._

class MetrikaClient(implicit cs: ContextShift[IO]) extends LazyLogging {
  private val BaseUrl = "https://api-metrika.yandex.net";

  def getListOfLogRequests(request: MetrikaLogGet): IO[Response[Either[ResponseError[circe.Error], LogRequestsResponse]]] = {
    AsyncHttpClientFs2Backend[IO]().flatMap { implicit backend =>
      val response =
        createRequest(request)
          .get(uri"$BaseUrl/management/v1/counter/${request.counterId}/logrequests")
          .response(asJson[LogRequestsResponse])
          .send()

      response
    }
  }

  def getLogRequest(request: MetrikaLogGet): IO[Response[Either[ResponseError[circe.Error], LogRequestResponse]]] = {
    AsyncHttpClientFs2Backend[IO]().flatMap { implicit backend =>
      val response =
        createRequest(request)
          .get(uri"$BaseUrl/management/v1/counter/${request.counterId}/logrequest/${request.requestId}")
          .response(asJson[LogRequestResponse])
          .send()

      response
    }
  }

  def createLogRequest(request: MetrikaLogCreate): IO[Response[Either[ResponseError[circe.Error], LogRequestResponse]]] = {
    AsyncHttpClientFs2Backend[IO]().flatMap { implicit backend =>
        createRequest(request)
          .post(uri"$BaseUrl/management/v1/counter/${request.counterId}/logrequests")
          .body(Map[String, String]("date1" -> request.date1, "date2" -> request.date2, "fields" -> request.fields.mkString(","), "source" -> request.source.toString))
          .response(asJson[LogRequestResponse])
          .send()
//          .map(logError)
    }
  }

  def streamLogRequestPartData(request: MetrikaLogStreamPart): IO[Stream[IO, (Array[String], Array[String])]] = {
    AsyncHttpClientFs2Backend[IO]().flatMap { implicit backend =>
        createRequest(request)
          .get(uri"$BaseUrl/management/v1/counter/${request.counterId}/logrequest/${request.requestId}/part/${request.partNumber}/download")
          .response(asStream[Stream[IO, ByteBuffer]])
          .readTimeout(5.minute)
          .send()
          .flatMap(resp => {
            resp.body match {
              case Right(resp) => IO.pure(resp
                .map(bb => Chunk.array(bb.array))
                .through(text.utf8DecodeC)
                .through(text.lines)
                .filter(!_.isEmpty)
                .map(line => line.split("\t"))
                .through(turpleWithHead)
              )
              case Left(body) => IO.raiseError(new Exception(s"Failed to get a stream, resp: ${body}, resp code: ${resp.code}"))
            }
          })
    }
  }

  private def turpleWithHead[F[_], O]: Pipe[F, O, (O, O)] = {
    def go(s: Stream[F,O], head: Option[O]): Pull[F,(O, O),Unit] =
      s.pull.uncons.flatMap {
        case Some((hd: Chunk[O], s: Stream[F, O])) =>
          val (headItem, tailChunks) = head match {
            case None => val (head, tail: Chunk[O]) = hd.splitAt(1)
              (head.last.get, tail)
            case Some(head) => (head, hd)
          }
          val value: Chunk[(O, O)] = tailChunks.map((el: O) => (headItem, el))
          Pull.output(value) >> go(s, Some(headItem))

        case None => Pull.done
      }


    in => go(in, None).stream
  }
  private def createRequest(baseMetrikaRequest: BaseMetrikaRequest) = {
    basicRequest
      .auth.bearer(baseMetrikaRequest.accessToken)
      .readTimeout(Duration(5, SECONDS))
  }
}
