package io.findify.flinkadt

import magnolia.Subtype
import org.apache.flink.api.common.typeutils.base.TypeSerializerSingleton
import org.apache.flink.api.common.typeutils.{SimpleTypeSerializerSnapshot, TypeSerializer, TypeSerializerSnapshot}
import org.apache.flink.core.memory.{DataInputView, DataOutputView}

import scala.annotation.tailrec

class CoproductSerializer[T](subtypes: Array[Subtype[TypeSerializer, T]]) extends TypeSerializerSingleton[T]{
  def dispatch[Return](value: T)(handle: Subtype[TypeSerializer, T] => Return): Return = {
    @tailrec def rec(ix: Int): Return =
      if (ix < subtypes.length) {
        val sub = subtypes(ix)
        if (sub.cast.isDefinedAt(value)) handle(sub) else rec(ix + 1)
      } else
        throw new IllegalArgumentException(
          s"The given value `$value` is not a sub type of `typeName`"
        )
    rec(0)
  }

  override def isImmutableType: Boolean = true
  override def copy(from: T): T = from
  override def copy(from: T, reuse: T): T = from
  override def copy(source: DataInputView, target: DataOutputView): Unit = serialize(deserialize(source), target)
  override def createInstance(): T = ???
  override def getLength: Int = -1
  override def serialize(record: T, target: DataOutputView): Unit =
    dispatch(record) { sub =>
    {
      target.writeByte(sub.index)
      sub.typeclass.serialize(sub.cast(record), target)
    }
    }
  override def deserialize(source: DataInputView): T = {
    val index = source.readByte()
    val subtype = subtypes(index)
    subtype.typeclass.deserialize(source)
  }
  override def deserialize(reuse: T, source: DataInputView): T = deserialize(source)
  override def snapshotConfiguration(): TypeSerializerSnapshot[T] = CoproductCodecSnapshot
  object CoproductCodecSnapshot extends SimpleTypeSerializerSnapshot[T](() => this)

}
