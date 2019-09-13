package io.findify.flinkadt

import java.io.{ ByteArrayInputStream, ByteArrayOutputStream }

import io.findify.flinkadt.SerializerTest.{ ADT, ADT2, Bar, Bar2, Foo, Foo2, Nested, Simple, SimpleJava, WrappedADT }
import io.findify.flinkadt.api.serializer.ProductSerializer
import org.apache.flink.api.common.typeutils.TypeSerializer
import org.apache.flink.api.java.typeutils.runtime.kryo.KryoSerializer
import org.apache.flink.core.memory.{ DataInputViewStreamWrapper, DataOutputViewStreamWrapper }
import org.scalatest.{ FlatSpec, Inspectors, Matchers }

class SerializerTest extends FlatSpec with Matchers with Inspectors {
  import io.findify.flinkadt.api.serializer.deriveSerializer
  import io.findify.flinkadt.instances.all._
  it should "derive serializer for simple class" in {
    val ser = deriveSerializer[Simple]
    roundtrip(ser, Simple(1, "foo"))
    noKryo(ser)
  }

  it should "derive serializer for java classes" in {
    val ser = deriveSerializer[SimpleJava]
    roundtrip(ser, SimpleJava(1, "foo"))
    noKryo(ser)
  }

  it should "derive nested classes" in {
    val ser = deriveSerializer[Nested]
    roundtrip(ser, Nested(Simple(1, "foo")))
    noKryo(ser)
  }

  it should "derive for ADTs" in {
    val ser = deriveSerializer[ADT]
    roundtrip(ser, Foo("a"))
    roundtrip(ser, Bar(1))
    noKryo(ser)
  }

  it should "derive for ADTs with case objects" in {
    val ser = deriveSerializer[ADT2]
    //roundtrip(ser, Foo2)
    roundtrip(ser, Bar2)
    noKryo(ser)
  }

  it should "derive for nested ADTs" in {
    implicit val ser1 = deriveSerializer[ADT]
    val ser = deriveSerializer[WrappedADT]
    roundtrip(ser, WrappedADT(Foo("a")))
    roundtrip(ser, WrappedADT(Bar(1)))
    noKryo(ser)
  }

  it should "derive seq" in {
    val ser = implicitly[TypeSerializer[Seq[Simple]]]
  }

  it should "derive seq of seq" in {
    val ser = implicitly[TypeSerializer[Seq[Seq[Simple]]]]
  }

  def roundtrip[T](ser: TypeSerializer[T], in: T) = {
    val out = new ByteArrayOutputStream()
    ser.serialize(in, new DataOutputViewStreamWrapper(out))
    val copy = ser.deserialize(new DataInputViewStreamWrapper(new ByteArrayInputStream(out.toByteArray)))
    in shouldBe copy
  }

  def noKryo[T](ser: TypeSerializer[T]): Unit =
    ser match {
      case p: ProductSerializer[_] =>
        forAll(p.ctx.parameters) { param =>
          noKryo(param.typeclass)
        }
      case _: KryoSerializer[_] =>
        throw new IllegalArgumentException("kryo detected")
      case _ => // ok
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
}
