package statistics.metrika

import cats.effect._
import sttp.client._
import cats.syntax.all._
import sttp.client.asynchttpclient.fs2.AsyncHttpClientFs2Backend
import java.nio.ByteBuffer

import cats.effect.{ContextShift, IO}
import fs2.Stream

import scala.concurrent.ExecutionContext

import cats.effect.{IO, Resource}
import cats.implicits._
import java.io._

object Main extends App {
//  implicit val sttpBackend = AsyncHttpClientFs2Backend()

  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.Implicits.global)
//  val effect = AsyncHttpClientFs2Backend[IO]().flatMap { implicit backend =>
//    val stream: Stream[IO, ByteBuffer] = ...
//
//    basicRequest
//      .streamBody(stream)
//      .post(uri"...")
//  }

  def inputStream(f: File): Resource[IO, FileInputStream] =
    Resource.make {
      IO(new FileInputStream(f))                         // build
    } { inStream =>
      IO(inStream.close()).handleErrorWith(_ => IO.unit) // release
    }

  def outputStream(f: File): Resource[IO, FileOutputStream] =
    Resource.make {
      IO(new FileOutputStream(f))                         // build
    } { outStream =>
      IO(outStream.close()).handleErrorWith(_ => IO.unit) // release
    }

  def inputOutputStreams(in: File, out: File): Resource[IO, (InputStream, OutputStream)] =
    for {
      inStream  <- inputStream(in)
      outStream <- outputStream(out)
    } yield (inStream, outStream)

//    args.headOption match {
//      case Some(name) =>
//        IO(println(s"Hello, $name.")).as(ExitCode.Success)
//      case None =>
//        IO(System.err.println("Usage: MyApp name")).as(ExitCode(2))
//    }
//    sys.addShutdownHook(argos.stop())
}
