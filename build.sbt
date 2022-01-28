ThisBuild / organization := "com.example"
ThisBuild / scalaVersion := "3.1.0"

lazy val root = (project in file(".")).settings(
  name := "worker-pull",
  libraryDependencies ++= Seq(
    // "core" module - IO, IOApp, schedulers
    // This pulls in the kernel and std modules automatically.
    "org.typelevel" %% "cats-effect" % "3.3.3",
    // concurrency abstractions and primitives (Concurrent, Sync, Async etc.)
    "org.typelevel" %% "cats-effect-kernel" % "3.3.3",
    // standard "effect" library (Queues, Console, Random etc.)
    "org.typelevel" %% "cats-effect-std" % "3.3.3",
    // https://mvnrepository.com/artifact/co.fs2/fs2-core
    "co.fs2" %% "fs2-core" % "3.2.4",
    // https://mvnrepository.com/artifact/co.fs2/fs2-cats
    //"co.fs2" %% "fs2-cats"             % "0.5.0",
    "co.fs2"        %% "fs2-reactive-streams" % "3.2.4",
    "co.fs2"        %% "fs2-io"               % "3.2.4",
    "co.fs2"        %% "fs2-scodec"           % "3.2.4",
    "org.typelevel" %% "log4cats-slf4j"       % "2.2.0",
  ),
)
