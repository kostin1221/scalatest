package statistics.metrika.api.dto

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import statistics.metrika.api.dto.MetrikaApi._
import statistics.metrika.db.MetrikaFieldsMap

object FetcherApi {

  case class DownloadStatisticsTask(accessToken: String,
                                    counterId: Int,
                                    dateFrom: String,
                                    dateTo: String)

  implicit def task2MetrikaRequest(
    downloadStatisticsTask: DownloadStatisticsTask
  ): MetrikaLogCreate =
    MetrikaLogCreate(
      downloadStatisticsTask.accessToken,
      downloadStatisticsTask.counterId,
      downloadStatisticsTask.dateFrom,
      downloadStatisticsTask.dateTo,
      MetrikaFieldsMap.getAllMetrikaFields,
      Visits
    )

  implicit val downloadStatisticsTaskDecoder: Decoder[DownloadStatisticsTask] =
    deriveDecoder[DownloadStatisticsTask]
  implicit val downloadStatisticsTaskEncoder: Encoder[DownloadStatisticsTask] =
    deriveEncoder[DownloadStatisticsTask]

}
