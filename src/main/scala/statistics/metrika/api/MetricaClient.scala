package statistics.metrika.api

import sttp.client.asynchttpclient.fs2.AsyncHttpClientFs2Backend
import sttp.client._
import java.nio.ByteBuffer

import cats.effect.{ContextShift, IO}
import fs2.Stream
import statistics.metrika.api.dto._
import sttp.client.circe._
import MetrikaApi._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class MetricaClient {
  private val BaseUrl = "https://api-metrika.yandex.net";

  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.Implicits.global)

  def listOfRequests(request: MetrikaLogGet) = {
    val effect = AsyncHttpClientFs2Backend[IO]().flatMap { implicit backend =>
      val response =
        basicRequest
          .get(uri"$BaseUrl/management/v1/counter/${request.counterId}/logrequests")
          .auth.bearer(request.accessToken)
          .response(asJson[List[MetrikaLogRequest]])
          .readTimeout(Duration(5, SECONDS))
          .send()

      response
    }
    effect
  }

  def createLog(): Unit = {

  }
}
