package tjl

import cats.effect.{ExitCode, IO, IOApp, Sync}
import cats.implicits.*
import tjl.*
import fs2.*
import cats.effect.IO
import fs2.*

import scala.concurrent.duration.*
import cats.implicits.*
import cats.*
import cats.*
import cats.data.*
import cats.implicits.*
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

import java.util.concurrent.{ExecutorService, Executors}
import scala.concurrent.ExecutionContext

object Main extends IOApp.Simple {

  val loggingEc: ExecutionContext = ExecutionContext.fromExecutor(Executors.newWorkStealingPool())
  val timeout                     = 30.seconds
  val workers                     = 50

  def run: IO[Unit] = {
    for {
      logger <- Slf4jLogger.create[IO]
      _      <- logger.warn("Logging start")
      s = new Streaming[IO](loggingEc).stream
      x <- s.compile.drain
    } yield ExitCode.Success
  }

}
