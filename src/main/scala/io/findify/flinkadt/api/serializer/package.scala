package io.findify.flinkadt.api

import magnolia.{ CaseClass, Magnolia, SealedTrait }
import org.apache.flink.api.common.typeutils.TypeSerializer

import scala.collection.mutable
import scala.language.experimental.macros
import scala.reflect.ClassTag

package object serializer {
  type Typeclass[T] = TypeSerializer[T]

  def combine[T <: Product: ClassTag](ctx: CaseClass[TypeSerializer, T]): TypeSerializer[T] = {
    // super ugly hack to make annotations array serializable
    // as some of annotations like SerialVersionUID itself are not serializable
    val ann = ctx.annotations.asInstanceOf[mutable.WrappedArray[Any]]
    ann.indices.foreach(i => ann.update(i, null))
    new ProductSerializer[T](ctx)
  }

  def dispatch[T](ctx: SealedTrait[TypeSerializer, T]): TypeSerializer[T] = {
    val ann = ctx.annotations.asInstanceOf[mutable.WrappedArray[Any]]
    ann.indices.foreach(i => ann.update(i, null))
    new CoproductSerializer[T](ctx)
  }

  implicit def deriveSerializer[T]: TypeSerializer[T] = macro Magnolia.gen[T]
}
