package io.findify.flinkadt.instances.typeinfo.primitive

import io.findify.flinkadt.api.typeinfo.{ AtomicTypeInformation, MappedComparator }
import org.apache.flink.api.common.ExecutionConfig
import org.apache.flink.api.common.typeutils.{ TypeComparator, TypeSerializer }
import org.apache.flink.api.common.typeutils.base.{ DoubleComparator, IntComparator }

case class DoubleTypeInformation(implicit s: TypeSerializer[Double]) extends AtomicTypeInformation[Double] {
  override def createComparator(sortOrderAscending: Boolean, executionConfig: ExecutionConfig): TypeComparator[Double] =
    new MappedComparator[java.lang.Double, Double](java.lang.Double.valueOf, new DoubleComparator(sortOrderAscending))
}
