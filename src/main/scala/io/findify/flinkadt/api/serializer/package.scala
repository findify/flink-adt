package io.findify.flinkadt.api

import magnolia.{ CaseClass, Magnolia, SealedTrait }
import org.apache.flink.api.common.typeutils.TypeSerializer

import scala.language.experimental.macros
import scala.reflect.ClassTag

package object serializer {
  type Typeclass[T] = TypeSerializer[T]

  def combine[T <: Product: ClassTag](ctx: CaseClass[TypeSerializer, T]): TypeSerializer[T] =
    new ProductSerializer[T](ctx)

  def dispatch[T](ctx: SealedTrait[TypeSerializer, T]): TypeSerializer[T] =
    new CoproductSerializer[T](ctx)

  implicit def deriveSerializer[T]: TypeSerializer[T] = macro Magnolia.gen[T]
}
