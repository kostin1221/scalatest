package statistics.metrika.api.dto

import io.circe._

import io.circe.generic.extras._
import io.circe.generic.extras.semiauto._
import io.circe.syntax._

object MetrikaApi {
  implicit val config: Configuration = Configuration.default.withSnakeCaseMemberNames

  sealed trait LogRequestStatus {
    override def toString: String = this match {
      case Processed => "processed"
      case Canceled => "canceled"
      case ProcessingFailed => "processing_failed"
      case Created => "created"
      case CleanedByUser => "cleaned_by_user"
      case CleanedAutomaticallyAsTooOld => "cleaned_automatically_as_too_old"
    }
  }
  case object Processed extends LogRequestStatus
  case object Canceled extends LogRequestStatus
  case object Created extends LogRequestStatus
  case object CleanedByUser extends LogRequestStatus
  case object CleanedAutomaticallyAsTooOld extends LogRequestStatus
  case object ProcessingFailed extends LogRequestStatus

  sealed trait Source {
    override def toString: String = this match {
      case Hits => "hits"
      case Visits => "visits"
    }
  }
  case object Visits extends Source
  case object Hits extends Source

  case class LogRequestPart(partNumber: Int,
                             size: Int
                            )

  case class MetrikaLogRequest(requestId: Int,
                               counterId: Int,
                               source: Source,
                               fields: List[String],
                               status: LogRequestStatus,
                               parts: Option[List[LogRequestPart]]
                              )

  case class LogRequestsResponse(requests: List[MetrikaLogRequest])
  case class LogRequestResponse(logRequest: MetrikaLogRequest)

  case class MetrikaCredentials(accessToken: String, counterId: Int)

  sealed trait BaseMetrikaRequest {
    val accessToken: String
  }

  case class MetrikaLogList(accessToken: String, counterId: Int) extends BaseMetrikaRequest

  case class MetrikaLogGet(accessToken: String, counterId: Int, requestId: Int) extends BaseMetrikaRequest

  case class MetrikaLogCreate(accessToken: String, counterId: Int, date1: String, date2: String, fields: List[String], source: Source) extends BaseMetrikaRequest

  case class MetrikaLogStreamPart(accessToken: String, counterId: Int, requestId: Int, partNumber: Int) extends BaseMetrikaRequest

  implicit val logRequestPartDecoder: Decoder[LogRequestPart] = deriveConfiguredDecoder[LogRequestPart]
  implicit val logRequestPartEncoder: Encoder[LogRequestPart] = deriveConfiguredEncoder[LogRequestPart]

  implicit val logRequestDecoder: Decoder[MetrikaLogRequest] = deriveConfiguredDecoder[MetrikaLogRequest]
  implicit val logRequestEncoder: Encoder[MetrikaLogRequest] = deriveConfiguredEncoder[MetrikaLogRequest]

  implicit val logRequestResponseDecoder: Decoder[LogRequestResponse] = deriveConfiguredDecoder[LogRequestResponse]
  implicit val logRequestResponseEncoder: Encoder[LogRequestResponse] = deriveConfiguredEncoder[LogRequestResponse]

  implicit val logRequestsResponseDecoder: Decoder[LogRequestsResponse] = deriveConfiguredDecoder[LogRequestsResponse]
  implicit val logRequestsResponseEncoder: Encoder[LogRequestsResponse] = deriveConfiguredEncoder[LogRequestsResponse]

  implicit val decodeSource: Decoder[Source] = (c: HCursor) => c.as[String].flatMap(source => {
    source.toLowerCase() match {
      case "visits" => Right(Visits)
      case "hits" => Right(Hits)
      case _ => Left(DecodingFailure("Unknown Source has been passed", c.history))
    }
  })
  implicit val encodeSource: Encoder[Source] = (a: Source) => a.toString.asJson

  implicit val decodeStatus: Decoder[LogRequestStatus] = (c: HCursor) => c.as[String].flatMap(status => {
    status.toLowerCase() match {
      case "processed" => Right(Processed)
      case "canceled" => Right(Canceled)
      case "processing_failed" => Right(ProcessingFailed)
      case "created" => Right(Created)
      case "cleaned_by_user" => Right(CleanedByUser)
      case "cleaned_automatically_as_too_old" => Right(CleanedAutomaticallyAsTooOld)
      case _ => Left(DecodingFailure("Unknown status has been passed", c.history))
    }
  })
  implicit val encodeStatus: Encoder[LogRequestStatus] = (a: LogRequestStatus) => a.toString.asJson
}
