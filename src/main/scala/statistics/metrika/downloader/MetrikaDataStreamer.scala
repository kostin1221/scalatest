package statistics.metrika.downloader

import cats.effect._
import com.typesafe.scalalogging.LazyLogging
import doobie.hikari.HikariTransactor
import statistics.metrika.api.MetrikaClient
import statistics.metrika.api.dto.FetcherApi.DownloadStatisticsTask
import statistics.metrika.api.dto.MetrikaApi._
import statistics.metrika.db.DbStreamer

object MetrikaDataStreamer extends LazyLogging {

  def createMetrikaTaskAndStreamToDb(
    downloadStatisticsTask: DownloadStatisticsTask
  )(implicit metrikaClient: MetrikaClient,
    cs: ContextShift[IO],
    timer: Timer[IO],
    transactor: HikariTransactor[IO]): IO[Int] = {
    for {
      stream <- createTaskAndStreamWhenReady(downloadStatisticsTask)
      updated <- DbStreamer.streamToDb(stream)
    } yield updated
  }

  def createTaskAndStreamWhenReady(
    downloadStatisticsTask: DownloadStatisticsTask
  )(implicit metrikaClient: MetrikaClient,
    cs: ContextShift[IO],
    timer: Timer[IO],
    transactor: HikariTransactor[IO])
    : IO[fs2.Stream[IO, (Array[String], Array[String])]] = {
    for {
      getLogRequest <- MetrikaTaskCreator
        .createMetrikaTask(downloadStatisticsTask)
        .map(
          createdLogRequest =>
            mkGetLogRequest(downloadStatisticsTask, createdLogRequest)
        )

//       requestStatus <- RequestStatusStorage.insert(
//         RequestStatusStorage.RequestStatus(getLogRequest.requestId, RequestStatusStorage.Pending)
//       )

      preparedLogRequest <- MetrikaTaskProgress.getLogRequestWhenReady(
        getLogRequest
      )

      stream <- streamData(downloadStatisticsTask, preparedLogRequest)

    } yield (stream)
  }

  private def streamData(
    downloadStatisticsTask: DownloadStatisticsTask,
    metrikaLogRequest: MetrikaLogRequest
  )(implicit metrikaClient: MetrikaClient, cs: ContextShift[IO]) = {

    val listOfResponses = metrikaLogRequest.parts.get.map(part => {
      val streamPartRequest = MetrikaLogStreamPart(
        downloadStatisticsTask.accessToken,
        downloadStatisticsTask.counterId,
        metrikaLogRequest.requestId,
        part.partNumber
      )
      metrikaClient.streamLogRequestPartData(streamPartRequest)
    })

    listOfResponses.reduce((streamIO1, streamIO2) => {
      for {
        stream1 <- streamIO1
        stream2 <- streamIO2
        mergedStream <- IO.pure(stream1 ++ stream2)
      } yield mergedStream
    })
  }

  private def mkGetLogRequest(downloadStatisticsTask: DownloadStatisticsTask,
                              createdLogRequest: MetrikaLogRequest) = {
    MetrikaLogGet(
      downloadStatisticsTask.accessToken,
      downloadStatisticsTask.counterId,
      createdLogRequest.requestId
    )
  }
}
