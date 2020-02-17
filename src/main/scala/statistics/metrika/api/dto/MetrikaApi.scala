package statistics.metrika.api.dto

import io.circe._, io.circe.generic.semiauto._
import io.circe.generic.extras._, io.circe.syntax._

object MetrikaApi {
  implicit val config: Configuration = Configuration.default.withSnakeCaseMemberNames

  sealed trait RequestStatus
  case object Beginner extends RequestStatus
  case object Intermediate extends RequestStatus
  case object Advanced extends RequestStatus
  case class Error(value: String)

  object Level {
    def fromString(value: String): Either[Error, RequestStatus] =
      value.toLowerCase match {
        case "beginner" => Right(Beginner)
        case "intermediate" => Right(Intermediate)
        case "advanced" => Right(Advanced)
        case _ => Left(Error("Unknown String"))
      }
  }


  case class LogRequestPart(partNumber: Int,
                                                 size: Int
                                                )

  case class MetrikaLogRequest(requestId: Int,
                                                    counterId: Int,
                                                    source: String,
                                                    fields: String,
                                                    status: String,
                                                    size: Int,
                                                    parts: List[LogRequestPart])


  implicit val logRequestPartDecoder: Decoder[LogRequestPart] = deriveDecoder[LogRequestPart]
  implicit val logRequestPartEncoder: Encoder[LogRequestPart] = deriveEncoder[LogRequestPart]

  implicit val logRequestDecoder: Decoder[MetrikaLogRequest] = deriveDecoder[MetrikaLogRequest]
  implicit val logRequestEncoder: Encoder[MetrikaLogRequest] = deriveEncoder[MetrikaLogRequest]
}
