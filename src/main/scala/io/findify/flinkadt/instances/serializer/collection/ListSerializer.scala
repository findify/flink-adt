package io.findify.flinkadt.instances.serializer.collection

import io.findify.flinkadt.api.serializer.SimpleSerializer
import org.apache.flink.api.common.typeutils.{SimpleTypeSerializerSnapshot, TypeSerializer, TypeSerializerSnapshot}
import org.apache.flink.core.memory.{DataInputView, DataOutputView}

import scala.reflect.ClassTag

class ListSerializer[T](child: TypeSerializer[T], clazz: Class[T]) extends SimpleSerializer[List[T]] {
  override def createInstance(): List[T] = List.empty[T]
  override def getLength: Int            = -1
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
  override def snapshotConfiguration(): TypeSerializerSnapshot[List[T]] =
    new CollectionSerializerSnapshot(child, classOf[ListSerializer[T]], clazz)

}
