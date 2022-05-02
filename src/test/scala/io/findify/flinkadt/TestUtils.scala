package io.findify.flinkadt

import io.findify.flinkadt.api.serializer.ScalaCaseClassSerializer
import org.apache.flink.api.common.typeutils.TypeSerializer
import org.apache.flink.api.java.typeutils.runtime.kryo.KryoSerializer
import org.apache.flink.core.memory.{DataInputViewStreamWrapper, DataOutputViewStreamWrapper}
import org.scalatest.{Assertion, Inspectors}
import org.scalatest.matchers.should.Matchers

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, ObjectOutputStream}

trait TestUtils extends Matchers with Inspectors {
  def roundtrip[T](ser: TypeSerializer[T], in: T): Assertion = {
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

  def serializable[T](ser: TypeSerializer[T]): Unit = {
    val stream = new ObjectOutputStream(new ByteArrayOutputStream())
    stream.writeObject(ser)
  }

  def all[T](ser: TypeSerializer[T], in: T): Unit = {
    roundtrip(ser, in)
    noKryo(ser)
    serializable(ser)
  }

}
