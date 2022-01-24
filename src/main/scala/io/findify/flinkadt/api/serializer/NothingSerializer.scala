package io.findify.flinkadt.api.serializer

import java.util.function.Supplier

import org.apache.flink.annotation.Internal
import org.apache.flink.api.common.typeutils.{SimpleTypeSerializerSnapshot, TypeSerializer, TypeSerializerSnapshot}
import org.apache.flink.core.memory.{DataInputView, DataOutputView}

/**
 * Serializer for cases where no serializer is required but the system still expects one. This
 * happens for OptionTypeInfo when None is used, or for Either when one of the type parameters
 * is Nothing.
 */
@Internal
class NothingSerializer extends TypeSerializer[Any] {

  override def duplicate: NothingSerializer = this

  override def createInstance: Any = {
    Integer.valueOf(-1)
  }

  override def isImmutableType: Boolean = true

  override def getLength: Int = -1

  override def copy(from: Any): Any =
    throw new RuntimeException("This must not be used. You encountered a bug.")

  override def copy(from: Any, reuse: Any): Any = copy(from)

  override def copy(source: DataInputView, target: DataOutputView): Unit =
    throw new RuntimeException("This must not be used. You encountered a bug.")

  override def serialize(any: Any, target: DataOutputView): Unit =
    throw new RuntimeException("This must not be used. You encountered a bug.")

  override def deserialize(source: DataInputView): Any =
    throw new RuntimeException("This must not be used. You encountered a bug.")

  override def deserialize(reuse: Any, source: DataInputView): Any =
    throw new RuntimeException("This must not be used. You encountered a bug.")

  override def snapshotConfiguration(): TypeSerializerSnapshot[Any] =
    new NothingSerializerSnapshot

  override def equals(obj: Any): Boolean = {
    obj match {
      case _: NothingSerializer => true
      case _ => false
    }
  }

  override def hashCode(): Int = {
    classOf[NothingSerializer].hashCode()
  }
}

class NothingSerializerSnapshot extends SimpleTypeSerializerSnapshot[Any](
  new Supplier[TypeSerializer[Any]] {
    override def get(): TypeSerializer[Any] = new NothingSerializer
  }
)
