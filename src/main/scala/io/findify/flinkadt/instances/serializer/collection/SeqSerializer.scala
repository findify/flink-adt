package io.findify.flinkadt.instances.serializer.collection

import io.findify.flinkadt.api.serializer.SimpleSerializer
import org.apache.flink.api.common.typeutils.{SimpleTypeSerializerSnapshot, TypeSerializer, TypeSerializerSnapshot}
import org.apache.flink.core.memory.{DataInputView, DataOutputView}

import scala.reflect.ClassTag

class SeqSerializer[T](child: TypeSerializer[T], clazz: Class[T]) extends SimpleSerializer[Seq[T]] {
  override def createInstance(): Seq[T] = Seq.empty[T]
  override def getLength: Int           = -1
  override def deserialize(source: DataInputView): Seq[T] = {
    val count = source.readInt()
    val result = for {
      _ <- 0 until count
    } yield {
      child.deserialize(source)
    }
    result
  }
  override def serialize(record: Seq[T], target: DataOutputView): Unit = {
    target.writeInt(record.size)
    record.foreach(element => child.serialize(element, target))
  }
  override def snapshotConfiguration(): TypeSerializerSnapshot[Seq[T]] =
    new CollectionSerializerSnapshot(child, classOf[SeqSerializer[T]], clazz)

}
