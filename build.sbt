name := "flink-adt"

version := "0.4.3"

scalaVersion := "2.12.15"

organization := "io.findify"
licenses := Seq("MIT" -> url("https://opensource.org/licenses/MIT"))
homepage := Some(url("https://github.com/findify/flink-adt"))

publishMavenStyle := true

publishTo := sonatypePublishToBundle.value

lazy val flinkVersion = "1.13.2"

libraryDependencies ++= Seq(
  "com.softwaremill.magnolia1_2" % "magnolia_2.12"         % "1.0.0-M5",
  "org.apache.flink"            %% "flink-scala"           % flinkVersion % "provided",
  "org.apache.flink"            %% "flink-streaming-scala" % flinkVersion % "provided",
  "org.apache.flink"            %% "flink-test-utils"      % flinkVersion % "test",
  "org.scalatest"               %% "scalatest"             % "3.2.10"     % "test",
  "org.typelevel"               %% "cats-core"             % "2.6.1"      % "test"
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

publishLocalConfiguration / publishMavenStyle := true
