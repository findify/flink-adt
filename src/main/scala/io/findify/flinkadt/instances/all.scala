package io.findify.flinkadt.instances

import cats.data.NonEmptyList
import org.apache.flink.api.common.typeutils.TypeSerializer
import org.apache.flink.api.common.typeutils.base.StringSerializer
import org.apache.flink.api.scala.typeutils.{EitherSerializer, OptionSerializer}

object all {
  implicit val stringSerializer: TypeSerializer[String] = new StringSerializer()
  implicit val intSerializer: TypeSerializer[Int] = new IntSerializer()
  implicit val longSerializer: TypeSerializer[Long] = new LongSerializer()
  implicit val floatSerializer: TypeSerializer[Float] = new FloatSerializer()
  implicit val doubleSerializer: TypeSerializer[Double] = new DoubleSerializer()
  implicit val booleanSerializer: TypeSerializer[Boolean] = new BooleanSerializer()

  implicit def optionSerializer[T](implicit vs: TypeSerializer[T]): TypeSerializer[Option[T]] = new OptionSerializer[T](vs)
  implicit def listSerializer[T](implicit vs: TypeSerializer[T]): TypeSerializer[List[T]] = new ListSerializer[T](vs)
  implicit def seqSerializer[T](implicit vs: TypeSerializer[T]): TypeSerializer[Seq[T]] = new SeqSerializer[T](vs)
  implicit def nelSerializer[T](implicit vs: TypeSerializer[T]): TypeSerializer[NonEmptyList[T]] = new NonEmptyListSerializer[T](vs)
  implicit def eitherSerializer[L, R](implicit ls: TypeSerializer[L], rs: TypeSerializer[R]) = new EitherSerializer[L, R](ls, rs)

  implicit val jIntegerSerializer: TypeSerializer[Integer] = new org.apache.flink.api.common.typeutils.base.IntSerializer()
  implicit val jLongSerializer: TypeSerializer[java.lang.Long] = new org.apache.flink.api.common.typeutils.base.LongSerializer()
  implicit val jFloatSerializer: TypeSerializer[java.lang.Float] = new org.apache.flink.api.common.typeutils.base.FloatSerializer()
  implicit val jDoubleSerializer: TypeSerializer[java.lang.Double] = new org.apache.flink.api.common.typeutils.base.DoubleSerializer()
  implicit val jBooleanSerializer: TypeSerializer[java.lang.Boolean] = new org.apache.flink.api.common.typeutils.base.BooleanSerializer()

}
