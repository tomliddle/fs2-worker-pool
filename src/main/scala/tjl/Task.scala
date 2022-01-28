package tjl

trait Task[F[_]] {
  def execute: F[Unit]

}

import cats.Monad
import cats.effect.{Async, IO}
import cats.effect.kernel.Sync

import java.time.{LocalDateTime, ZoneOffset}

/** I've recently done something similar for my pet project:
  * define a action class (your domain)
  * create a queue, on to which this actions will be published. i.e. whenever something happens, an action is added into that queue.
  * have another class that consumes from that queue (fs2.Stream.fromQueue) and processes these actions
  * Effectively, this is pretty much what akka does where you have actors that are sending messages to other actors (edited)
  */

case class TaskImpl[F[_]: Sync]() extends Task[F] {

  val rnd = scala.util.Random(seed)

  def seed = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)

  def rndTime: Int = {
    rnd.setSeed(seed)
    rnd.nextInt(10)
  }

  def execute: F[Unit] = {
    Sync[F].delay {
      Thread.sleep(rndTime)
    }
  }

}
