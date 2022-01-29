ThisBuild / organization := "com.example"
ThisBuild / scalaVersion := "3.1.0"

lazy val root = (project in file(".")).settings(
  name := "fs2-worker-pool",
  libraryDependencies ++= Seq(
    "org.typelevel" %% "cats-effect"          % "3.3.3",
    "org.typelevel" %% "cats-effect-kernel"   % "3.3.3",
    "org.typelevel" %% "cats-effect-std"      % "3.3.3",
    "co.fs2"        %% "fs2-core"             % "3.2.4",
    "co.fs2"        %% "fs2-reactive-streams" % "3.2.4",
    "co.fs2"        %% "fs2-io"               % "3.2.4",
    "co.fs2"        %% "fs2-scodec"           % "3.2.4",
    "org.typelevel" %% "log4cats-slf4j"       % "2.2.0",
    "org.slf4j"      % "slf4j-simple"         % "1.7.35",
    "org.scalatest" %% "scalatest"            % "3.2.11" % Test,
  ),
)
