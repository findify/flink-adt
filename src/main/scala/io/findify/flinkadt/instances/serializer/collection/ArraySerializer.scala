package io.findify.flinkadt.instances.serializer.collection

import io.findify.flinkadt.api.serializer.SimpleSerializer
import io.findify.flinkadt.instances.serializer.collection.ArraySerializer.ArraySerializerSnapshot
import org.apache.flink.api.common.typeutils.{ SimpleTypeSerializerSnapshot, TypeSerializer, TypeSerializerSnapshot }
import org.apache.flink.core.memory.{ DataInputView, DataOutputView }

import scala.reflect.ClassTag

class ArraySerializer[T: ClassTag](child: TypeSerializer[T]) extends SimpleSerializer[Array[T]] {
  override def createInstance(): Array[T] = Array.empty[T]
  override def getLength: Int = -1
  override def deserialize(source: DataInputView): Array[T] = {
    val count = source.readInt()
    val result = for {
      _ <- 0 until count
    } yield {
      child.deserialize(source)
    }
    result.toArray
  }
  override def serialize(record: Array[T], target: DataOutputView): Unit = {
    target.writeInt(record.length)
    record.foreach(element => child.serialize(element, target))
  }
  override def snapshotConfiguration(): TypeSerializerSnapshot[Array[T]] = new ArraySerializerSnapshot(this)

}

object ArraySerializer {
  case class ArraySerializerSnapshot[T](self: TypeSerializer[Array[T]])
      extends SimpleTypeSerializerSnapshot[Array[T]](() => self)
}
