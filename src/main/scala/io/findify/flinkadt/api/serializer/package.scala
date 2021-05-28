package io.findify.flinkadt.api

import magnolia.{CaseClass, Magnolia, SealedTrait, Subtype}
import org.apache.flink.api.common.typeutils.TypeSerializer
import org.apache.flink.api.scala.typeutils.ScalaCaseClassSerializer
import org.apache.flink.shaded.guava18.com.google.common.reflect.ClassPath

import scala.annotation.tailrec
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

  def dispatch[T](ctx: SealedTrait[TypeSerializer, T]): TypeSerializer[T] = {
    new CoproductSerializer[T](
      subtypeClasses = ctx.subtypes
        .map(_.typeName)
        .map(c => guessClass(c.full).getOrElse(throw new ClassNotFoundException(c.full)))
        .toArray,
      subtypeSerializers = ctx.subtypes.map(_.typeclass).toArray
    )
  }

  private def loadClass(name: String):Option[Class[_]] = Try(Class.forName(name)) match {
    case Failure(_) => Try(Class.forName(name + "$")) match {
      case Failure(_) => None
      case Success(value) => Some(value)
    }
    case Success(value) => Some(value)
  }
  private def replaceLast(str: String, what: Char, dest: Char): Option[String] = {
    str.lastIndexOf(what) match {
      case -1 => None
      case pos =>
        val arr = str.toCharArray
        arr(pos) = dest
        Some(new String(arr))
    }
  }
  @tailrec private def guessClass(name: String): Option[Class[_]]= {
    loadClass(name) match {
      case Some(value) => Some(value)
      case None => replaceLast(name, '.', '$') match {
        case None => None
        case Some(next) => guessClass(next)
      }
    }
  }

  implicit def deriveSerializer[T]: TypeSerializer[T] = macro Magnolia.gen[T]
}
