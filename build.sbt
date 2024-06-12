ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.14"

lazy val root = (project in file("."))
  .settings(
    name := "CurrentTimeAPI"
  )

libraryDependencies ++= Seq(
  "com.lihaoyi" %% "cask" % "0.9.2",
  "com.lihaoyi" %% "upickle" % "3.3.1"
)