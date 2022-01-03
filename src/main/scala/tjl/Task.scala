package tjl

trait Task[F[_]] {
  def execute: F[Unit]

  def state: Option[Int]
}
