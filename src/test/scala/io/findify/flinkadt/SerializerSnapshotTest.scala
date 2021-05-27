package io.findify.flinkadt

import java.io.{ ByteArrayInputStream, ByteArrayOutputStream }

import io.findify.flinkadt.SerializerSnapshotTest.{ OuterTrait, SimpleClass1 }
import org.apache.flink.api.common.typeutils.TypeSerializer
import org.apache.flink.core.memory.{ DataInputViewStreamWrapper, DataOutputViewStreamWrapper }
import org.scalatest.{ FlatSpec, Matchers }
import io.findify.flinkadt.api.typeinfo._
import io.findify.flinkadt.api.serializer._
import io.findify.flinkadt.instances.all._

class SerializerSnapshotTest extends FlatSpec with Matchers {
  it should "roundtrip product serializer snapshot" in {
    val ser = implicitly[TypeSerializer[SimpleClass1]]
    roundtripSerializer(ser)
  }

  it should "roundtrip coproduct serializer snapshot" in {
    val ser = implicitly[TypeSerializer[OuterTrait]]
    roundtripSerializer(ser)
  }

  it should "do array ser snapshot" in {
    val set = implicitly[TypeSerializer[Array[SimpleClass1]]]
    roundtripSerializer(set)
  }

  it should "do map ser snapshot" in {
    val set = implicitly[TypeSerializer[Map[SimpleClass1, String]]]
    roundtripSerializer(set)
  }

  it should "do list ser snapshot" in {
    val set = implicitly[TypeSerializer[List[SimpleClass1]]]
    roundtripSerializer(set)
  }

  it should "do map ser snapshot adt " in {
    val set = implicitly[TypeSerializer[Map[OuterTrait, String]]]
    roundtripSerializer(set)
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
  }

}

object SerializerSnapshotTest {
  sealed trait OuterTrait
  case class SimpleClass1(a: String, b: Int) extends OuterTrait
  case class SimpleClass2(a: String, b: Long) extends OuterTrait
}
