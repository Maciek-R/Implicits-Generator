ThisBuild / version := "1.0.12"
ThisBuild / scalaVersion := "2.13.12"
ThisBuild / organization := "io.github.maciek-r"
ThisBuild / organizationName := "ruszczyk.maciek"

ThisBuild / scmInfo := Some(
  ScmInfo(url("https://github.com/Maciek-R/Implicits-Generator"), "scm:git@github.Maciek-R/Implicits-Generator.git")
)

ThisBuild / developers := List(
  Developer(
    id = "Maciek-R",
    name = "Maciek-R",
    email = "maciek3633@gmail.com",
    url = url("https://github.com/Maciek-R")
  )
)

ThisBuild / description := "Implicit Generator Macros"
ThisBuild / licenses := List("MIT" -> new URI("https://opensource.org/license/mit/").toURL)
ThisBuild / homepage := Some(url("https://github.com/Maciek-R/Implicits-Generator"))

ThisBuild / versionScheme := Some("pvp")

ThisBuild / publishTo := {
  val nexus = "https://s01.oss.sonatype.org/"
  if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
  else Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

lazy val commonSettings = Seq(
  scalacOptions ++= Seq(
    "-Ymacro-annotations",
    "-Ywarn-macros:after"
  )
)

lazy val root = (project in file("."))
  .settings(commonSettings)
  .settings(
    name := "ImplicitsGenerator",
    publish / skip := true,
    pomIncludeRepository := { _ => false },
    publishMavenStyle := true
  )
  .aggregate(implicitsGeneratorMacros)

lazy val implicitsGeneratorMacros = project
  .settings(commonSettings)
  .settings(
    name := "implicits-generator-macros",
    libraryDependencies ++= Seq(
      "com.chuusai" %% "shapeless" % "2.3.10",
      "org.typelevel" %% "cats-effect" % "3.4.8",
      "org.scala-lang" % "scala-reflect" % "2.13.10",
      "com.softwaremill.magnolia1_2" %% "magnolia" % "1.1.8",
      "org.scalatest" %% "scalatest" % "3.2.15" % Test,
      "org.scalatestplus" %% "mockito-4-11" % "3.2.17.0" % Test
    ),
    publishConfiguration := publishConfiguration.value.withOverwrite(true)
  )

lazy val implicitsGeneratorMacrosTest = project
  .settings(commonSettings)
  .settings(
    name := "implicits-generator-macros-test"
  )
  .dependsOn(implicitsGeneratorMacros)
  .dependsOn(implicitsGeneratorMacros % "test->test")
