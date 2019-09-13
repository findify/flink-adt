package io.findify.flinkadt.instances.primitive

import io.findify.flinkadt.instances.SimpleSerializer
import io.findify.flinkadt.instances.primitive.ByteSerializer.ByteSerializerSnapshot
import org.apache.flink.api.common.typeutils.{ SimpleTypeSerializerSnapshot, TypeSerializerSnapshot }
import org.apache.flink.core.memory.{ DataInputView, DataOutputView }

class ByteSerializer extends SimpleSerializer[Byte] {
  override def createInstance(): Byte = 0
  override def getLength: Int = 1
  override def deserialize(source: DataInputView): Byte = source.readByte()
  override def serialize(record: Byte, target: DataOutputView): Unit = target.writeByte(record)
  override def snapshotConfiguration(): TypeSerializerSnapshot[Byte] = ByteSerializerSnapshot
}

object ByteSerializer {
  case object ByteSerializerSnapshot extends SimpleTypeSerializerSnapshot[Byte](() => new ByteSerializer())
}
