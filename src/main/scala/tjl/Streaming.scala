package tjl

import cats.Id
import cats.effect.{Async, IO, IOApp}
import fs2.*
import cats.effect.unsafe.implicits.global
import cats.effect.{IO, IOApp}
import fs2.io.file.*
import fs2.{Stream, text}

import scala.concurrent.duration.*
import org.typelevel.log4cats.{Logger, SelfAwareStructuredLogger}
import org.typelevel.log4cats.slf4j.Slf4jLogger
import cats.effect.kernel.Sync


trait LazyLogging[F[_]: Sync] {
  implicit val logger: SelfAwareStructuredLogger[F] = Slf4jLogger.getLogger[F]
}

class Streaming[F[_]: Sync] extends LazyLogging[F] {

  val workerCount = 50

  def stream: Stream[IO, (Int, Int)] = {
    val task = TaskImpl[IO]()

    val stream: Stream[IO, Task[IO]] = Stream.constant(task).covary[IO].take(100)
    val run: Stream[IO, Unit]            = stream.parEvalMapUnordered(workerCount)(t => t.execute)
    val ticks                            = fs2.Stream.every[IO](1.second)
    val completed: Stream[IO, Unit]      = run.filter(x => true) // TODO check is completed

    val zipped: Stream[IO, (Unit, Boolean)] = run.zip(ticks)
    val checker: Stream[IO, (Int, Int)] = zipped
      .scan((0, 0)) {
        case ((x, count), (n, true)) =>
          Logger[F].info("Logging Start Something")

          // todo output value to screen
          (x, count + 1)
        case ((x, count), (n, y)) => (x, count + 1) // emit elements and increment counter
      }.map(_._2)

    checker
  }
}
