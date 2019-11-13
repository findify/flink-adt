package io.findify.flinkadt.instances.serializer.collection

import io.findify.flinkadt.api.serializer.SimpleSerializer
import io.findify.flinkadt.instances.serializer.collection.MapSerializer.MapSerializerSnapshot
import org.apache.flink.api.common.typeutils.{
  SimpleTypeSerializerSnapshot,
  TypeSerializer,
  TypeSerializerSchemaCompatibility,
  TypeSerializerSnapshot
}
import org.apache.flink.core.memory.{ DataInputView, DataOutputView }
import org.apache.flink.util.InstantiationUtil

import scala.reflect.ClassTag

class MapSerializer[K, V](ks: TypeSerializer[K], vs: TypeSerializer[V]) extends SimpleSerializer[Map[K, V]] {
  override def createInstance(): Map[K, V] = Map.empty[K, V]
  override def getLength: Int = -1
  override def deserialize(source: DataInputView): Map[K, V] = {
    val count = source.readInt()
    val result = for {
      _ <- 0 until count
    } yield {
      val key = ks.deserialize(source)
      val value = vs.deserialize(source)
      key -> value
    }
    result.toMap
  }
  override def serialize(record: Map[K, V], target: DataOutputView): Unit = {
    target.writeInt(record.size)
    record.foreach(element => {
      ks.serialize(element._1, target)
      vs.serialize(element._2, target)
    })
  }
  override def snapshotConfiguration(): TypeSerializerSnapshot[Map[K, V]] = new MapSerializerSnapshot(ks, vs)
}

object MapSerializer {
  case class MapSerializerSnapshot[K, V](var keySerializer: TypeSerializer[K], var valueSerializer: TypeSerializer[V])
      extends TypeSerializerSnapshot[Map[K, V]] {
    override def getCurrentVersion: Int = 1

    override def readSnapshot(readVersion: Int, in: DataInputView, userCodeClassLoader: ClassLoader): Unit = {
      keySerializer = readSerializer[K](readVersion, in, userCodeClassLoader)
      valueSerializer = readSerializer[V](readVersion, in, userCodeClassLoader)
    }

    def readSerializer[T](readVersion: Int, in: DataInputView, userCodeClassLoader: ClassLoader) = {
      val snapClass = InstantiationUtil.resolveClassByName[TypeSerializerSnapshot[T]](in, userCodeClassLoader)
      val nestedSnapshot = InstantiationUtil.instantiate(snapClass)
      nestedSnapshot.readSnapshot(nestedSnapshot.getCurrentVersion, in, userCodeClassLoader)
      nestedSnapshot.restoreSerializer()
    }

    override def writeSnapshot(out: DataOutputView): Unit = {
      writeSerializer[K](keySerializer, out)
      writeSerializer[V](valueSerializer, out)
    }

    def writeSerializer[T](nestedSerializer: TypeSerializer[T], out: DataOutputView) = {
      out.writeUTF(nestedSerializer.snapshotConfiguration().getClass.getName)
      nestedSerializer.snapshotConfiguration().writeSnapshot(out)
    }

    override def resolveSchemaCompatibility(
        newSerializer: TypeSerializer[Map[K, V]]
    ): TypeSerializerSchemaCompatibility[Map[K, V]] = TypeSerializerSchemaCompatibility.compatibleAsIs()

    override def restoreSerializer(): TypeSerializer[Map[K, V]] = new MapSerializer(keySerializer, valueSerializer)
  }

}
