package io.findify.flinkadt

import cats.data.NonEmptyList
import io.findify.flinkadt.AnyTest.FAny
import io.findify.flinkadt.AnyTest.Filter.{FTerm, StringTerm, TermFilter}
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.common.typeutils.TypeSerializer
import org.apache.flink.core.memory.{DataInputViewStreamWrapper, DataOutputViewStreamWrapper}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

class AnyTest extends AnyFlatSpec with Matchers with TestUtils {
  import io.findify.flinkadt.api._

  it should "serialize concrete class" in {
    val ser = implicitly[TypeInformation[StringTerm]].createSerializer(null)
    roundtrip(ser, StringTerm("fo"))
  }

  it should "serialize ADT" in {
    val ser = implicitly[TypeInformation[FAny]].createSerializer(null)
    roundtrip(ser, StringTerm("fo"))
  }

  it should "serialize NEL" in {
    val ser = implicitly[TypeInformation[NonEmptyList[FTerm]]].createSerializer(null)
    roundtrip(ser, NonEmptyList.one(StringTerm("fo")))
  }

  it should "serialize nested nel" in {
    val ser = implicitly[TypeInformation[TermFilter]].createSerializer(null)
    roundtrip(ser, TermFilter("a", NonEmptyList.one(StringTerm("fo"))))
  }

}

object AnyTest {
  sealed trait FAny

  sealed trait FValueAny extends FAny {
    def value: Any
  }
  object Filter {
    sealed trait FTerm extends FValueAny
    case class StringTerm(value: String) extends FTerm {
      type T = String
    }
    case class NumericTerm(value: Double) extends FTerm {
      type T = Double
    }

    case class TermFilter(
        field: String,
        values: NonEmptyList[FTerm]
    )
  }
}
