package io.findify.flinkadt.instances.typeinfo.primitive

import io.findify.flinkadt.api.typeinfo.{ AtomicTypeInformation, MappedComparator }
import org.apache.flink.api.common.ExecutionConfig
import org.apache.flink.api.common.typeutils.{ TypeComparator, TypeSerializer }
import org.apache.flink.api.common.typeutils.base.{ DoubleComparator, ShortComparator }

case class ShortTypeInformation(implicit s: TypeSerializer[Short]) extends AtomicTypeInformation[Short] {
  override def createComparator(sortOrderAscending: Boolean, executionConfig: ExecutionConfig): TypeComparator[Short] =
    new MappedComparator[java.lang.Short, Short](java.lang.Short.valueOf, new ShortComparator(sortOrderAscending))
}
