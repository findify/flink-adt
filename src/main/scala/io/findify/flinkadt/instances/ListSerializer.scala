package io.findify.flinkadt.instances

import io.findify.flinkadt.instances.IntSerializer.IntSerializerSnapshot
import io.findify.flinkadt.instances.ListSerializer.ListSerializerSnapshot
import org.apache.flink.api.common.typeutils.{CompositeSerializer, CompositeTypeSerializerSnapshot, SimpleTypeSerializerSnapshot, TypeSerializer, TypeSerializerSnapshot}
import org.apache.flink.core.memory.{DataInputView, DataOutputView}

class ListSerializer[T](child: TypeSerializer[T]) extends SimpleSerializer[List[T]] {
  override def createInstance(): List[T] = List.empty[T]
  override def getLength: Int = -1
  override def deserialize(source: DataInputView): List[T] = {
    val count = source.readInt()
    val result = for {
      _ <- 0 until count
    } yield {
      child.deserialize(source)
    }
    result.toList
  }
  override def serialize(record: List[T], target: DataOutputView): Unit = {
    target.writeInt(record.size)
    record.foreach(element => child.serialize(element, target))
  }
  override def snapshotConfiguration(): TypeSerializerSnapshot[List[T]] = new ListSerializerSnapshot(this)

}

object ListSerializer {
  case class ListSerializerSnapshot[T](self: TypeSerializer[List[T]]) extends SimpleTypeSerializerSnapshot[List[T]]( () => self)
}