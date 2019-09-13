name := "flink-adt"

version := "0.1-M10"

scalaVersion := "2.12.10"
crossScalaVersions := Seq("2.12.10", "2.11.12")

organization := "io.findify"
licenses := Seq("MIT" -> url("https://opensource.org/licenses/MIT"))
homepage := Some(url("https://github.com/findify/flink-adt"))

publishMavenStyle := true
bintrayOrganization := Some("findify")

bintrayVcsUrl := Some("git@github.com:findify/flink-adt.git")
lazy val flinkVersion = "1.9.0"

libraryDependencies ++= Seq(
  "com.propensive"                   %% "magnolia"                   % "0.11.0",
  "org.apache.flink"          %% "flink-scala"                % flinkVersion % "provided",
  "org.apache.flink"          %% "flink-streaming-scala"      % flinkVersion % "provided",
  "org.scalatest" %% "scalatest" % "3.0.8" % "test",
  "org.typelevel" %% "cats-core" % "1.6.1"
)