package io.findify.flinkadt.api.serializer

import org.apache.flink.annotation.Internal
import org.apache.flink.api.common.typeutils.TypeSerializer
import org.apache.flink.api.java.typeutils.runtime.TupleSerializerBase
import org.apache.flink.core.memory.{DataInputView, DataOutputView}
import org.apache.flink.types.NullFieldException

/**
 * Serializer for Case Classes. Creation and access is different from
 * our Java Tuples so we have to treat them differently.
 * Copied from Flink 1.14.
 */
@Internal
@SerialVersionUID(7341356073446263475L)
abstract class CaseClassSerializer[T <: Product](
  clazz: Class[T],
  scalaFieldSerializers: Array[TypeSerializer[_]]
) extends TupleSerializerBase[T](clazz, scalaFieldSerializers)
    with Cloneable {

  @transient var fields: Array[AnyRef] = _

  @transient var instanceCreationFailed: Boolean = false

  override def duplicate: CaseClassSerializer[T] = {
    clone().asInstanceOf[CaseClassSerializer[T]]
  }

  @throws[CloneNotSupportedException]
  override protected def clone(): Object = {
    val result = super.clone().asInstanceOf[CaseClassSerializer[T]]

    // achieve a deep copy by duplicating the field serializers
    result.fieldSerializers = result.fieldSerializers.map(_.duplicate())
    result.fields = null
    result.instanceCreationFailed = false

    result
  }

  def createInstance: T = {
    if (instanceCreationFailed) {
      null.asInstanceOf[T]
    }
    else {
      initArray()
      try {
        var i = 0
        while (i < arity) {
          fields(i) = fieldSerializers(i).createInstance()
          i += 1
        }
        createInstance(fields)
      }
      catch {
        case t: Throwable =>
          instanceCreationFailed = true
          null.asInstanceOf[T]
      }
    }
  }

  override def createOrReuseInstance(fields: Array[Object], reuse: T) : T = {
    createInstance(fields)
  }

  def copy(from: T, reuse: T): T = {
    copy(from)
  }

  def copy(from: T): T = {
    if (from == null) {
      null.asInstanceOf[T]
    }
    else {
      initArray()
      var i = 0
      while (i < arity) {
        fields(i) = fieldSerializers(i).copy(from.productElement(i).asInstanceOf[AnyRef])
        i += 1
      }
      createInstance(fields)
    }
  }

  def serialize(value: T, target: DataOutputView): Unit = {
    var i = 0
    while (i < arity) {
      val serializer = fieldSerializers(i).asInstanceOf[TypeSerializer[Any]]
      val o = value.productElement(i)
      try
        serializer.serialize(o, target)
      catch {
        case e: NullPointerException =>
          throw new NullFieldException(i, e)
      }
      i += 1
    }
  }

  def deserialize(reuse: T, source: DataInputView): T = {
    deserialize(source)
  }

  def deserialize(source: DataInputView): T = {
    initArray()
    var i = 0
    while (i < arity) {
      fields(i) = fieldSerializers(i).deserialize(source)
      i += 1
    }
    createInstance(fields)
  }

  def initArray(): Unit = {
    if (fields == null) {
      fields = new Array[AnyRef](arity)
    }
  }
}
