package io.findify.flinkadt

import magnolia.{CaseClass, Param}
import org.apache.flink.api.common.typeutils.base.TypeSerializerSingleton
import org.apache.flink.api.common.typeutils.{SimpleTypeSerializerSnapshot, TypeSerializer, TypeSerializerSnapshot}
import org.apache.flink.core.memory.{DataInputView, DataOutputView}

class ProductSerializer[T](val params: Array[Param[TypeSerializer, T]], val ctx: CaseClass[TypeSerializer, T] ) extends RichTypeSerializerSingleton[T] {
  override def createInstance(): T = ctx.construct { param =>
    param.default
  }
  override def serialize(record: T, target: DataOutputView): Unit = {
    var i = 0
    while (i < params.length) {
      val p = params(i)
      p.typeclass.serialize(p.dereference(record), target)
      i += 1
    }
  }
  override def deserialize(source: DataInputView): T =
    ctx.construct { param =>
      param.typeclass.deserialize(source)
    }
  override def snapshotConfiguration(): TypeSerializerSnapshot[T] = ProductSerializerSnapshot
  object ProductSerializerSnapshot extends SimpleTypeSerializerSnapshot[T](() => this)
}
