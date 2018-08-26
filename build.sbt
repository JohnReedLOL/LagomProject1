organization in ThisBuild := "com.github.johnreedlol"
version in ThisBuild := "1.0-SNAPSHOT"

// the Scala version that will be used for cross-compiled libraries
scalaVersion in ThisBuild := "2.12.4"

val macwire = "com.softwaremill.macwire" %% "macros" % "2.3.0" % "provided"
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.4" % Test

lazy val `project1` = (project in file("."))
  .aggregate(`project1-api`, `project1-impl`, `project1-stream-api`, `project1-stream-impl`)

lazy val `project1-api` = (project in file("project1-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )

lazy val `project1-impl` = (project in file("project1-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      lagomScaladslKafkaBroker,
      lagomScaladslTestKit,
      macwire,
      scalaTest
    )
  )
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`project1-api`)

lazy val `project1-stream-api` = (project in file("project1-stream-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )

lazy val `project1-stream-impl` = (project in file("project1-stream-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslTestKit,
      macwire,
      scalaTest
    )
  )
  .dependsOn(`project1-stream-api`, `project1-api`)

// https://stackoverflow.com/questions/15624236/is-there-a-way-in-sbt-to-convert-compiler-warnings-to-errors-so-the-build-fails
// scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-Xfatal-warnings")

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-Xlint", "-Ywarn-inaccessible", "-Ywarn-nullary-override", "-Ywarn-nullary-unit", "-Xfatal-warnings")
