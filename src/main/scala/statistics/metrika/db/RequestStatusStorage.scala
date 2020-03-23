package statistics.metrika.db

import doobie.implicits._
import doobie.util._

object RequestStatusStorage {

  def statusFromString(rawStatus: String): Status = rawStatus match {
    case "pending" => Pending
    case "success" => Success
    case "failed"  => Failed
  }

  def statusToString(status: Status): String = status match {
    case Pending => "pending"
    case Success => "success"
    case Failed  => "failed"
  }

  def insert(request: RequestStatus): doobie.ConnectionIO[RequestStatus] = {
    sql"insert into request_statuses (request_id, status) values (${request.request_id}, ${request.status})".update
      .withUniqueGeneratedKeys[RequestStatus]("id", "request_id", "status")
  }

  sealed trait Status

  case class RequestStatus(id: Long, request_id: Long, status: Status)

  case object Pending extends Status

  implicit val statusGet: Get[Status] = Get[String].map(statusFromString)
  implicit val statusPut: Put[Status] = Put[String].contramap(statusToString)

  case object Success extends Status

  case object Failed extends Status
}
