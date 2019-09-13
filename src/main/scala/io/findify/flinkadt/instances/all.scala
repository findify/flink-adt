package io.findify.flinkadt.instances

import cats.data.NonEmptyList
import io.findify.flinkadt.instances.collection.{
  ArraySerializer,
  ListSerializer,
  MapSerializer,
  NonEmptyListSerializer,
  SeqSerializer,
  SetSerializer
}
import io.findify.flinkadt.instances.primitive.{
  BooleanSerializer,
  ByteSerializer,
  CharSerializer,
  DoubleSerializer,
  FloatSerializer,
  IntSerializer,
  LongSerializer,
  ShortSerializer
}
import org.apache.flink.api.common.typeutils.TypeSerializer
import org.apache.flink.api.common.typeutils.base.StringSerializer
import org.apache.flink.api.common.typeutils.base.array.{
  BooleanPrimitiveArraySerializer,
  BytePrimitiveArraySerializer,
  CharPrimitiveArraySerializer,
  DoublePrimitiveArraySerializer,
  FloatPrimitiveArraySerializer,
  IntPrimitiveArraySerializer,
  LongPrimitiveArraySerializer,
  ShortPrimitiveArraySerializer,
  StringArraySerializer
}
import org.apache.flink.api.scala.typeutils.{ EitherSerializer, OptionSerializer }

import scala.reflect.ClassTag

object all {
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
  implicit def listSerializer[T](implicit vs: TypeSerializer[T]): TypeSerializer[List[T]] = new ListSerializer[T](vs)
  implicit def arraySerializer[T: ClassTag](implicit vs: TypeSerializer[T]): TypeSerializer[Array[T]] =
    new ArraySerializer[T](vs)
  implicit def setSerializer[T](implicit vs: TypeSerializer[T]): TypeSerializer[Set[T]] = new SetSerializer[T](vs)
  implicit def mapSerializer[K, V](implicit ks: TypeSerializer[K], vs: TypeSerializer[V]): TypeSerializer[Map[K, V]] =
    new MapSerializer[K, V](ks, vs)
  implicit def seqSerializer[T](implicit vs: TypeSerializer[T]): TypeSerializer[Seq[T]] = new SeqSerializer[T](vs)
  implicit def nelSerializer[T](implicit vs: TypeSerializer[T]): TypeSerializer[NonEmptyList[T]] =
    new NonEmptyListSerializer[T](vs)
  implicit def eitherSerializer[L, R](implicit ls: TypeSerializer[L], rs: TypeSerializer[R]) =
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

}
