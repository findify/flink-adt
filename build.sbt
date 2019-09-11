name := "flink-adt"

version := "0.1-M1"

scalaVersion := "2.12.10"
crossScalaVersions := Seq("2.12.10", "2.11.12")

organization := "io.findify"
licenses := Seq("MIT" -> url("https://opensource.org/licenses/MIT"))
homepage := Some(url("https://github.com/findify/flink-adt"))

publishMavenStyle := true
bintrayOrganization := Some("findify")


lazy val flinkVersion = "1.9.0"

libraryDependencies ++= Seq(
  "com.propensive"                   %% "magnolia"                   % "0.11.0" exclude ("org.scala-lang", "scala-compiler"),
  "org.apache.flink"          %% "flink-scala"                % flinkVersion % "provided" exclude ("org.scala-lang", "scala-compiler"),
  "org.apache.flink"          %% "flink-streaming-scala"      % flinkVersion % "provided" exclude ("org.scala-lang", "scala-compiler"),
  "org.scalatest" %% "scalatest" % "3.0.8" % "test"
)