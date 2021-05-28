package io.findify.flinkadt

import io.findify.flinkadt.SerializerTest.DeeplyNested.ModeNested.SuperNested.{Egg, Food}

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, ObjectOutputStream}
import io.findify.flinkadt.SerializerTest.{ADT, ADT2, Ann, Annotated, Bar, Bar2, Foo, Foo2, Nested, Simple, SimpleJava, WrappedADT}
import org.apache.flink.api.common.typeutils.TypeSerializer
import org.apache.flink.api.java.typeutils.runtime.kryo.KryoSerializer
import org.apache.flink.api.scala.typeutils.ScalaCaseClassSerializer
import org.apache.flink.core.memory.{DataInputViewStreamWrapper, DataOutputViewStreamWrapper}
import org.scalatest.{FlatSpec, Inspectors, Matchers}

class SerializerTest extends FlatSpec with Matchers with Inspectors {
  import io.findify.flinkadt.api.serializer.deriveSerializer
  import io.findify.flinkadt.instances.all._
  it should "derive serializer for simple class" in {
    val ser = deriveSerializer[Simple]
    all(ser, Simple(1, "foo"))
  }

  it should "derive serializer for java classes" in {
    val ser = deriveSerializer[SimpleJava]
    all(ser, SimpleJava(1, "foo"))
  }

  it should "derive nested classes" in {
    val ser = deriveSerializer[Nested]
    all(ser, Nested(Simple(1, "foo")))
  }

  it should "derive for ADTs" in {
    val ser = deriveSerializer[ADT]
    all(ser, Foo("a"))
    all(ser, Bar(1))
  }

  it should "derive for ADTs with case objects" in {
    val ser = deriveSerializer[ADT2]
    all(ser, Foo2)
    all(ser, Bar2)
  }

  it should "derive for deeply nested classes" in {
    val ser = deriveSerializer[Egg]
    all(ser, Egg(1))
  }

  it should "derive for deeply nested adts" in {
    val ser = deriveSerializer[Food]
    all(ser, Egg(1))
  }

  it should "derive for nested ADTs" in {
    implicit val ser1 = deriveSerializer[ADT]
    val ser = deriveSerializer[WrappedADT]
    all(ser, WrappedADT(Foo("a")))
    all(ser, WrappedADT(Bar(1)))
  }

  it should "derive seq" in {
    val ser = implicitly[TypeSerializer[Seq[Simple]]]
    noKryo(ser)
    serializable(ser)
  }

  it should "derive seq of seq" in {
    val ser = implicitly[TypeSerializer[Seq[Seq[Simple]]]]
    noKryo(ser)
    serializable(ser)
  }

  it should "be serializable in case of annotations on classes" in {
    val ser = implicitly[TypeSerializer[Annotated]]
    serializable(ser)
  }

  it should "be serializable in case of annotations on subtypes" in {
    val ser = implicitly[TypeSerializer[Ann]]
    serializable(ser)
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
  case class SimpleJava(a: Integer, b: String)
  case class Nested(a: Simple)

  sealed trait ADT
  case class Foo(a: String) extends ADT
  case class Bar(b: Int) extends ADT

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
        case object Ham extends Food
      }
    }
  }
}
