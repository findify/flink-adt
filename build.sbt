name := "flink-adt"

version := "0.2"

scalaVersion := "2.12.10"

organization := "io.findify"
licenses := Seq("MIT" -> url("https://opensource.org/licenses/MIT"))
homepage := Some(url("https://github.com/findify/flink-adt"))

publishMavenStyle := true
//bintrayOrganization := Some("findify")

//bintrayVcsUrl := Some("git@github.com:findify/flink-adt.git")

publishTo := sonatypePublishToBundle.value

lazy val flinkVersion = "1.9.0"

libraryDependencies ++= Seq(
  "com.propensive"                   %% "magnolia"                   % "0.12.0",
  "org.apache.flink"          %% "flink-scala"                % flinkVersion % "provided",
  "org.apache.flink"          %% "flink-streaming-scala"      % flinkVersion % "provided",
  "org.scalatest" %% "scalatest" % "3.0.8" % "test",
  "org.typelevel" %% "cats-core" % "1.6.1"
)

pomExtra := (
  <scm>
    <url>git@github.com:findify/flink-adt.git</url>
    <connection>scm:git:git@github.com:findify/flink-adt.git</connection>
  </scm>
    <developers>
      <developer>
        <id>romangrebennikov</id>
        <name>Roman Grebennikov</name>
        <url>http://www.dfdx.me</url>
      </developer>
    </developers>)