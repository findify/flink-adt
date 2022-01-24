package io.findify.flinkadt.api.serializer

import io.findify.flinkadt.api.serializer.ScalaCaseClassSerializer.lookupConstructor
import org.apache.flink.api.common.typeutils.{TypeSerializer, TypeSerializerSnapshot}

import java.io.ObjectInputStream

import scala.annotation.nowarn
import scala.reflect.runtime.universe

/**
 * This is a non macro-generated, concrete Scala case class serializer.
 * Copied from Flink 1.14 without `SelfResolvingTypeSerializer`.
 */
@SerialVersionUID(1L)
class ScalaCaseClassSerializer[T <: Product](
  clazz: Class[T],
  scalaFieldSerializers: Array[TypeSerializer[_]]
) extends CaseClassSerializer[T](clazz, scalaFieldSerializers) {

  @transient
  private var constructor = lookupConstructor(clazz)

  override def createInstance(fields: Array[AnyRef]): T = {
    constructor(fields)
  }

  override def snapshotConfiguration(): TypeSerializerSnapshot[T] =
    new ScalaCaseClassSerializerSnapshot[T](this)

  // Do NOT delete this method, it is used by ser/de even though it is private.
  // This should be removed once we make sure that serializer are no long java serialized.
  private def readObject(in: ObjectInputStream): Unit = {
    in.defaultReadObject()
    constructor = lookupConstructor(clazz)
  }
}

object ScalaCaseClassSerializer {
  @nowarn("msg=eliminated by erasure")
  def lookupConstructor[T](cls: Class[T]): Array[AnyRef] => T = {
    val rootMirror = universe.runtimeMirror(cls.getClassLoader)
    val classSymbol = rootMirror.classSymbol(cls)

    require(
      classSymbol.isStatic,
      s"""
         |The class ${cls.getSimpleName} is an instance class, meaning it is not a member of a
         |toplevel object, or of an object contained in a toplevel object,
         |therefore it requires an outer instance to be instantiated, but we don't have a
         |reference to the outer instance. Please consider changing the outer class to an object.
         |""".stripMargin
    )

    val primaryConstructorSymbol = classSymbol.toType
      .decl(universe.termNames.CONSTRUCTOR)
      .alternatives
      .collectFirst {
        case constructorSymbol: universe.MethodSymbol if constructorSymbol.isPrimaryConstructor =>
          constructorSymbol
      }
      .head
      .asMethod

    val classMirror = rootMirror.reflectClass(classSymbol)
    val constructorMethodMirror = classMirror.reflectConstructor(primaryConstructorSymbol)

    arr: Array[AnyRef] => {
      constructorMethodMirror.apply(arr: _*).asInstanceOf[T]
    }
  }
}
