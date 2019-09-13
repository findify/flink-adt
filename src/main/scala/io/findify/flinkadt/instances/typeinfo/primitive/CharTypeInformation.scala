package io.findify.flinkadt.instances.typeinfo.primitive

import io.findify.flinkadt.api.typeinfo.{ AtomicTypeInformation, MappedComparator }
import org.apache.flink.api.common.ExecutionConfig
import org.apache.flink.api.common.typeutils.{ TypeComparator, TypeSerializer }
import org.apache.flink.api.common.typeutils.base.{ ByteComparator, CharComparator }

case class CharTypeInformation(implicit s: TypeSerializer[Char]) extends AtomicTypeInformation[Char] {
  override def createComparator(sortOrderAscending: Boolean, executionConfig: ExecutionConfig): TypeComparator[Char] =
    new MappedComparator[java.lang.Character, Char](java.lang.Character.valueOf, new CharComparator(sortOrderAscending))
}
