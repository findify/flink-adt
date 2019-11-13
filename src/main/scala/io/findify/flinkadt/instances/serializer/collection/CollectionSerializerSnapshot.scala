package io.findify.flinkadt.instances.serializer.collection

import org.apache.flink.api.common.typeutils.{
  TypeSerializer,
  TypeSerializerSchemaCompatibility,
  TypeSerializerSnapshot
}
import org.apache.flink.core.memory.{ DataInputView, DataOutputView }
import org.apache.flink.util.InstantiationUtil

case class CollectionSerializerSnapshot[F[_], T, S <: TypeSerializer[F[T]]](nested: TypeSerializer[T],
                                                                            build: TypeSerializer[T] => S)
    extends TypeSerializerSnapshot[F[T]] {
  var nestedSerializer: TypeSerializer[T] = nested
  override def getCurrentVersion: Int = 1

  override def readSnapshot(readVersion: Int, in: DataInputView, userCodeClassLoader: ClassLoader): Unit = {
    val snapClass = InstantiationUtil.resolveClassByName[TypeSerializerSnapshot[T]](in, userCodeClassLoader)
    val nestedSnapshot = InstantiationUtil.instantiate(snapClass)
    nestedSnapshot.readSnapshot(nestedSnapshot.getCurrentVersion, in, userCodeClassLoader)
    nestedSerializer = nestedSnapshot.restoreSerializer()
  }

  override def writeSnapshot(out: DataOutputView): Unit = {
    out.writeUTF(nestedSerializer.snapshotConfiguration().getClass.getName)
    nestedSerializer.snapshotConfiguration().writeSnapshot(out)
  }

  override def resolveSchemaCompatibility(
      newSerializer: TypeSerializer[F[T]]
  ): TypeSerializerSchemaCompatibility[F[T]] = TypeSerializerSchemaCompatibility.compatibleAsIs()

  override def restoreSerializer(): TypeSerializer[F[T]] =
    build(nestedSerializer)

}
