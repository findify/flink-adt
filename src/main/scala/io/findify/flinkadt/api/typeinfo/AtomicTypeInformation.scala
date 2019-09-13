package io.findify.flinkadt.api.typeinfo

import org.apache.flink.api.common.typeinfo.AtomicType
import org.apache.flink.api.common.typeutils.TypeSerializer

import scala.reflect.ClassTag

abstract class AtomicTypeInformation[T: ClassTag: TypeSerializer] extends SimpleTypeInformation[T] with AtomicType[T] {
  override def isBasicType: Boolean = true
  override def isKeyType: Boolean = true
}
