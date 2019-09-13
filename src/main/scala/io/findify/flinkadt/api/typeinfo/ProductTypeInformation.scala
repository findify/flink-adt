package io.findify.flinkadt.api.typeinfo

import magnolia.CaseClass
import org.apache.flink.api.common.ExecutionConfig
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.common.typeutils.TypeSerializer
import org.apache.flink.api.scala.typeutils.CaseClassTypeInfo

import scala.reflect._

class ProductTypeInformation[T <: Product: ClassTag: TypeSerializer](ctx: CaseClass[TypeInformation, T])
    extends CaseClassTypeInfo[T](
      clazz = classTag[T].runtimeClass.asInstanceOf[Class[T]],
      typeParamTypeInfos = Array(),
      fieldTypes = ctx.parameters.map(_.typeclass),
      fieldNames = ctx.parameters.map(_.label)
    ) {
  override def createSerializer(config: ExecutionConfig): TypeSerializer[T] = implicitly[TypeSerializer[T]]
}
