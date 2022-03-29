ThisBuild / name := "lunatech-bootiful-zio"
ThisBuild / organization := "com.lunatech"
ThisBuild / scalaVersion := "2.13.8"
ThisBuild / version := "0.0.1"
ThisBuild / description := "Sample project showing how ZIO ZLayer can be used to achieve a clean separation of concerns"

lazy val root = (project in file("."))
  .settings(
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio" % "2.0.0-RC3",
      "dev.zio" %% "zio-test" % "2.0.0-RC3" % Test
    ),
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )
