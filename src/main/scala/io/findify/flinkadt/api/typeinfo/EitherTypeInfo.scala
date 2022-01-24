package io.findify.flinkadt.api.typeinfo

import io.findify.flinkadt.api.serializer.{EitherSerializer, NothingSerializer}
import org.apache.flink.annotation.{Public, PublicEvolving}
import org.apache.flink.api.common.ExecutionConfig
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.common.typeutils.TypeSerializer

import scala.collection.JavaConverters._

/**
 * TypeInformation [[Either]].
 */
@Public
class EitherTypeInfo[A, B, T <: Either[A, B]](
  val clazz: Class[T],
  val leftTypeInfo: TypeInformation[A],
  val rightTypeInfo: TypeInformation[B]
) extends TypeInformation[T] {

  @PublicEvolving
  override def isBasicType: Boolean = false
  @PublicEvolving
  override def isTupleType: Boolean = false
  @PublicEvolving
  override def isKeyType: Boolean = false
  @PublicEvolving
  override def getTotalFields: Int = 1
  @PublicEvolving
  override def getArity: Int = 1
  @PublicEvolving
  override def getTypeClass: Class[T] = clazz
  @PublicEvolving
  override def getGenericParameters: java.util.Map[String, TypeInformation[_]] =
    Map[String, TypeInformation[_]]("A" -> leftTypeInfo, "B" -> rightTypeInfo).asJava

  @PublicEvolving
  def createSerializer(executionConfig: ExecutionConfig): TypeSerializer[T] = {
    val leftSerializer: TypeSerializer[A] = if (leftTypeInfo != null) {
      leftTypeInfo.createSerializer(executionConfig)
    } else {
      (new NothingSerializer).asInstanceOf[TypeSerializer[A]]
    }

    val rightSerializer: TypeSerializer[B] = if (rightTypeInfo != null) {
      rightTypeInfo.createSerializer(executionConfig)
    } else {
      (new NothingSerializer).asInstanceOf[TypeSerializer[B]]
    }
    new EitherSerializer[A, B](leftSerializer, rightSerializer).asInstanceOf[TypeSerializer[T]]
  }

  override def equals(obj: Any): Boolean = {
    obj match {
      case eitherTypeInfo: EitherTypeInfo[_, _, _] =>
        eitherTypeInfo.canEqual(this) &&
          clazz.equals(eitherTypeInfo.clazz) &&
          leftTypeInfo.equals(eitherTypeInfo.leftTypeInfo) &&
          rightTypeInfo.equals(eitherTypeInfo.rightTypeInfo)
      case _ => false
    }
  }

  override def canEqual(obj: Any): Boolean = {
    obj.isInstanceOf[EitherTypeInfo[_, _, _]]
  }

  override def hashCode(): Int = {
    31 * (31 * clazz.hashCode() + leftTypeInfo.hashCode()) + rightTypeInfo.hashCode()
  }

  override def toString = s"Either[$leftTypeInfo, $rightTypeInfo]"
}
