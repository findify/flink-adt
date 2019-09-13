package io.findify.flinkadt.api.typeinfo

import org.apache.flink.api.common.typeutils.TypeComparator
import org.apache.flink.api.common.typeutils.base.BasicTypeComparator
import org.apache.flink.core.memory.{ DataInputView, DataOutputView, MemorySegment }

case class MappedComparator[T <: Comparable[T], S](map: S => T, parent: BasicTypeComparator[T])
    extends TypeComparator[S] {
  override def compareSerialized(firstSource: DataInputView, secondSource: DataInputView): Int =
    parent.compareSerialized(firstSource, secondSource)
  override def putNormalizedKey(record: S, target: MemorySegment, offset: Int, numBytes: Int): Unit =
    parent.putNormalizedKey(map(record), target, offset, numBytes)
  override def isNormalizedKeyPrefixOnly(keyBytes: Int): Boolean = parent.isNormalizedKeyPrefixOnly(keyBytes)
  override def supportsNormalizedKey(): Boolean = parent.supportsNormalizedKey()
  override def getNormalizeKeyLen: Int = parent.getNormalizeKeyLen
  override def duplicate(): TypeComparator[S] = new MappedComparator(map, parent)

  override def invertNormalizedKey(): Boolean = parent.invertNormalizedKey()

  override def writeWithKeyNormalization(record: S, target: DataOutputView): Unit =
    parent.writeWithKeyNormalization(map(record), target)

  override def hash(record: S): Int = parent.hash(map(record))

  override def compare(first: S, second: S): Int = parent.compare(map(first), map(second))

  override def compareToReference(referencedComparator: TypeComparator[S]): Int = ???

  override def setReference(toCompare: S): Unit = parent.setReference(map(toCompare))

  override def getFlatComparators: Array[TypeComparator[_]] = parent.getFlatComparators

  override def equalToReference(candidate: S): Boolean = parent.equalToReference(map(candidate))

  override def supportsSerializationWithKeyNormalization(): Boolean = false

  override def readWithKeyDenormalization(reuse: S, source: DataInputView): S = ???

  override def extractKeys(record: Any, target: Array[AnyRef], index: Int): Int =
    parent.extractKeys(record, target, index)
}
