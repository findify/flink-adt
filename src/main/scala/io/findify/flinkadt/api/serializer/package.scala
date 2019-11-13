package io.findify.flinkadt.api

import magnolia.{ CaseClass, Magnolia, SealedTrait, Subtype }
import org.apache.flink.api.common.typeutils.TypeSerializer

import scala.collection.mutable
import scala.language.experimental.macros
import scala.reflect.{ classTag, ClassTag }

package object serializer {
  type Typeclass[T] = TypeSerializer[T]

  def combine[T <: Product: ClassTag](ctx: CaseClass[TypeSerializer, T]): TypeSerializer[T] = {
    // super ugly hack to make annotations array serializable
    // as some of annotations like SerialVersionUID itself are not serializable
    val ann = ctx.annotations.asInstanceOf[mutable.WrappedArray[Any]]
    ann.indices.foreach(i => ann.update(i, null))
    new ProductSerializer[T](ctx, classTag[T].runtimeClass.asInstanceOf[Class[T]])
  }

  def dispatch[T](ctx: SealedTrait[TypeSerializer, T]): TypeSerializer[T] = {
    // we wipe all top-level annotations
    val ann = ctx.annotations.asInstanceOf[mutable.WrappedArray[Any]]
    ann.indices.foreach(i => ann.update(i, null))
    // and all annotations on subtypes
    val subtypes = ctx.subtypes.asInstanceOf[mutable.WrappedArray[Subtype[Typeclass, _]]]
    for {
      subtype <- subtypes
      ai      <- subtype.annotationsArray.indices
    } {
      subtype.annotationsArray(ai) = null
    }
    new CoproductSerializer[T](ctx)
  }

  implicit def deriveSerializer[T]: TypeSerializer[T] = macro Magnolia.gen[T]
}
