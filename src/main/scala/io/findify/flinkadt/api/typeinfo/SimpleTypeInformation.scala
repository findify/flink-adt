package io.findify.flinkadt.api.typeinfo

import org.apache.flink.api.common.ExecutionConfig
import org.apache.flink.api.common.typeinfo.{AtomicType, TypeInformation}
import org.apache.flink.api.common.typeutils.{TypeComparator, TypeSerializer}

import scala.reflect.{classTag, ClassTag}

abstract class SimpleTypeInformation[T: ClassTag: TypeSerializer] extends TypeInformation[T] {
  override def createSerializer(config: ExecutionConfig): TypeSerializer[T] = implicitly[TypeSerializer[T]]
  override def isBasicType: Boolean                                         = false
  override def isTupleType: Boolean                                         = false
  override def isKeyType: Boolean                                           = false
  override def getTotalFields: Int                                          = 1
  override def getTypeClass: Class[T]                                       = classTag[T].runtimeClass.asInstanceOf[Class[T]]
  override def getArity: Int                                                = 1
}
