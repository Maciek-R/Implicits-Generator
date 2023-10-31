ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val commonSettings = Seq(
  scalacOptions ++= Seq(
    "-Ymacro-annotations",
    "-Ywarn-macros:after"
  )
)

lazy val root = (project in file("."))
  .settings(commonSettings)
  .settings(
    name := "MacrosGenerator"
  )
  .aggregate(macros)
  .aggregate(app)

lazy val macros = project
  .settings(commonSettings)
  .settings(
    name := "macros",
    libraryDependencies ++= Seq(
      "com.chuusai" %% "shapeless" % "2.3.3",
      "org.typelevel" %% "cats-effect" % "2.5.3",
      "org.scala-lang" % "scala-reflect" % "2.13.10",
      "org.scalatest" %% "scalatest" % "3.2.15" % Test,
      "org.scalatestplus" %% "mockito-4-11" % "3.2.17.0" % Test
    )
  )

lazy val app = project
  .settings(commonSettings)
  .settings(
    name := "app",
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core" % "0.14.6",
      "io.circe" %% "circe-generic" % "0.14.6"
    )
  )
  .dependsOn(macros)