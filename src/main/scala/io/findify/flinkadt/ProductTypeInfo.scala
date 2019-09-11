package io.findify.flinkadt

import magnolia.CaseClass
import org.apache.flink.api.common.ExecutionConfig
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.common.typeutils.TypeSerializer

import scala.reflect.{ClassTag, _}

case class ProductTypeInfo[T](clazz: Class[T], serializer: TypeSerializer[T]) extends TypeInformation[T] {
  override def isBasicType: Boolean = false
  override def isTupleType: Boolean = false
  override def getArity: Int = 1
  override def getTotalFields: Int = clazz.getDeclaredFields.length // hack!
  override def getTypeClass: Class[T] = clazz
  override def isKeyType: Boolean = true
  override def createSerializer(config: ExecutionConfig): TypeSerializer[T] = serializer
  override def canEqual(obj: Any): Boolean = clazz.isInstance(obj)
}

object ProductTypeInfo {
  def apply[T: ClassTag](serializer: TypeSerializer[T]) = new ProductTypeInfo[T](
    clazz = classTag[T].runtimeClass.asInstanceOf[Class[T]],
    serializer = serializer
  )
}