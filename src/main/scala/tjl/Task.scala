package tjl

trait Task[F[_]] {
  def execute: F[String]
}

import cats.Monad
import cats.effect.{Async, IO}
import cats.effect.kernel.Sync
import cats.effect.implicits._
import cats.effect.instances.all._
import java.time.{LocalDateTime, ZoneOffset}
import scala.concurrent.duration._
import cats.implicits.toFunctorOps
import cats.syntax.all.toFunctorOps
import cats.syntax.functor.toFunctorOps

case class TaskImpl[F[_]: Async]() extends Task[F] {

  val rnd = scala.util.Random()

  def rndTime: Int =
    rnd.nextInt(10)

  def execute: F[String] =
    Async[F].sleep(rndTime.seconds).map(_ => s"time")
}
