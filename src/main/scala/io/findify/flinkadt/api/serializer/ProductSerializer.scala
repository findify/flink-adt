package io.findify.flinkadt.api.serializer

import java.io.{ ByteArrayInputStream, ByteArrayOutputStream, ObjectInputStream, ObjectOutputStream }

import magnolia.{ CaseClass, Param, SealedTrait }
import org.apache.flink.api.common.typeutils.{
  TypeSerializer,
  TypeSerializerSchemaCompatibility,
  TypeSerializerSnapshot
}
import org.apache.flink.api.scala.typeutils.ScalaCaseClassSerializer
import org.apache.flink.core.memory.{ DataInputView, DataOutputView }

import scala.reflect._
import _root_.io.findify.flinkadt.api.serializer.ProductSerializer.ProductSerializerSnapshot
import org.apache.flink.util.InstantiationUtil

class ProductSerializer[T <: Product](val ctx: CaseClass[TypeSerializer, T], val clz: Class[T])
    extends ScalaCaseClassSerializer[T](
      clazz = clz,
      scalaFieldSerializers = ctx.parameters.map(_.typeclass).toArray
    ) {

  // otherwise it creates duplicates of case objects
  override def createInstance(fields: Array[AnyRef]): T =
    ctx.rawConstruct(fields)

  override def snapshotConfiguration(): TypeSerializerSnapshot[T] = new ProductSerializerSnapshot[T](ctx, clz)

}

object ProductSerializer {
  class ProductSerializerSnapshot[T <: Product]() extends TypeSerializerSnapshot[T] {
    var context: CaseClass[TypeSerializer, T] = _
    var clazz: Class[T] = _
    def this(ctx: CaseClass[TypeSerializer, T], clz: Class[T]) = {
      this()
      context = ctx
      clazz = clz
    }
    override def readSnapshot(readVersion: Int, in: DataInputView, userCodeClassLoader: ClassLoader): Unit = {
      val len = in.readInt()
      val bytes = new Array[Byte](len)
      in.read(bytes)
      val buffer = new ByteArrayInputStream(bytes)
      val objStream = new ObjectInputStream(buffer)
      context = objStream.readObject().asInstanceOf[CaseClass[TypeSerializer, T]]
      clazz = InstantiationUtil.resolveClassByName[T](in, userCodeClassLoader)
    }

    override def getCurrentVersion: Int = 1

    override def writeSnapshot(out: DataOutputView): Unit = {
      val buffer = new ByteArrayOutputStream()
      val stream = new ObjectOutputStream(buffer)
      stream.writeObject(context)
      val bytes = buffer.toByteArray
      out.writeInt(bytes.length)
      out.write(bytes)
      out.writeUTF(clazz.getName)
    }

    override def resolveSchemaCompatibility(newSerializer: TypeSerializer[T]): TypeSerializerSchemaCompatibility[T] =
      TypeSerializerSchemaCompatibility.compatibleAsIs()

    override def restoreSerializer(): TypeSerializer[T] =
      new ProductSerializer[T](context, clazz)
  }

}
