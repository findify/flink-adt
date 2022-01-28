package io.findify.flinkadt.api.typeinfo

import magnolia1.Param
import org.apache.flink.api.common.ExecutionConfig
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.common.typeutils.TypeSerializer

class ProductTypeInformation[T <: Product](c: Class[T], params: Seq[Param[TypeInformation, T]], ser: TypeSerializer[T])
    extends CaseClassTypeInfo[T](
      clazz = c,
      typeParamTypeInfos = Array(),
      fieldTypes = params.map(_.typeclass),
      fieldNames = params.map(_.label)
    ) {
  override def createSerializer(config: ExecutionConfig): TypeSerializer[T] = ser
}
