package io.findify.flinkadt

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.apache.flink.api.common.typeinfo.TypeInformation

import io.findify.flinkadt.api.*

class Scala3EnumTest extends AnyFlatSpec with Matchers {
  import Scala3EnumTest.Example

  it should "derive type information for a Scala 3 enum" in {
    drop(implicitly[TypeInformation[Example]])
  }
}

object Scala3EnumTest {
  enum Example {
    case Foo(a: String, b: Int)
    case Bar
  }

  object Example {
    implicit val exampleTi: TypeInformation[Example] = deriveTypeInformation
  }
}
