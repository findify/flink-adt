name := "flink-adt"

version := "0.5.0"

lazy val `scala 2.12` = "2.12.15"
lazy val `scala 2.13` = "2.13.8"
lazy val `scala 3`    = "3.1.2"

scalaVersion := `scala 2.13`
crossScalaVersions := Seq(`scala 2.12`, `scala 2.13`)

organization := "io.findify"
licenses := Seq("MIT" -> url("https://opensource.org/licenses/MIT"))
homepage := Some(url("https://github.com/findify/flink-adt"))

publishMavenStyle := true

publishTo := sonatypePublishToBundle.value

// Need to isolate macro usage to version-specific folders.
Compile / unmanagedSourceDirectories += {
  val dir              = (Compile / scalaSource).value.getPath
  val Some((major, _)) = CrossVersion.partialVersion(scalaVersion.value)
  file(s"$dir-$major")
}

scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-language:higherKinds"
)

lazy val flinkVersion = "1.15.0"

libraryDependencies ++= Seq(
  "org.apache.flink" % "flink-java"       % flinkVersion % Provided,
  "org.apache.flink" % "flink-test-utils" % flinkVersion % Test,
  "org.scalatest"   %% "scalatest"        % "3.2.12"     % Test,
  "org.typelevel"   %% "cats-core"        % "2.7.0"      % Test
)

// Macro libraries are based on major version.
libraryDependencies ++= {
  if (scalaBinaryVersion.value.startsWith("2")) {
    Seq(
      "com.softwaremill.magnolia1_2" %% "magnolia"      % "1.1.2",
      "org.scala-lang"                % "scala-reflect" % scalaVersion.value % Provided
    )
  } else {
    Seq(
      "com.softwaremill.magnolia1_3" %% "magnolia"        % "1.1.1",
      "org.scala-lang"               %% "scala3-compiler" % scalaVersion.value % Provided
    )
  }
}

scmInfo := Some(
  ScmInfo(
    url("https://github.com/findify/flink-adt"),
    "scm:git@github.com:findify/flink-adt.git"
  )
)
developers := List(
  Developer(id = "romangrebennikov", name = "Roman Grebennikov", email = "grv@dfdx.me", url = url("https://dfdx.me/"))
)
