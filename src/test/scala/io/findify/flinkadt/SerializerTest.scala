package io.findify.flinkadt

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

import io.findify.flinkadt.SerializerTest.{ADT, Bar, Foo, Nested, Simple, SimpleJava, WrappedADT}
import org.apache.flink.api.common.typeutils.TypeSerializer
import org.apache.flink.api.java.typeutils.runtime.kryo.KryoSerializer
import org.apache.flink.core.memory.{DataInputViewStreamWrapper, DataOutputViewStreamWrapper}
import org.scalatest.{FlatSpec, Inspectors, Matchers}

class SerializerTest extends FlatSpec with Matchers with Inspectors {
  import org.apache.flink.api.scala._
  import api._
  it should "derive serializer for simple class" in {
    val ser = gen[Simple]
    roundtrip(ser, Simple(1, "foo"))
    noKryo(ser)
  }

  it should "derive serializer for java classes" in {
    val ser = gen[SimpleJava]
    roundtrip(ser, SimpleJava(1, "foo"))
    noKryo(ser)
  }

  it should "derive nested classes" in {
    val ser = gen[Nested]
    roundtrip(ser, Nested(Simple(1, "foo")))
    noKryo(ser)
  }

  it should "derive for ADTs" in {
    val ser = gen[ADT]
    roundtrip(ser, Foo("a"))
    roundtrip(ser, Bar(1))
    noKryo(ser)
  }

  it should "derive for nested ADTs" in {
    implicit val ser1 = gen[ADT]
    val ser = gen[WrappedADT]
    roundtrip(ser, WrappedADT(Foo("a")))
    roundtrip(ser, WrappedADT(Bar(1)))
    noKryo(ser)
  }

  def roundtrip[T](ser: TypeSerializer[T], in: T) = {
    val out = new ByteArrayOutputStream()
    ser.serialize(in, new DataOutputViewStreamWrapper(out))
    val copy = ser.deserialize(new DataInputViewStreamWrapper(new ByteArrayInputStream(out.toByteArray)))
    in shouldBe copy
  }

  def noKryo[T](ser: TypeSerializer[T]): Unit = {
    ser match {
      case p: ProductSerializer[_] =>
        forAll(p.params) { param => noKryo(param.typeclass)}
      case _: KryoSerializer[_] =>
        throw new IllegalArgumentException("kryo detected")
      case _ => // ok
    }
  }

}

object SerializerTest {
  case class Simple(a: Int, b: String)
  case class SimpleJava(a: Integer, b: String)
  case class Nested(a: Simple)

  sealed trait ADT
  case class Foo(a: String) extends ADT
  case class Bar(b: Int) extends ADT

  case class WrappedADT(x: ADT)
}