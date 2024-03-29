name := """bitcoin-bot"""
organization := "com.bitbot"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.3"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test
libraryDependencies += "com.netflix.hystrix" % "hystrix-core" % "1.5.12"
libraryDependencies += ws
libraryDependencies += "com.typesafe.play" %% "play-json-joda" % "2.6.8"
libraryDependencies += "com.typesafe.play" %% "play-json" % "2.6.8"
libraryDependencies += "io.reactivex" %% "rxscala" % "0.26.5"
libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.2.18"

val jacksonVersion = "2.9.2"

libraryDependencies ++= Seq(
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % jacksonVersion,
  "com.fasterxml.jackson.datatype" % "jackson-datatype-joda" % jacksonVersion
)

libraryDependencies ++= Seq(
  "org.scalamock" %% "scalamock" % "4.0.0" % Test,
  "org.mockito" % "mockito-core" % "2.13.0" % Test
)

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.bitbot.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.bitbot.binders._"
