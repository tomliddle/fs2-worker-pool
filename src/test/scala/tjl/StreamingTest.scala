package tjl

import cats.effect.IO
import org.scalatest.*
import flatspec.*
import matchers.*

import scala.concurrent.ExecutionContext

abstract class UnitSpec extends AnyFlatSpec with should.Matchers with OptionValues with Inside with Inspectors

class StreamingTest extends UnitSpec {

  val ec = ExecutionContext.Implicits.global

  "A Stack" should "pop values in last-in-first-out order" in {

    val s = new Streaming[IO](ec)

    s.stream.compile.drain

  }

  it should "throw NoSuchElementException if an empty stack is popped" in {}
}
