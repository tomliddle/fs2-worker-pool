package tjl

import cats.effect.IOApp
import cats.effect.IO

import scala.concurrent.duration.*
import cats.implicits.*
import tjl.*
import fs2.*
import cats.effect.IO
import fs2._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object Main extends IOApp.Simple {

  val timeout = 30.seconds
  val workers = 50

  def run: IO[Unit] = {
    val tasks: Seq[TaskImpl[IO]] = (0 to 50).map(_ => TaskImpl[IO]())
    IO(program(tasks, timeout, workers))
  }

  def program(
      tasks: Seq[TaskImpl[IO]],
      timeout: FiniteDuration,
      workers: Int,
  ): Unit = {
    for {
      t <- tasks.map(t => t.execute)
      //_ <- IO.sleep(1.seconds)
      //_ <- IO(t.map(x => x))
    } yield ()

  }

}
