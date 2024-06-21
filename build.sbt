ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.14"

val circeVersion = "0.14.1"

lazy val root = (project in file("."))
  .settings(
    name := "CurrentTimeAPI"
  )

libraryDependencies ++= Seq(
  "com.lihaoyi" %% "cask" % "0.9.2",
  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,
  "com.lihaoyi" %% "requests" % "0.8.3",
  "com.lihaoyi" %% "utest" % "0.8.3" % "test"
)

testFrameworks += new TestFramework("utest.runner.Framework")
