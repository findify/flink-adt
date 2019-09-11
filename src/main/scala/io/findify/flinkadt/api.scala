package io.findify.flinkadt


import magnolia.{CaseClass, Magnolia, SealedTrait}
import org.apache.flink.api.common.ExecutionConfig
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.common.typeutils.TypeSerializer

import scala.language.experimental.macros
import scala.reflect.ClassTag

object api {

  lazy val ec = new ExecutionConfig()
  type Typeclass[T] = TypeSerializer[T]

  def combine[T](ctx: CaseClass[TypeSerializer, T]): TypeSerializer[T] = new ProductSerializer[T](ctx.parameters.toArray, ctx)

  def dispatch[T](ctx: SealedTrait[TypeSerializer, T]): TypeSerializer[T] = {
    val arr = ctx.subtypes.toArray
    new CoproductSerializer[T](arr)
  }

  implicit def gen[T]: TypeSerializer[T] = macro Magnolia.gen[T]

  implicit def typeInfo[T: ClassTag](implicit serializer: TypeSerializer[T]): TypeInformation[T] =
    ProductTypeInfo[T](serializer)

  implicit def defaultSerializer[T](implicit ti: TypeInformation[T]): TypeSerializer[T] = ti.createSerializer(ec)

}
