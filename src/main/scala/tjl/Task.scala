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

/** I've recently done something similar for my pet project:
  * define a action class (your domain)
  * create a queue, on to which this actions will be published. i.e. whenever something happens, an action is added into that queue.
  * have another class that consumes from that queue (fs2.Stream.fromQueue) and processes these actions
  * Effectively, this is pretty much what akka does where you have actors that are sending messages to other actors (edited)
  */

case class TaskImpl[F[_]: Async]() extends Task[F] {

  val rnd = scala.util.Random()

  //def seed = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)

  def rndTime: Int =
    //rnd.setSeed(seed)
    rnd.nextInt(10)

  def execute: F[String] =
    Async[F].sleep(rndTime.seconds).map(_ => s"time")

}
