package io.findify.flinkadt.instances.serializer.primitive

import io.findify.flinkadt.api.serializer.SimpleSerializer
import io.findify.flinkadt.instances.serializer.primitive.CharSerializer.CharSerializerSnapshot
import org.apache.flink.api.common.typeutils.{ SimpleTypeSerializerSnapshot, TypeSerializerSnapshot }
import org.apache.flink.core.memory.{ DataInputView, DataOutputView }

class CharSerializer extends SimpleSerializer[Char] {
  override def createInstance(): Char = 0
  override def getLength: Int = 1
  override def deserialize(source: DataInputView): Char = source.readChar()
  override def serialize(record: Char, target: DataOutputView): Unit = target.writeChar(record)
  override def snapshotConfiguration(): TypeSerializerSnapshot[Char] = CharSerializerSnapshot
}

object CharSerializer {
  case object CharSerializerSnapshot extends SimpleTypeSerializerSnapshot[Char](() => new CharSerializer())
}
