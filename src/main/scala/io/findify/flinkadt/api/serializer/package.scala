package io.findify.flinkadt.api

import magnolia.{CaseClass, Magnolia, SealedTrait, Subtype}
import org.apache.flink.api.common.typeutils.TypeSerializer
import org.apache.flink.api.scala.typeutils.ScalaCaseClassSerializer
import scala.reflect.runtime.universe._
import scala.language.experimental.macros
import scala.reflect.{ClassTag, classTag}
import scala.util.{Failure, Success, Try}

package object serializer {
  type Typeclass[T] = TypeSerializer[T]

  def combine[T <: Product: ClassTag: TypeTag](ctx: CaseClass[TypeSerializer, T]): TypeSerializer[T] = {
    implicitly[TypeTag[T]].tpe.typeSymbol.isModuleClass match {
      case true =>
        new ScalaCaseObjectSerializer[T](classTag[T].runtimeClass.asInstanceOf[Class[T]])
      case false =>
        new ScalaCaseClassSerializer[T](
          clazz = classTag[T].runtimeClass.asInstanceOf[Class[T]],
          scalaFieldSerializers = ctx.parameters.map(_.typeclass).toArray
        )
    }
  }

  def dispatch[T](ctx: SealedTrait[TypeSerializer, T]): TypeSerializer[T] =
    new CoproductSerializer[T](
      subtypeClasses = ctx.subtypes
        .map(_.typeName)
        .map(c => guessClass(c.owner, c.short))
        .toArray,
      subtypeSerializers = ctx.subtypes.map(_.typeclass).toArray
    )

  def guessClass(owner: String, name: String): Class[_] =
    Try(Class.forName(s"$owner.$name")) match {
      case Success(c) => c
      case Failure(_) =>
        Try(Class.forName(s"${owner}$$${name}")) match {
          case Success(c) => c
          case Failure(_) =>
            Try(Class.forName(s"${owner}$$${name}$$")) match {
              case Success(c)  => c
              case Failure(ex) => throw ex
            }
        }
    }

  implicit def deriveSerializer[T]: TypeSerializer[T] = macro Magnolia.gen[T]
}
