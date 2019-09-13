package io.findify.flinkadt.api.serializer

import magnolia.{ CaseClass, Param }
import org.apache.flink.api.common.typeutils.{ SimpleTypeSerializerSnapshot, TypeSerializer }
import org.apache.flink.api.scala.typeutils.ScalaCaseClassSerializer

import scala.reflect._

class ProductSerializer[T <: Product: ClassTag](val ctx: CaseClass[TypeSerializer, T])
    extends ScalaCaseClassSerializer[T](
      clazz = classTag[T].runtimeClass.asInstanceOf[Class[T]],
      scalaFieldSerializers = ctx.parameters.map(_.typeclass).toArray
    ) {

  // otherwise it creates duplicates of case objects
  override def createInstance(fields: Array[AnyRef]): T =
    ctx.rawConstruct(fields)
}

object ProductSerializer {
  class ProductSerializerSnapshot[T](self: TypeSerializer[T]) extends SimpleTypeSerializerSnapshot[T](() => self)
}
