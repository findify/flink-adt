name := "flink-adt"

version := "0.5.0"

lazy val `scala 2.12` = "2.12.15"
lazy val `scala 2.13` = "2.13.8"

scalaVersion := `scala 2.13`
crossScalaVersions := Seq(`scala 2.12`, `scala 2.13`)

organization := "io.findify"
licenses := Seq("MIT" -> url("https://opensource.org/licenses/MIT"))
homepage := Some(url("https://github.com/findify/flink-adt"))

publishMavenStyle := true

publishTo := sonatypePublishToBundle.value

lazy val flinkVersion = "1.15.0"

libraryDependencies ++= Seq(
  "org.apache.flink"              % "flink-java"       % flinkVersion % "provided",
  "org.apache.flink"              % "flink-test-utils" % flinkVersion % "test",
  "org.scalatest"                %% "scalatest"        % "3.2.12"     % "test",
  "org.typelevel"                %% "cats-core"        % "2.7.0"      % "test",
  "com.softwaremill.magnolia1_2" %% "magnolia"         % "1.1.2",
  "org.scala-lang"                % "scala-reflect"    % scalaVersion.value
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
