package statistics.metrika.db

import doobie.implicits._
import doobie.util._
import statistics.metrika.api.dto.MetrikaApi.{Canceled, CleanedAutomaticallyAsTooOld, CleanedByUser, Created, Processed, ProcessingFailed}

object RequestStatusStorage {
  sealed trait Status
  case object Pending extends Status
  case object Success extends Status
  case object Failed extends Status

  def statusFromString(rawStatus: String): Status = rawStatus match {
    case "pending" => Pending
    case "success" => Success
    case "failed" => Failed
  }
  def statusToString(status: Status): String = status match {
    case Pending => "pending"
    case Success => "success"
    case Failed => "failed"
  }

  implicit val statusGet: Get[Status] = Get[String].map(statusFromString)
  implicit val statusPut: Put[Status] = Put[String].contramap(statusToString)

  case class RequestStatus(id: Long, request_id: Long, status: Status)

  def insert(request: RequestStatus): doobie.ConnectionIO[RequestStatus] = {
    sql"insert into request_statuses (request_id, status) values (${request.request_id}, ${request.status})"
      .update
      .withUniqueGeneratedKeys[RequestStatus]("id", "request_id", "status")
  }
}
