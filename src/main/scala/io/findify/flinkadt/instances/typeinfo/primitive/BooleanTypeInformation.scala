package io.findify.flinkadt.instances.typeinfo.primitive

import io.findify.flinkadt.api.typeinfo.{ AtomicTypeInformation, MappedComparator, SimpleTypeInformation }
import org.apache.flink.api.common.ExecutionConfig
import org.apache.flink.api.common.typeutils.{ TypeComparator, TypeSerializer }
import org.apache.flink.api.common.typeutils.base.BooleanComparator

case class BooleanTypeInformation(implicit s: TypeSerializer[Boolean]) extends AtomicTypeInformation[Boolean] {
  override def createComparator(sortOrderAscending: Boolean,
                                executionConfig: ExecutionConfig): TypeComparator[Boolean] =
    new MappedComparator[java.lang.Boolean, Boolean](
      java.lang.Boolean.valueOf,
      new BooleanComparator(sortOrderAscending)
    )
}
