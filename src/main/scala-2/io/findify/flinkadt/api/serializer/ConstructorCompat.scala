package io.findify.flinkadt.api.serializer

import scala.annotation.nowarn
import scala.reflect.runtime.universe

private[serializer] trait ConstructorCompat {
  @nowarn("msg=(eliminated by erasure)|(explicit array)")
  def lookupConstructor[T](cls: Class[T]): Array[AnyRef] => T = {
    val rootMirror  = universe.runtimeMirror(cls.getClassLoader)
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

    val classMirror             = rootMirror.reflectClass(classSymbol)
    val constructorMethodMirror = classMirror.reflectConstructor(primaryConstructorSymbol)

    (arr: Array[AnyRef]) => {
      constructorMethodMirror.apply(arr: _*).asInstanceOf[T]
    }
  }
}
