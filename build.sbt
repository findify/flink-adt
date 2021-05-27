name := "flink-adt"

version := "0.3.0-M2"

scalaVersion := "2.12.13"

organization := "io.findify"
licenses := Seq("MIT" -> url("https://opensource.org/licenses/MIT"))
homepage := Some(url("https://github.com/findify/flink-adt"))

publishMavenStyle := true

publishTo := sonatypePublishToBundle.value

lazy val flinkVersion = "1.13.0"

libraryDependencies ++= Seq(
  "com.propensive"                   %% "magnolia"                   % "0.17.0",
  "org.apache.flink"          %% "flink-scala"                % flinkVersion % "provided",
  "org.apache.flink"          %% "flink-streaming-scala"      % flinkVersion % "provided",
  "org.scalatest" %% "scalatest" % "3.0.9" % "test"
)

scmInfo := Some(
  ScmInfo(
    url("https://github.com/findify/flink-adt"),
    "scm:git@github.com:findify/flink-adt.git"
  )
)
developers := List(
  Developer(id = "romangrebennikov", name = "Roman Grebennikov", email = "grv@dfdx.me", url = url("https://dfdx.me/"))
)
