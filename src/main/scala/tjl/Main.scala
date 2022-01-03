package tjl

import cats.effect.IOApp
import cats.effect.IO
import scala.concurrent.duration._

object Main extends IOApp.Simple {

  val timeout = 30.seconds
  val workers = 50

  // This is your new "main"!
  def run: IO[Unit] = {
    val tasks = (0 to 50).map { _ =>
      new TaskImpl[IO]()
    }

    ProgramImpl.program(tasks, timeout, workers)
  }
}
