package io.findify.flinkadt

import io.findify.flinkadt.TypeInfoTest.{ ADT, ListedArray, ListedList, Simple }
import org.scalatest.{ FlatSpec, Matchers }

class TypeInfoTest extends FlatSpec with Matchers {
  import io.findify.flinkadt.api.typeinfo._
  import io.findify.flinkadt.api.serializer._
  import io.findify.flinkadt.instances.all._

  it should "derive simple classes" in {
    val x = deriveTypeInformation[Simple]
  }

  it should "derive lists" in {
    val x = deriveTypeInformation[ListedList]
  }

  it should "derive arrays" in {
    val x = deriveTypeInformation[ListedArray]
  }

  it should "derive maps" in {
    val x = deriveTypeInformation[ListedArray]
  }

  it should "derive ADT" in {
    val x = deriveTypeInformation[ADT]
  }
}

object TypeInfoTest {
  case class Simple(a: String, b: Int)
  case class ListedList(x: List[String])
  case class ListedArray(x: Array[String])
  case class ListedMap(x: Map[String, String])

  sealed trait ADT
  case class Foo(a: String) extends ADT
  case class Bar(a: Int) extends ADT
}
