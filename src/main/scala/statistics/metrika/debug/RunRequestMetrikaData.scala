package statistics.metrika.debug

import statistics.metrika.api.MetricaClient
import statistics.metrika.api.dto.MetrikaLogGet
import cats.effect._
import com.typesafe.scalalogging.LazyLogging

object RunRequestMetrikaData extends App with LazyLogging {

  // account id 461269
  val token = "AgAAAAAoCRsJAAAdEIMpaBFdk0N9iw_7QiczytA";
  val counter = 44977411;

  val metrikaClient = new MetricaClient()

  val metrikaLogGet = MetrikaLogGet(token, counter)

  val io = for {
    resp <- metrikaClient.listOfRequests(metrikaLogGet)
    value <- resp.body match {
      case Right(value) => IO.pure(value)
      case Left(error) =>
        println(error.getCause);
        logger.error(s"Metrika request error ${error.getMessage}")
        IO.pure(None)
    }
  } yield (value)

  val resp = io.unsafeRunSync()

  println(resp)

//  println(resp.unsafeRunSync())

}
