package tjl

import cats.Id
import cats.effect.{Async, IO, IOApp}
import fs2.*
import cats.effect.unsafe.implicits.global
import cats.effect.{IO, IOApp}
import fs2.io.file._
import fs2.{Stream, text}

import scala.concurrent.duration.*

class StreamingExamples {

  // evaluate an io
  val ioStream: Stream[IO, Int] = fs2.Stream.eval(IO(2))

  // don't evaluate
  val x: Stream[Pure, IO[Int]] = fs2.Stream(IO(2))

  // Emits a value every second
  val emitter: Stream[IO, Int] = Stream.awakeEvery[IO](1.second) zipRight Stream.emits(1 to 100)

  // Constant produces infinite stream that produces the same value
  val ints: Stream[IO, Int] = Stream.constant[IO, Int](1).scan1(_ + _) // 1, 2, 3, ...

  // Debounce takes one from the queue at the rate and drops the rest
  val db: Stream[IO, Int] = ints.debounce(1.second)

  // Emits true every second
  val throttledInts: Stream[IO, Int] = {
    val ticks                              = fs2.Stream.every[IO](1.second) // emits true every second
    val rate                               = 100                            // 100 elements per second
    val zipped: Stream[IO, (Int, Boolean)] = ints.zip(ticks)

    // If we want 100 elements per second, we want to keep the first 100 elements then discard any other elements until another second starts.
    zipped
      .scan((0, rate + 1)) {
        case (_, (n, true))       => (n, 0)         // new second start, emit element and reset counter
        case ((_, count), (n, _)) => (n, count + 1) // emit elements and increment counter
      }
      .filter(_._2 < rate) // keep only the elements where counter is less than rate
      .map(_._1)           // remove counter
  }

  val l: Id[List[String]] = {
    val a: Stream[Pure, Char]                  = fs2.Stream.emits('A' to 'E')
    val b: Stream[Pure, IndexedSeq[String]]    = a.map(letter => (1 to 3).map(index => s"$letter$index"))
    val c: Stream[Pure, String]                = b.flatMap(fs2.Stream.emits) // this flattens the stream
    val d: Stream.CompileOps[Pure, Id, String] = c.compile
    d.toList
  }

  // This is similar to l
  val l2: Id[List[String]] = {
    val a: Stream[Pure, Char]                  = fs2.Stream.emits('A' to 'E')
    val b: Stream[Pure, Stream[Pure, String]]  = a.map(letter => fs2.Stream.emits(1 to 3).map(index => s"$letter$index"))
    val c: Stream[Pure, String]                = b.flatten
    val d: Stream.CompileOps[Pure, Id, String] = c.compile
    d.toList
  }

  // Stream runs and creates another stream, then join in parallel - if all are infinite the later ones won't complete
  val parJoin: Stream[IO, String] = {
    val s                                   = fs2.Stream.emits[IO, Char]('A' to 'E')
    def t(letter: Char): Stream[IO, String] = fs2.Stream.emits[IO, Int](1 to 3).map(index => s"$letter$index")
    val u                                   = s.map(letter => t(letter))
    u.parJoin(5)
  }

  // Start processing the head of each stream first
  /*  def breadthFirst[F, E](streams: Stream[F, Stream[F, E]]): Stream[F, Stream[F, E]] =
    Stream.unfoldEval(streams) { streams =>
      val values: Stream[F, E]          = streams.flatMap(_.head) // get the head of each stream
      val next: Stream[F, Stream[F, E]] = streams.map(_.tail)     // continue with the tails
      val res: Stream[F, Stream[F, E]] =
        values.compile.toList.map(_.headOption.map(_ => values -> next)) // stop when there's no more values
      res
    }*/

  // Batching (drain disgards the output) chunk batches
  val a: Id[Unit] = Stream.emits(1 to 100).chunkN(10).map(println).compile.drain

  val list: List[Chunk[Int]] = {
    val s: Stream[Pure, Int]             = Stream(1, 2) ++ Stream(3) ++ Stream(4, 5, 6)
    val chunks: Stream[Pure, Chunk[Int]] = s.chunks
    chunks.toList
  }

  // Covary: Lifts this stream to the specified effect type.
  val infiniteStream: Stream[IO, Int] = {
    val s: Stream[Pure, Int] = Stream.emit(1)
    val t: Stream[Pure, Int] = s.repeat
    val u: Stream[IO, Int]   = t.covary[IO]
    val v: Stream[IO, Int]   = u.map(_ + 3)
    val w: Stream[IO, Int]   = v.take(4)
    w
  }

  val converter: Stream[IO, Unit] = {
    def fahrenheitToCelsius(f: Double): Double =
      (f - 32.0) * (5.0 / 9.0)

    def doRead() = {
      val a: Stream[IO, Byte]     = fs2.io.file.Files[IO].readAll(Path("testdata/fahrenheit.txt"))
      val b: Stream[IO, String]   = a.through(text.utf8.decode)
      val c: Stream[IO, String]   = b.through(text.lines)
      val d: Stream[IO, String]   = c.filter(s => s.trim.nonEmpty && !s.startsWith("//"))
      val e: Stream[IO, String]   = d.map(line => fahrenheitToCelsius(line.toDouble).toString)
      val f: Stream[IO, String]   = e.intersperse("\n") // adds separator between values
      val g: Stream[IO, Byte]     = f.through(text.utf8.encode)
      val h: Stream[IO, INothing] = g.through(Files[IO].writeAll(Path("testdata/celsius.txt")))
      h
    }
    doRead()
  }

  val y = fs2.Stream
    .eval(IO(2))
    .compile
    .toList
    .unsafeRunSync()
}
