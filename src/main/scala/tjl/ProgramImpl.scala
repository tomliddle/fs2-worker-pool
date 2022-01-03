package tjl

import cats.effect.{Clock, IO}

import scala.concurrent.duration.FiniteDuration

object ProgramImpl extends Program[IO] {

  def program(
      tasks: List[Task[IO]],
      timeout: FiniteDuration,
      workers: Int
  ): Unit = {
    for {
      t <- tasks.map { t => t.execute }
      _ <- tasks.head.state
    } yield ()

  }
}
