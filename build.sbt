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


// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.bitbot.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.bitbot.binders._"
