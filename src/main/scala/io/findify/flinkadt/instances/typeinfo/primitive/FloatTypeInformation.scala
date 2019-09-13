package io.findify.flinkadt.instances.typeinfo.primitive

import io.findify.flinkadt.api.typeinfo.{ AtomicTypeInformation, MappedComparator }
import org.apache.flink.api.common.ExecutionConfig
import org.apache.flink.api.common.typeutils.{ TypeComparator, TypeSerializer }
import org.apache.flink.api.common.typeutils.base.{ DoubleComparator, FloatComparator }

case class FloatTypeInformation(implicit s: TypeSerializer[Float]) extends AtomicTypeInformation[Float] {
  override def createComparator(sortOrderAscending: Boolean, executionConfig: ExecutionConfig): TypeComparator[Float] =
    new MappedComparator[java.lang.Float, Float](java.lang.Float.valueOf, new FloatComparator(sortOrderAscending))
}
