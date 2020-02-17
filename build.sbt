
name := "metrika-statistics-fetcher"

version := "0.1"

scalaVersion := "2.13.1"

val circeVersion = "0.13.0"
val sttVersion = "2.0.0-RC9"
val ScalaLogging = "3.9.2"

libraryDependencies += "com.softwaremill.sttp.client" %% "core" % sttVersion
libraryDependencies += "com.softwaremill.sttp.client" %% "async-http-client-backend-cats" % sttVersion
libraryDependencies += "com.softwaremill.sttp.client" %% "http4s-backend" % sttVersion
libraryDependencies += "com.softwaremill.sttp.client" %% "async-http-client-backend-fs2" % sttVersion
libraryDependencies += "com.softwaremill.sttp.client" %% "circe" % sttVersion

libraryDependencies += "io.circe" %% "circe-generic" % circeVersion
libraryDependencies += "io.circe" %% "circe-parser" % circeVersion
libraryDependencies += "io.circe" %% "circe-generic-extras" % circeVersion

libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % ScalaLogging

