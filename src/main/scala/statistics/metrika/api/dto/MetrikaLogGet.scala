package statistics.metrika.api.dto


sealed abstract class BaseMetrikaRequest(accessToken: String)

case class MetrikaLogGet(accessToken: String, counterId: Int) extends BaseMetrikaRequest(accessToken)

