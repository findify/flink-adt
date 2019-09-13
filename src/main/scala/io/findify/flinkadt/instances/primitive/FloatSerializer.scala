package io.findify.flinkadt.instances.primitive

import io.findify.flinkadt.instances.SimpleSerializer
import io.findify.flinkadt.instances.primitive.FloatSerializer.FloatSerializerSnapshot
import org.apache.flink.api.common.typeutils.{ SimpleTypeSerializerSnapshot, TypeSerializerSnapshot }
import org.apache.flink.core.memory.{ DataInputView, DataOutputView }

class FloatSerializer extends SimpleSerializer[Float] {
  override def createInstance(): Float = 0
  override def getLength: Int = 8
  override def deserialize(source: DataInputView): Float = source.readFloat()
  override def serialize(record: Float, target: DataOutputView): Unit = target.writeFloat(record)
  override def snapshotConfiguration(): TypeSerializerSnapshot[Float] = FloatSerializerSnapshot
}

object FloatSerializer {
  case object FloatSerializerSnapshot extends SimpleTypeSerializerSnapshot[Float](() => new FloatSerializer())
}
