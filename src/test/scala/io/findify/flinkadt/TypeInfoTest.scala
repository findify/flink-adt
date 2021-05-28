package io.findify.flinkadt

import io.findify.flinkadt.TypeInfoTest.{ADT, Bar, Foo, ListedArray, ListedList, ListedMap, Parametrized, Simple}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class TypeInfoTest extends AnyFlatSpec with Matchers {
  import io.findify.flinkadt.api._

  it should "derive simple classes" in {
    val x = deriveTypeInformation[Simple]
  }

  it should "derive parametrized classes" in {
    val x = deriveTypeInformation[Parametrized[String]]
  }

  it should "derive lists" in {
    val x = deriveTypeInformation[ListedList]
  }

  it should "derive arrays" in {
    val x = deriveTypeInformation[ListedArray]
  }

  it should "derive maps" in {
    val x = deriveTypeInformation[ListedMap]
  }

  it should "derive ADT" in {
    val x = deriveTypeInformation[ADT]
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
