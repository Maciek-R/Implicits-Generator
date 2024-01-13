ThisBuild / version := "1.0.0"
ThisBuild / scalaVersion := "2.13.10"
ThisBuild / organization := "mrlibs"

lazy val commonSettings = Seq(
  scalacOptions ++= Seq(
    "-Ymacro-annotations",
    "-Ywarn-macros:after"
  ),
  publishTo := {
    Some("mrlibs" at "https://maven.cloudsmith.io/mrlibs/implicits-organizer/")
  },
  pomIncludeRepository := { _ => false },
  publishMavenStyle := true
)

lazy val root = (project in file("."))
  .settings(commonSettings)
  .settings(name := "ImplicitsOrganizer", publish / skip := true)
  .aggregate(implicitsOrganizerMacros)

lazy val implicitsOrganizerMacros = project
  .settings(commonSettings)
  .settings(
    name := "implicits-organizer-macros",
    libraryDependencies ++= Seq(
      "com.chuusai" %% "shapeless" % "2.3.3",
      "org.typelevel" %% "cats-effect" % "2.5.3",
      "org.scala-lang" % "scala-reflect" % "2.13.10",
      "com.softwaremill.magnolia1_2" %% "magnolia" % "1.1.8",
      "org.scalatest" %% "scalatest" % "3.2.15" % Test,
      "org.scalatestplus" %% "mockito-4-11" % "3.2.17.0" % Test
    ),
    publishConfiguration := publishConfiguration.value.withOverwrite(true)
  )

lazy val implicitsOrganizerMacrosTest = project
  .settings(commonSettings)
  .settings(
    name := "implicits-organizer-macros-test"
  )
  .dependsOn(implicitsOrganizerMacros)
  .dependsOn(implicitsOrganizerMacros % "test->test")
