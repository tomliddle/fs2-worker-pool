package tjl

import cats.effect.IO
import org.scalatest.*
import flatspec.*
import matchers.*

import scala.concurrent.ExecutionContext

abstract class UnitSpec extends AnyFlatSpec with should.Matchers with OptionValues with Inside with Inspectors

class StreamingTest extends UnitSpec {

  val ec = ExecutionContext.Implicits.global

  "A fs2 worker pool" should "" in {

    val s = new Streaming[IO]

    s.stream.compile.drain
  }

}
