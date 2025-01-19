ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / evictionErrorLevel := Level.Info
ThisBuild / scalaVersion := "2.13.8"

lazy val root = (project in file("."))
  .settings(
    name := "click-stream-spark-analytics"
  )
// https://mvnrepository.com/artifact/org.apache.spark/spark-mllib
libraryDependencies += "org.apache.spark" %% "spark-mllib" % "3.5.3" % "provided"

libraryDependencies += "org.apache.spark" %% "spark-sql" % "3.5.3"// https://mvnrepository.com/artifact/com.google.cloud.spark/spark-bigquery-with-dependencies
libraryDependencies += "com.google.cloud.spark" %% "spark-bigquery-with-dependencies" % "0.41.1" % "provided"

