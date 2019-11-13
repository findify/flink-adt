package io.findify.flinkadt.instances

import cats.data.NonEmptyList
import io.findify.flinkadt.instances.serializer.collection._
import io.findify.flinkadt.instances.serializer.primitive._
import io.findify.flinkadt.instances.typeinfo.collection.{
  ArrayTypeInformation,
  ListTypeInformation,
  MapTypeInformation,
  NonEmptyListTypeInformation,
  SeqTypeInformation,
  SetTypeInformation,
  VectorTypeInformation
}
import io.findify.flinkadt.instances.typeinfo.primitive.{
  BooleanTypeInformation,
  ByteTypeInformation,
  CharTypeInformation,
  DoubleTypeInformation,
  FloatTypeInformation,
  IntTypeInformation,
  LongTypeInformation,
  ShortTypeInformation
}
import org.apache.flink.api.common.typeinfo.{ BasicTypeInfo, TypeInformation }
import org.apache.flink.api.common.typeutils.{ TypeComparator, TypeSerializer }
import org.apache.flink.api.common.typeutils.base.{ StringComparator, StringSerializer }
import org.apache.flink.api.common.typeutils.base.array._
import org.apache.flink.api.scala.typeutils.{ EitherSerializer, OptionSerializer }

import scala.reflect.{ classTag, ClassTag }

object all {

  // serializers
  implicit val stringSerializer: TypeSerializer[String] = new StringSerializer()
  implicit val intSerializer: TypeSerializer[Int] = new IntSerializer()
  implicit val longSerializer: TypeSerializer[Long] = new LongSerializer()
  implicit val floatSerializer: TypeSerializer[Float] = new FloatSerializer()
  implicit val doubleSerializer: TypeSerializer[Double] = new DoubleSerializer()
  implicit val booleanSerializer: TypeSerializer[Boolean] = new BooleanSerializer()
  implicit val byteSerializer: TypeSerializer[Byte] = new ByteSerializer()
  implicit val charSerializer: TypeSerializer[Char] = new CharSerializer()
  implicit val shortSerializer: TypeSerializer[Short] = new ShortSerializer()

  implicit def optionSerializer[T](implicit vs: TypeSerializer[T]): TypeSerializer[Option[T]] =
    new OptionSerializer[T](vs)
  implicit def listSerializer[T: ClassTag](implicit vs: TypeSerializer[T]): TypeSerializer[List[T]] =
    new ListSerializer[T](vs, classTag[T].runtimeClass.asInstanceOf[Class[T]])
  implicit def vectorSerializer[T: ClassTag](implicit vs: TypeSerializer[T]): TypeSerializer[Vector[T]] =
    new VectorSerializer[T](vs, classTag[T].runtimeClass.asInstanceOf[Class[T]])
  implicit def arraySerializer[T: ClassTag](implicit vs: TypeSerializer[T]): TypeSerializer[Array[T]] =
    new ArraySerializer[T](vs, classTag[T].runtimeClass.asInstanceOf[Class[T]])
  implicit def setSerializer[T: ClassTag](implicit vs: TypeSerializer[T]): TypeSerializer[Set[T]] =
    new SetSerializer[T](vs, classTag[T].runtimeClass.asInstanceOf[Class[T]])
  implicit def mapSerializer[K: ClassTag, V: ClassTag](implicit ks: TypeSerializer[K],
                                                       vs: TypeSerializer[V]): TypeSerializer[Map[K, V]] =
    new MapSerializer[K, V](ks, vs)
  implicit def seqSerializer[T: ClassTag](implicit vs: TypeSerializer[T]): TypeSerializer[Seq[T]] =
    new SeqSerializer[T](vs, classTag[T].runtimeClass.asInstanceOf[Class[T]])
  implicit def nelSerializer[T: ClassTag](implicit vs: TypeSerializer[T]): TypeSerializer[NonEmptyList[T]] =
    new NonEmptyListSerializer[T](vs, classTag[T].runtimeClass.asInstanceOf[Class[T]])
  implicit def eitherSerializer[L: ClassTag, R: ClassTag](implicit ls: TypeSerializer[L], rs: TypeSerializer[R]) =
    new EitherSerializer[L, R](ls, rs)

  implicit val intArraySerializer: TypeSerializer[Array[Int]] = new IntPrimitiveArraySerializer()
  implicit val longArraySerializer: TypeSerializer[Array[Long]] = new LongPrimitiveArraySerializer()
  implicit val floatArraySerializer: TypeSerializer[Array[Float]] = new FloatPrimitiveArraySerializer()
  implicit val doubleArraySerializer: TypeSerializer[Array[Double]] = new DoublePrimitiveArraySerializer()
  implicit val booleanArraySerializer: TypeSerializer[Array[Boolean]] = new BooleanPrimitiveArraySerializer()
  implicit val byteArraySerializer: TypeSerializer[Array[Byte]] = new BytePrimitiveArraySerializer()
  implicit val charArraySerializer: TypeSerializer[Array[Char]] = new CharPrimitiveArraySerializer()
  implicit val shortArraySerializer: TypeSerializer[Array[Short]] = new ShortPrimitiveArraySerializer()
  implicit val stringArraySerializer: TypeSerializer[Array[String]] = new StringArraySerializer()

  implicit val jIntegerSerializer: TypeSerializer[Integer] =
    new org.apache.flink.api.common.typeutils.base.IntSerializer()
  implicit val jLongSerializer: TypeSerializer[java.lang.Long] =
    new org.apache.flink.api.common.typeutils.base.LongSerializer()
  implicit val jFloatSerializer: TypeSerializer[java.lang.Float] =
    new org.apache.flink.api.common.typeutils.base.FloatSerializer()
  implicit val jDoubleSerializer: TypeSerializer[java.lang.Double] =
    new org.apache.flink.api.common.typeutils.base.DoubleSerializer()
  implicit val jBooleanSerializer: TypeSerializer[java.lang.Boolean] =
    new org.apache.flink.api.common.typeutils.base.BooleanSerializer()
  implicit val jByteSerializer: TypeSerializer[java.lang.Byte] =
    new org.apache.flink.api.common.typeutils.base.ByteSerializer()
  implicit val jCharSerializer: TypeSerializer[java.lang.Character] =
    new org.apache.flink.api.common.typeutils.base.CharSerializer()
  implicit val jShortSerializer: TypeSerializer[java.lang.Short] =
    new org.apache.flink.api.common.typeutils.base.ShortSerializer()

  // type infos
  implicit val stringInfo: TypeInformation[String] = BasicTypeInfo.STRING_TYPE_INFO
  implicit val intInfo: TypeInformation[Int] = IntTypeInformation()
  implicit val boolInfo: TypeInformation[Boolean] = BooleanTypeInformation()
  implicit val byteInfo: TypeInformation[Byte] = ByteTypeInformation()
  implicit val charInfo: TypeInformation[Char] = CharTypeInformation()
  implicit val doubleInfo: TypeInformation[Double] = DoubleTypeInformation()
  implicit val floatInfo: TypeInformation[Float] = FloatTypeInformation()
  implicit val longInfo: TypeInformation[Long] = LongTypeInformation()
  implicit val shortInfo: TypeInformation[Short] = ShortTypeInformation()

  // java
  implicit val jIntegerInfo: TypeInformation[java.lang.Integer] = BasicTypeInfo.INT_TYPE_INFO
  implicit val jLongInfo: TypeInformation[java.lang.Long] = BasicTypeInfo.LONG_TYPE_INFO
  implicit val jFloatInfo: TypeInformation[java.lang.Float] = BasicTypeInfo.FLOAT_TYPE_INFO
  implicit val jDoubleInfo: TypeInformation[java.lang.Double] = BasicTypeInfo.DOUBLE_TYPE_INFO
  implicit val jBooleanInfo: TypeInformation[java.lang.Boolean] = BasicTypeInfo.BOOLEAN_TYPE_INFO
  implicit val jByteInfo: TypeInformation[java.lang.Byte] = BasicTypeInfo.BYTE_TYPE_INFO
  implicit val jCharInfo: TypeInformation[java.lang.Character] = BasicTypeInfo.CHAR_TYPE_INFO
  implicit val jShortInfo: TypeInformation[java.lang.Short] = BasicTypeInfo.SHORT_TYPE_INFO

  implicit def listInfo[T: ClassTag: TypeInformation](implicit ts: TypeSerializer[T],
                                                      ls: TypeSerializer[List[T]]): TypeInformation[List[T]] =
    new ListTypeInformation[T]()

  implicit def seqInfo[T: ClassTag: TypeInformation](implicit ts: TypeSerializer[T],
                                                     ls: TypeSerializer[Seq[T]]): TypeInformation[Seq[T]] =
    new SeqTypeInformation[T]()

  implicit def vectorInfo[T: ClassTag: TypeInformation](implicit ts: TypeSerializer[T],
                                                        ls: TypeSerializer[Vector[T]]): TypeInformation[Vector[T]] =
    new VectorTypeInformation[T]()

  implicit def setInfo[T: ClassTag: TypeInformation](implicit ts: TypeSerializer[T],
                                                     ls: TypeSerializer[Set[T]]): TypeInformation[Set[T]] =
    new SetTypeInformation[T]()

  implicit def nelInfo[T: ClassTag: TypeInformation](
      implicit ts: TypeSerializer[T],
      ls: TypeSerializer[NonEmptyList[T]]
  ): TypeInformation[NonEmptyList[T]] =
    new NonEmptyListTypeInformation[T]()

  implicit def arrayInfo[T: ClassTag: TypeInformation](implicit ts: TypeSerializer[T],
                                                       ls: TypeSerializer[Array[T]]): TypeInformation[Array[T]] =
    new ArrayTypeInformation[T]()

  implicit def mapInfo[K: ClassTag: TypeInformation, V: ClassTag: TypeInformation](
      implicit ks: TypeSerializer[K],
      vs: TypeSerializer[V],
      ms: TypeSerializer[Map[K, V]]
  ): TypeInformation[Map[K, V]] =
    new MapTypeInformation[K, V]()
}
