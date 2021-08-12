
name := "metrika-statistics-fetcher"

version := "0.1"

scalaVersion := "2.13.1"

val circeVersion = "0.13.0"
val sttVersion = "2.0.0-RC9"
val ScalaLogging = "3.9.2"
val slfVersion = "1.7.19"
val Logstash = "1.2.3"
val http4sVersion = "0.21.0"

libraryDependencies += "com.softwaremill.sttp.client" %% "core" % sttVersion
libraryDependencies += "com.softwaremill.sttp.client" %% "async-http-client-backend-cats" % sttVersion
//libraryDependencies += "com.softwaremill.sttp.client" %% "http4s-backend" % sttVersion
libraryDependencies += "com.softwaremill.sttp.client" %% "async-http-client-backend-fs2" % sttVersion
libraryDependencies += "com.softwaremill.sttp.client" %% "circe" % sttVersion

libraryDependencies += "io.circe" %% "circe-generic" % circeVersion
libraryDependencies += "io.circe" %% "circe-parser" % circeVersion
libraryDependencies += "io.circe" %% "circe-generic-extras" % circeVersion

libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % ScalaLogging
libraryDependencies += "org.slf4j" % "slf4j-api" % slfVersion
libraryDependencies += "ch.qos.logback" % "logback-classic" % Logstash

libraryDependencies += "org.http4s" %% "http4s-dsl" % http4sVersion
libraryDependencies += "org.http4s" %% "http4s-blaze-server" % http4sVersion
libraryDependencies += "org.http4s" %% "http4s-circe" % http4sVersion

//addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)

libraryDependencies += "io.scalaland" %% "chimney" % "0.4.1"

libraryDependencies ++= Seq(
  // Start with this one
  "org.tpolecat" %% "doobie-core"      % "0.8.8",

  // And add any of these as needed
//  "org.tpolecat" %% "doobie-h2"        % "0.8.8",          // H2 driver 1.4.200 + type mappings.
  "org.tpolecat" %% "doobie-hikari"    % "0.8.8",          // HikariCP transactor.
  "org.tpolecat" %% "doobie-postgres"  % "0.8.8",          // Postgres driver 42.2.9 + type mappings.
//  "org.tpolecat" %% "doobie-quill"     % "0.8.8",          // Support for Quill 3.4.10
//  "org.tpolecat" %% "doobie-specs2"    % "0.8.8" % "test", // Specs2 support for typechecking statements.
//  "org.tpolecat" %% "doobie-scalatest" % "0.8.8" % "test"  // ScalaTest support for typechecking statements.
)

addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.11.0" cross CrossVersion.full)

libraryDependencies += "org.typelevel" %% "cats-effect" % "2.1.2"
