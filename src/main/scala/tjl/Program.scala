package tjl

import scala.concurrent.duration.FiniteDuration

trait Program[F[_]] {
  def program(tasks: List[Task[F]], timeout: FiniteDuration, workers: Int): Unit
}
