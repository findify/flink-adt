package io.findify.flinkadt.instances

import io.findify.flinkadt.instances.MapSerializer.MapSerializerSnapshot
import org.apache.flink.api.common.typeutils.{ SimpleTypeSerializerSnapshot, TypeSerializer, TypeSerializerSnapshot }
import org.apache.flink.core.memory.{ DataInputView, DataOutputView }

class MapSerializer[K, V](ks: TypeSerializer[K], vs: TypeSerializer[V]) extends SimpleSerializer[Map[K, V]] {
  override def createInstance(): Map[K, V] = Map.empty[K, V]
  override def getLength: Int = -1
  override def deserialize(source: DataInputView): Map[K, V] = {
    val count = source.readInt()
    val result = for {
      _ <- 0 until count
    } yield {
      val key = ks.deserialize(source)
      val value = vs.deserialize(source)
      key -> value
    }
    result.toMap
  }
  override def serialize(record: Map[K, V], target: DataOutputView): Unit = {
    target.writeInt(record.size)
    record.foreach(element => {
      ks.serialize(element._1, target)
      vs.serialize(element._2, target)
    })
  }
  override def snapshotConfiguration(): TypeSerializerSnapshot[Map[K, V]] = new MapSerializerSnapshot(this)

}

object MapSerializer {
  case class MapSerializerSnapshot[K, V](self: TypeSerializer[Map[K, V]])
      extends SimpleTypeSerializerSnapshot[Map[K, V]](() => self)
}
