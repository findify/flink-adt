package io.findify.flinkadt.api.serializer

import magnolia.{ SealedTrait, Subtype }
import org.apache.flink.api.common.typeutils.base.TypeSerializerSingleton
import org.apache.flink.api.common.typeutils.{ SimpleTypeSerializerSnapshot, TypeSerializer, TypeSerializerSnapshot }
import org.apache.flink.core.memory.{ DataInputView, DataOutputView }

class CoproductSerializer[T](ctx: SealedTrait[TypeSerializer, T]) extends TypeSerializerSingleton[T] {
  override def isImmutableType: Boolean = true
  override def copy(from: T): T = from
  override def copy(from: T, reuse: T): T = from
  override def copy(source: DataInputView, target: DataOutputView): Unit = serialize(deserialize(source), target)
  override def createInstance(): T =
    // this one may be used for later reuse, but we never reuse coproducts due to their unclear concrete type
    ctx.subtypes.head.typeclass.createInstance()
  override def getLength: Int = -1
  override def serialize(record: T, target: DataOutputView): Unit =
    ctx.dispatch(record) { sub =>
      {
        target.writeByte(sub.index)
        sub.typeclass.serialize(sub.cast(record), target)
      }
    }
  override def deserialize(source: DataInputView): T = {
    val index = source.readByte()
    val subtype = ctx.subtypes(index)
    subtype.typeclass.deserialize(source)
  }
  override def deserialize(reuse: T, source: DataInputView): T = deserialize(source)
  override def snapshotConfiguration(): TypeSerializerSnapshot[T] = CoproductCodecSnapshot
  object CoproductCodecSnapshot extends SimpleTypeSerializerSnapshot[T](() => this)

}
