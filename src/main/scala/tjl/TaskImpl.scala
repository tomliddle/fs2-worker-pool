package tjl

import cats.Monad
import cats.effect.kernel.Sync

import java.time.{LocalDateTime, ZoneOffset}

class TaskImpl[F[_]](implicit val S: Sync[F]) extends Task[F] {

  var result: Option[Int] = None

  val rnd = scala.util.Random(seed)

  def seed = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)

  def rndTime: Int = {
    rnd.setSeed(seed)
    rnd.nextInt(10)
  }

  def execute: F[Unit] = {
    S.interruptible {
      Thread.sleep(rndTime)
      result = Some(rndTime)
    }
  }

  def state: F[Option[Int]] = S.delay(result)
}
