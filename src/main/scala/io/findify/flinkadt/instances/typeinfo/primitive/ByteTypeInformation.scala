package io.findify.flinkadt.instances.typeinfo.primitive

import io.findify.flinkadt.api.typeinfo.{ AtomicTypeInformation, MappedComparator }
import org.apache.flink.api.common.ExecutionConfig
import org.apache.flink.api.common.typeutils.{ TypeComparator, TypeSerializer }
import org.apache.flink.api.common.typeutils.base.{ ByteComparator, IntComparator }

case class ByteTypeInformation(implicit s: TypeSerializer[Byte]) extends AtomicTypeInformation[Byte] {
  override def createComparator(sortOrderAscending: Boolean, executionConfig: ExecutionConfig): TypeComparator[Byte] =
    new MappedComparator[java.lang.Byte, Byte](java.lang.Byte.valueOf, new ByteComparator(sortOrderAscending))
}
