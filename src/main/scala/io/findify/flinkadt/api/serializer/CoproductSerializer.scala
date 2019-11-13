package io.findify.flinkadt.api.serializer

import java.io.{ ByteArrayInputStream, ByteArrayOutputStream, ObjectInputStream, ObjectOutputStream }

import io.findify.flinkadt.api.serializer.CoproductSerializer.CoproductSerializerSnapshot
import magnolia.{ SealedTrait, Subtype, TypeName }
import org.apache.flink.api.common.typeutils.base.TypeSerializerSingleton
import org.apache.flink.api.common.typeutils.{
  GenericTypeSerializerSnapshot,
  SimpleTypeSerializerSnapshot,
  TypeSerializer,
  TypeSerializerSchemaCompatibility,
  TypeSerializerSnapshot
}
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
  override def snapshotConfiguration(): TypeSerializerSnapshot[T] = new CoproductSerializerSnapshot(ctx)
}

object CoproductSerializer {
  class CoproductSerializerSnapshot[T]() extends TypeSerializerSnapshot[T] {
    var context: SealedTrait[TypeSerializer, T] = _
    def this(ctx: SealedTrait[TypeSerializer, T]) = {
      this()
      context = ctx
    }
    override def readSnapshot(readVersion: Int, in: DataInputView, userCodeClassLoader: ClassLoader): Unit = {
      val len = in.readInt()
      val bytes = new Array[Byte](len)
      in.read(bytes)
      val buffer = new ByteArrayInputStream(bytes)
      val objStream = new ObjectInputStream(buffer)
      context = objStream.readObject().asInstanceOf[SealedTrait[TypeSerializer, T]]
    }

    override def getCurrentVersion: Int = 1

    override def writeSnapshot(out: DataOutputView): Unit = {
      val buffer = new ByteArrayOutputStream()
      val stream = new ObjectOutputStream(buffer)
      stream.writeObject(context)
      val bytes = buffer.toByteArray
      out.writeInt(bytes.length)
      out.write(bytes)
    }

    override def resolveSchemaCompatibility(newSerializer: TypeSerializer[T]): TypeSerializerSchemaCompatibility[T] =
      TypeSerializerSchemaCompatibility.compatibleAsIs()

    override def restoreSerializer(): TypeSerializer[T] =
      new CoproductSerializer[T](context)
  }
}
