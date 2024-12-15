ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.12.18"

lazy val root = (project in file("."))
  .settings(
    name := "click-stream-spark-analytics"
  )

libraryDependencies += "org.apache.spark" %% "spark-sql" % "3.5.3"
