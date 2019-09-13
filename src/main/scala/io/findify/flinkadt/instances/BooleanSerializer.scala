package io.findify.flinkadt.instances

import io.findify.flinkadt.instances.BooleanSerializer.BooleanSerializerSnapshot
import org.apache.flink.api.common.typeutils.{ SimpleTypeSerializerSnapshot, TypeSerializerSnapshot }
import org.apache.flink.core.memory.{ DataInputView, DataOutputView }

class BooleanSerializer extends SimpleSerializer[Boolean] {
  override def createInstance(): Boolean = false
  override def getLength: Int = 1
  override def deserialize(source: DataInputView): Boolean = source.readBoolean()
  override def serialize(record: Boolean, target: DataOutputView): Unit = target.writeBoolean(record)
  override def snapshotConfiguration(): TypeSerializerSnapshot[Boolean] = BooleanSerializerSnapshot
}

object BooleanSerializer {
  case object BooleanSerializerSnapshot extends SimpleTypeSerializerSnapshot[Boolean](() => new BooleanSerializer())
}
