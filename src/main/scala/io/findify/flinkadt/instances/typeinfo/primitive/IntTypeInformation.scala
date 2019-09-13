package io.findify.flinkadt.instances.typeinfo.primitive

import io.findify.flinkadt.api.typeinfo.{ AtomicTypeInformation, MappedComparator }
import org.apache.flink.api.common.ExecutionConfig
import org.apache.flink.api.common.typeutils.base.IntComparator
import org.apache.flink.api.common.typeutils.{ TypeComparator, TypeSerializer }

case class IntTypeInformation(implicit s: TypeSerializer[Int]) extends AtomicTypeInformation[Int] {
  override def createComparator(sortOrderAscending: Boolean, executionConfig: ExecutionConfig): TypeComparator[Int] =
    new MappedComparator[Integer, Int](Integer.valueOf, new IntComparator(sortOrderAscending))
}
