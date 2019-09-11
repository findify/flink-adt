package io.findify.flinkadt

import org.apache.flink.api.common.typeutils.base.TypeSerializerSingleton
import org.apache.flink.core.memory.{DataInputView, DataOutputView}

trait RichTypeSerializerSingleton[T] extends TypeSerializerSingleton[T] {
  override def isImmutableType: Boolean = true
  override def copy(from: T): T = from
  override def copy(from: T, reuse: T): T = from
  override def copy(source: DataInputView, target: DataOutputView): Unit = serialize(deserialize(source), target)
  override def getLength: Int = -1
  override def deserialize(reuse: T, source: DataInputView): T = deserialize(source)

}
