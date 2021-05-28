package io.findify.flinkadt

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import io.findify.flinkadt.SerializerSnapshotTest.{ADT2, OuterTrait, SimpleClass1, SimpleClassArray, SimpleClassList, SimpleClassMap1, SimpleClassMap2, TraitMap}
import org.apache.flink.api.common.typeutils.TypeSerializer
import org.apache.flink.core.memory.{DataInputViewStreamWrapper, DataOutputViewStreamWrapper}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import io.findify.flinkadt.api.typeinfo._
import io.findify.flinkadt.api.serializer._
import io.findify.flinkadt.instances.all._

class SerializerSnapshotTest extends AnyFlatSpec with Matchers {
  implicit val s1 = deriveSerializer[SimpleClass1]
  implicit val out = deriveADTSerializer[OuterTrait]
  it should "roundtrip product serializer snapshot" in {
    val ser = deriveSerializer[SimpleClass1]
    roundtripSerializer(ser)
  }

  it should "roundtrip coproduct serializer snapshot" in {
    val ser = deriveADTSerializer[OuterTrait]
    roundtripSerializer(ser)
  }

  it should "roundtrip coproduct serializer snapshot with singletons" in {
    val ser = deriveADTSerializer[ADT2]
    roundtripSerializer(ser)
  }

  it should "do array ser snapshot" in {
    val set = deriveSerializer[SimpleClassArray]
    roundtripSerializer(set)
  }

  it should "do map ser snapshot" in {
    roundtripSerializer(deriveSerializer[SimpleClassMap1])
    roundtripSerializer(deriveSerializer[SimpleClassMap2])
  }

  it should "do list ser snapshot" in {
    roundtripSerializer(deriveSerializer[SimpleClassList])  }

  it should "do map ser snapshot adt " in {
    roundtripSerializer(deriveSerializer[TraitMap])
  }

  def roundtripSerializer[T](ser: TypeSerializer[T]) = {
    val snap = ser.snapshotConfiguration()
    val buffer = new ByteArrayOutputStream()
    val output = new DataOutputViewStreamWrapper(buffer)
    snap.writeSnapshot(output)
    output.close()
    val input = new DataInputViewStreamWrapper(new ByteArrayInputStream(buffer.toByteArray))
    snap.readSnapshot(ser.snapshotConfiguration().getCurrentVersion, input, ClassLoader.getSystemClassLoader)
    val restored = snap.restoreSerializer()
    ser shouldBe restored
  }

}

object SerializerSnapshotTest {
  sealed trait OuterTrait
  case class SimpleClass1(a: String, b: Int) extends OuterTrait
  case class SimpleClass2(a: String, b: Long) extends OuterTrait

  case class SimpleClassArray(a: Array[SimpleClass1])
  case class SimpleClassMap1(a: Map[String, SimpleClass1])
  case class SimpleClassMap2(a: Map[SimpleClass1, String])
  case class SimpleClassList(a: List[SimpleClass1])

  case class TraitMap(a: Map[OuterTrait, String])

  sealed trait ADT2
  case object Foo2 extends ADT2
  case object Bar2 extends ADT2

}
