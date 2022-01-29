package io.findify.flinkadt

import io.findify.flinkadt.TypeInfoTest.{ADT, ListedArray, ListedList, ListedMap, Parametrized, Simple}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class TypeInfoTest extends AnyFlatSpec with Matchers {
  import io.findify.flinkadt.api._

  it should "derive simple classes" in {
    drop(deriveTypeInformation[Simple])
  }

  it should "derive parametrized classes" in {
    drop(deriveTypeInformation[Parametrized[String]])
  }

  it should "derive lists" in {
    drop(deriveTypeInformation[ListedList])
  }

  it should "derive arrays" in {
    drop(deriveTypeInformation[ListedArray])
  }

  it should "derive maps" in {
    drop(deriveTypeInformation[ListedMap])
  }

  it should "derive ADT" in {
    drop(deriveTypeInformation[ADT])
  }
}

object TypeInfoTest {
  case class Parametrized[T](a: T)
  case class Simple(a: String, b: Int)
  case class ListedList(x: List[String])
  case class ListedArray(x: Array[String])
  case class ListedMap(x: Map[String, String])

  sealed trait ADT
  case class Foo(a: String) extends ADT
  case class Bar(a: Int)    extends ADT
}
