package io.findify.flinkadt.instances.serializer.primitive

import io.findify.flinkadt.api.serializer.SimpleSerializer
import io.findify.flinkadt.instances.serializer.primitive.ShortSerializer.ShortSerializerSnapshot
import org.apache.flink.api.common.typeutils.{ SimpleTypeSerializerSnapshot, TypeSerializerSnapshot }
import org.apache.flink.core.memory.{ DataInputView, DataOutputView }

class ShortSerializer extends SimpleSerializer[Short] {
  override def createInstance(): Short = 0
  override def getLength: Int = 1
  override def deserialize(source: DataInputView): Short = source.readShort()
  override def serialize(record: Short, target: DataOutputView): Unit = target.writeShort(record)
  override def snapshotConfiguration(): TypeSerializerSnapshot[Short] = ShortSerializerSnapshot
}

object ShortSerializer {
  case object ShortSerializerSnapshot extends SimpleTypeSerializerSnapshot[Short](() => new ShortSerializer())
}
