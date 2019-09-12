package io.findify.flinkadt.instances

import io.findify.flinkadt.instances.IntSerializer.IntSerializerSnapshot
import org.apache.flink.api.common.typeutils.{SimpleTypeSerializerSnapshot, TypeSerializerSnapshot}
import org.apache.flink.core.memory.{DataInputView, DataOutputView}

class IntSerializer extends SimpleSerializer[Int] {
  override def createInstance(): Int = 0
  override def getLength: Int = 4
  override def deserialize(source: DataInputView): Int = source.readInt()
  override def serialize(record: Int, target: DataOutputView): Unit = target.writeInt(record)
  override def snapshotConfiguration(): TypeSerializerSnapshot[Int] = IntSerializerSnapshot
}

object IntSerializer {
  case object IntSerializerSnapshot extends SimpleTypeSerializerSnapshot[Int](() => new IntSerializer())
}