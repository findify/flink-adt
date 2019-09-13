package io.findify.flinkadt.instances.primitive

import io.findify.flinkadt.instances.SimpleSerializer
import io.findify.flinkadt.instances.primitive.DoubleSerializer.DoubleSerializerSnapshot
import org.apache.flink.api.common.typeutils.{ SimpleTypeSerializerSnapshot, TypeSerializerSnapshot }
import org.apache.flink.core.memory.{ DataInputView, DataOutputView }

class DoubleSerializer extends SimpleSerializer[Double] {
  override def createInstance(): Double = 0
  override def getLength: Int = 8
  override def deserialize(source: DataInputView): Double = source.readDouble()
  override def serialize(record: Double, target: DataOutputView): Unit = target.writeDouble(record)
  override def snapshotConfiguration(): TypeSerializerSnapshot[Double] = DoubleSerializerSnapshot
}

object DoubleSerializer {
  case object DoubleSerializerSnapshot extends SimpleTypeSerializerSnapshot[Double](() => new DoubleSerializer())
}
