package tjl

import cats.Id
import cats.effect.{Async, IO, IOApp}
import fs2.*
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext
//import cats.effect.unsafe.implicits.global
import cats.effect.{IO, IOApp}
import fs2.io.file.*
import fs2.{Stream, text}

import scala.concurrent.duration.*
import org.typelevel.log4cats.{Logger, SelfAwareStructuredLogger}
import org.typelevel.log4cats.slf4j.Slf4jLogger
import cats.effect.kernel.Sync
import Streaming._

trait LazyLogging[F[_]: Sync] {
  val logger = LoggerFactory.getLogger(getClass)

  def print(str: String): IO[Unit] = IO.blocking(println(str))
}

object Streaming {
  sealed trait Event
  case class Result(value: String) extends Event
  case object Tick extends Event

  sealed trait Count {
    def int: Int
  }
  case class Completed(int: Int) extends Count
  case class Partial(int: Int) extends Count
}

class Streaming[F[_]: Sync] extends LazyLogging[F] {
  val workerCount = 20

  def stream: Stream[IO, Int] = {
    def task = TaskImpl[IO]()

    val stream: Stream[IO, Task[IO]] = Stream.constant(task).covary[IO].take(100)
    val run: Stream[IO, Result]            = stream.parEvalMapUnordered(workerCount)(t => t.execute.map(Result.apply))
    val events: fs2.Stream[IO, Event] = (run ++ Stream(Tick)).mergeHaltL(fs2.Stream.awakeEvery[IO](1.second).map(_ => Tick))

    events
      .scan[Count](Partial(0)) {
        case (count, Tick) => Completed(count.int)
        case (count, result: Result) => Partial(count.int + 1)
      }
      .collect { case Completed(count) => count }
      .evalTap(c => print(s"Completed count: $c"))
  }
}
