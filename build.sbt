name := "flink-adt"

version := "0.4.5"

scalaVersion := "2.12.15"

organization := "io.findify"
licenses := Seq("MIT" -> url("https://opensource.org/licenses/MIT"))
homepage := Some(url("https://github.com/findify/flink-adt"))

publishMavenStyle := true

publishTo := sonatypePublishToBundle.value

// Use snapshot of Flink 1.15 for now.
// Otherwise impossible to import `flink-test-utils` without transitive Scala dependency.
resolvers += "Apache Snapshots" at "https://repository.apache.org/content/repositories/snapshots/"
lazy val flinkVersion = "1.15-20220128.014046-92"

libraryDependencies ++= Seq(
  "com.softwaremill.magnolia1_2" %% "magnolia" % "1.0.0-M8",
  "org.apache.flink" % "flink-java" % flinkVersion % "provided",
  "org.apache.flink" % "flink-test-utils" % flinkVersion % "test",
  "org.scalatest" %% "scalatest" % "3.2.10" % "test",
  "org.typelevel" %% "cats-core" % "2.7.0" % "test",
  "org.scala-lang" % "scala-reflect" % scalaVersion.value
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
