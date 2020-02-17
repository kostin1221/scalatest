package statistics.metrika.db

import doobie.implicits._

object RequestStatusStorage {
  sealed trait Status
  case object Pending extends Status
  case object Success extends Status
  case object Failed extends Status

  case class RequestStatus(id: Long, request_id: Long, status: Status)

  def insert(request: RequestStatus): doobie.ConnectionIO[RequestStatus] = {
    sql"insert into request_statuses (request_id, status) values (${request.request_id}, ${request.status})"
      .update
      .withUniqueGeneratedKeys[RequestStatus]("id", "request_id", "status")
  }
}
