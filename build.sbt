import Dependencies._

ThisBuild / scalaVersion     := "2.12.8"
ThisBuild / version          := "0.0.1"
ThisBuild / organization     := "com.github.sobreera"

lazy val root = (project in file("."))
  .settings(
    name := "spark-example",
    libraryDependencies += scalaTest % Test,
    libraryDependencies += "org.apache.spark" %% "spark-core" % "2.4.3",
    libraryDependencies += "org.apache.spark" %% "spark-mllib" % "2.4.3",
    libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.4.3"
  )