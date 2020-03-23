package statistics.metrika.downloader

object Exceptions {
  class DownloaderError(message: String) extends Exception(message)

  class LogRequestIsInWrongStatus(message: String)
      extends DownloaderError(message)
}
