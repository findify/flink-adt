package io.findify.flinkadt

import io.findify.flinkadt.SerializerTest.DeeplyNested.ModeNested.SuperNested.{Egg, Food}

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, ObjectOutputStream}
import io.findify.flinkadt.SerializerTest.{
  ADT,
  ADT2,
  Ann,
  Annotated,
  Bar,
  Bar2,
  Foo,
  Foo2,
  Nested,
  Node,
  P2,
  Param,
  Simple,
  SimpleJava,
  SimpleList,
  SimpleOption,
  SimpleSeq,
  SimpleSeqSeq,
  WrappedADT
}
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.common.typeutils.TypeSerializer
import org.apache.flink.api.java.typeutils.runtime.kryo.KryoSerializer
import org.apache.flink.api.scala.typeutils.ScalaCaseClassSerializer
import org.apache.flink.core.memory.{DataInputViewStreamWrapper, DataOutputViewStreamWrapper}
import org.scalatest.Inspectors
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import io.findify.flinkadt.api._

class SerializerTest extends AnyFlatSpec with Matchers with Inspectors {
  import io.findify.flinkadt.api._

  it should "derive serializer for simple class" in {
    val ser = implicitly[TypeInformation[Simple]].createSerializer(null)
    all(ser, Simple(1, "foo"))
  }

  it should "derive serializer for java classes" in {
    val ser = implicitly[TypeInformation[SimpleJava]].createSerializer(null)
    all(ser, SimpleJava(1, "foo"))
  }

  it should "derive nested classes" in {
    val ser = implicitly[TypeInformation[Nested]].createSerializer(null)
    all(ser, Nested(Simple(1, "foo")))
  }

  it should "derive for ADTs" in {
    val ser = implicitly[TypeInformation[ADT]].createSerializer(null)
    all(ser, Foo("a"))
    all(ser, Bar(1))
  }

  it should "derive for ADTs with case objects" in {
    val ser = implicitly[TypeInformation[ADT2]].createSerializer(null)
    all(ser, Foo2)
    all(ser, Bar2)
  }

  it should "derive for deeply nested classes" in {
    val ser = implicitly[TypeInformation[Egg]].createSerializer(null)
    all(ser, Egg(1))
  }

  it should "derive for deeply nested adts" in {
    val ser = implicitly[TypeInformation[Food]].createSerializer(null)
    all(ser, Egg(1))
  }

  it should "derive for nested ADTs" in {
    val ser = implicitly[TypeInformation[WrappedADT]].createSerializer(null)
    all(ser, WrappedADT(Foo("a")))
    all(ser, WrappedADT(Bar(1)))
  }

  it should "derive for generic ADTs" in {
    val ser = implicitly[TypeInformation[Param[Int]]].createSerializer(null)
    all(ser, P2(1))
  }

  it should "derive seq" in {
    val ser = implicitly[TypeInformation[SimpleSeq]].createSerializer(null)
    noKryo(ser)
    serializable(ser)
  }

  it should "derive recursively" in {
    // recursive is broken
    //val ti = implicitly[TypeInformation[Node]]
  }

  it should "derive list" in {
    val ser = implicitly[TypeInformation[List[Simple]]].createSerializer(null)
    all(ser, List(Simple(1, "a")))
  }

  it should "derive nested list" in {
    val ser = implicitly[TypeInformation[List[SimpleList]]].createSerializer(null)
    all(ser, List(SimpleList(List(1))))
  }

  it should "derive seq of seq" in {
    val ser = implicitly[TypeInformation[SimpleSeqSeq]].createSerializer(null)
    noKryo(ser)
    serializable(ser)
  }

  it should "be serializable in case of annotations on classes" in {
    val ser = implicitly[TypeInformation[Annotated]].createSerializer(null)
    serializable(ser)
  }

  it should "be serializable in case of annotations on subtypes" in {
    val ser = implicitly[TypeInformation[Ann]].createSerializer(null)
    serializable(ser)
  }

  it should "serialize Option" in {
    val ser = implicitly[TypeInformation[SimpleOption]].createSerializer(null)
    all(ser, SimpleOption(None))
    roundtrip(ser, SimpleOption(Some("foo")))
  }

  def roundtrip[T](ser: TypeSerializer[T], in: T) = {
    val out = new ByteArrayOutputStream()
    ser.serialize(in, new DataOutputViewStreamWrapper(out))
    val copy = ser.deserialize(new DataInputViewStreamWrapper(new ByteArrayInputStream(out.toByteArray)))
    in shouldBe copy
  }

  def noKryo[T](ser: TypeSerializer[T]): Unit =
    ser match {
      case p: ScalaCaseClassSerializer[_] =>
        forAll(p.getFieldSerializers) { param =>
          noKryo(param)
        }
      case _: KryoSerializer[_] =>
        throw new IllegalArgumentException("kryo detected")
      case _ => // ok
    }

  def serializable[T](ser: TypeSerializer[T]) = {
    val stream = new ObjectOutputStream(new ByteArrayOutputStream())
    stream.writeObject(ser)
  }

  def all[T](ser: TypeSerializer[T], in: T) = {
    roundtrip(ser, in)
    noKryo(ser)
    serializable(ser)
  }

}

object SerializerTest {
  case class Simple(a: Int, b: String)
  case class SimpleList(a: List[Int])
  case class SimpleJava(a: Integer, b: String)
  case class Nested(a: Simple)

  case class SimpleSeq(a: Seq[Simple])
  case class SimpleSeqSeq(a: Seq[Seq[Simple]])

  sealed trait ADT
  case class Foo(a: String) extends ADT
  case class Bar(b: Int)    extends ADT

  sealed trait ADT2
  case object Foo2 extends ADT2
  case object Bar2 extends ADT2

  case class WrappedADT(x: ADT)

  @SerialVersionUID(1L)
  case class Annotated(foo: String)

  sealed trait Ann
  @SerialVersionUID(1L)
  case class Next(foo: String) extends Ann

  object DeeplyNested {
    object ModeNested {
      object SuperNested {
        sealed trait Food
        case class Egg(i: Int) extends Food
        case object Ham        extends Food
      }
    }
  }

  sealed trait Param[T] {
    def a: T
  }
  case class P1(a: String) extends Param[String]
  case class P2(a: Int)    extends Param[Int]

  case class Node(left: Option[Node], right: Option[Node])

  case class SimpleOption(a: Option[String])
}
