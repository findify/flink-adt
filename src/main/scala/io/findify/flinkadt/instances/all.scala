package io.findify.flinkadt.instances

import cats.data.NonEmptyList
import io.findify.flinkadt.instances.serializer.collection._
import io.findify.flinkadt.instances.typeinfo.collection.{
  ArrayTypeInformation,
  ListTypeInformation,
  MapTypeInformation,
  NonEmptyListTypeInformation,
  SeqTypeInformation,
  SetTypeInformation,
  VectorTypeInformation
}
import org.apache.flink.api.common.ExecutionConfig
import org.apache.flink.api.common.typeinfo.{BasicTypeInfo, TypeInformation}
import org.apache.flink.api.common.typeutils.{TypeComparator, TypeSerializer}
import org.apache.flink.api.common.typeutils.base.{StringComparator, StringSerializer}
import org.apache.flink.api.common.typeutils.base.array._
import org.apache.flink.api.scala.typeutils.{EitherSerializer, OptionSerializer, TraversableSerializer}
import org.apache.flink.api.scala.createTypeInformation

import scala.reflect.{classTag, ClassTag}

object all {

  private val config = new ExecutionConfig()

  implicit def optionSerializer[T](implicit vs: TypeSerializer[T]): TypeSerializer[Option[T]] =
    new OptionSerializer[T](vs)
  implicit def listSerializer[T: ClassTag](implicit vs: TypeSerializer[T]): TypeSerializer[List[T]] =
    //new TraversableSerializer[List[T], T](vs, ???)
    new ListSerializer[T](vs, classTag[T].runtimeClass.asInstanceOf[Class[T]])
  implicit def vectorSerializer[T: ClassTag](implicit vs: TypeSerializer[T]): TypeSerializer[Vector[T]] =
    new VectorSerializer[T](vs, classTag[T].runtimeClass.asInstanceOf[Class[T]])
  implicit def arraySerializer[T: ClassTag](implicit vs: TypeSerializer[T]): TypeSerializer[Array[T]] =
    new ArraySerializer[T](vs, classTag[T].runtimeClass.asInstanceOf[Class[T]])
  implicit def setSerializer[T: ClassTag](implicit vs: TypeSerializer[T]): TypeSerializer[Set[T]] =
    new SetSerializer[T](vs, classTag[T].runtimeClass.asInstanceOf[Class[T]])
  implicit def mapSerializer[K: ClassTag, V: ClassTag](implicit
      ks: TypeSerializer[K],
      vs: TypeSerializer[V]
  ): TypeSerializer[Map[K, V]] =
    new MapSerializer[K, V](ks, vs)
  implicit def seqSerializer[T: ClassTag](implicit vs: TypeSerializer[T]): TypeSerializer[Seq[T]] =
    new SeqSerializer[T](vs, classTag[T].runtimeClass.asInstanceOf[Class[T]])
  implicit def nelSerializer[T: ClassTag](implicit vs: TypeSerializer[T]): TypeSerializer[NonEmptyList[T]] =
    new NonEmptyListSerializer[T](vs, classTag[T].runtimeClass.asInstanceOf[Class[T]])
  implicit def eitherSerializer[L: ClassTag, R: ClassTag](implicit ls: TypeSerializer[L], rs: TypeSerializer[R]) =
    new EitherSerializer[L, R](ls, rs)

  implicit val intArraySerializer: TypeSerializer[Array[Int]]         = new IntPrimitiveArraySerializer()
  implicit val longArraySerializer: TypeSerializer[Array[Long]]       = new LongPrimitiveArraySerializer()
  implicit val floatArraySerializer: TypeSerializer[Array[Float]]     = new FloatPrimitiveArraySerializer()
  implicit val doubleArraySerializer: TypeSerializer[Array[Double]]   = new DoublePrimitiveArraySerializer()
  implicit val booleanArraySerializer: TypeSerializer[Array[Boolean]] = new BooleanPrimitiveArraySerializer()
  implicit val byteArraySerializer: TypeSerializer[Array[Byte]]       = new BytePrimitiveArraySerializer()
  implicit val charArraySerializer: TypeSerializer[Array[Char]]       = new CharPrimitiveArraySerializer()
  implicit val shortArraySerializer: TypeSerializer[Array[Short]]     = new ShortPrimitiveArraySerializer()
  implicit val stringArraySerializer: TypeSerializer[Array[String]]   = new StringArraySerializer()

  implicit lazy val jIntegerSerializer: TypeSerializer[Integer] =
    new org.apache.flink.api.common.typeutils.base.IntSerializer()
  implicit lazy val jLongSerializer: TypeSerializer[java.lang.Long] =
    new org.apache.flink.api.common.typeutils.base.LongSerializer()
  implicit lazy val jFloatSerializer: TypeSerializer[java.lang.Float] =
    new org.apache.flink.api.common.typeutils.base.FloatSerializer()
  implicit lazy val jDoubleSerializer: TypeSerializer[java.lang.Double] =
    new org.apache.flink.api.common.typeutils.base.DoubleSerializer()
  implicit lazy val jBooleanSerializer: TypeSerializer[java.lang.Boolean] =
    new org.apache.flink.api.common.typeutils.base.BooleanSerializer()
  implicit lazy val jByteSerializer: TypeSerializer[java.lang.Byte] =
    new org.apache.flink.api.common.typeutils.base.ByteSerializer()
  implicit lazy val jCharSerializer: TypeSerializer[java.lang.Character] =
    new org.apache.flink.api.common.typeutils.base.CharSerializer()
  implicit lazy val jShortSerializer: TypeSerializer[java.lang.Short] =
    new org.apache.flink.api.common.typeutils.base.ShortSerializer()

  // type infos
  implicit lazy val stringInfo: TypeInformation[String] = BasicTypeInfo.STRING_TYPE_INFO
  implicit lazy val intInfo: TypeInformation[Int]       = createTypeInformation[Int]
  implicit lazy val boolInfo: TypeInformation[Boolean]  = createTypeInformation[Boolean]
  implicit lazy val byteInfo: TypeInformation[Byte]     = createTypeInformation[Byte]
  implicit lazy val charInfo: TypeInformation[Char]     = createTypeInformation[Char]
  implicit lazy val doubleInfo: TypeInformation[Double] = createTypeInformation[Double]
  implicit lazy val floatInfo: TypeInformation[Float]   = createTypeInformation[Float]
  implicit lazy val longInfo: TypeInformation[Long]     = createTypeInformation[Long]
  implicit lazy val shortInfo: TypeInformation[Short]   = createTypeInformation[Short]
  // serializers
  implicit lazy val stringSerializer: TypeSerializer[String]   = stringInfo.createSerializer(config)
  implicit lazy val intSerializer: TypeSerializer[Int]         = intInfo.createSerializer(config)
  implicit lazy val longSerializer: TypeSerializer[Long]       = longInfo.createSerializer(config)
  implicit lazy val floatSerializer: TypeSerializer[Float]     = floatInfo.createSerializer(config)
  implicit lazy val doubleSerializer: TypeSerializer[Double]   = doubleInfo.createSerializer(config)
  implicit lazy val booleanSerializer: TypeSerializer[Boolean] = boolInfo.createSerializer(config)
  implicit lazy val byteSerializer: TypeSerializer[Byte]       = byteInfo.createSerializer(config)
  implicit lazy val charSerializer: TypeSerializer[Char]       = charInfo.createSerializer(config)
  implicit lazy val shortSerializer: TypeSerializer[Short]     = shortInfo.createSerializer(config)

  // java
  implicit lazy val jIntegerInfo: TypeInformation[java.lang.Integer] = BasicTypeInfo.INT_TYPE_INFO
  implicit lazy val jLongInfo: TypeInformation[java.lang.Long]       = BasicTypeInfo.LONG_TYPE_INFO
  implicit lazy val jFloatInfo: TypeInformation[java.lang.Float]     = BasicTypeInfo.FLOAT_TYPE_INFO
  implicit lazy val jDoubleInfo: TypeInformation[java.lang.Double]   = BasicTypeInfo.DOUBLE_TYPE_INFO
  implicit lazy val jBooleanInfo: TypeInformation[java.lang.Boolean] = BasicTypeInfo.BOOLEAN_TYPE_INFO
  implicit lazy val jByteInfo: TypeInformation[java.lang.Byte]       = BasicTypeInfo.BYTE_TYPE_INFO
  implicit lazy val jCharInfo: TypeInformation[java.lang.Character]  = BasicTypeInfo.CHAR_TYPE_INFO
  implicit lazy val jShortInfo: TypeInformation[java.lang.Short]     = BasicTypeInfo.SHORT_TYPE_INFO

  implicit def listInfo[T: ClassTag: TypeInformation](implicit
      ts: TypeSerializer[T],
      ls: TypeSerializer[List[T]]
  ): TypeInformation[List[T]] =
    new ListTypeInformation[T]()

  implicit def seqInfo[T: ClassTag: TypeInformation](implicit
      ts: TypeSerializer[T],
      ls: TypeSerializer[Seq[T]]
  ): TypeInformation[Seq[T]] =
    new SeqTypeInformation[T]()

  implicit def vectorInfo[T: ClassTag: TypeInformation](implicit
      ts: TypeSerializer[T],
      ls: TypeSerializer[Vector[T]]
  ): TypeInformation[Vector[T]] =
    new VectorTypeInformation[T]()

  implicit def setInfo[T: ClassTag: TypeInformation](implicit
      ts: TypeSerializer[T],
      ls: TypeSerializer[Set[T]]
  ): TypeInformation[Set[T]] =
    new SetTypeInformation[T]()

  implicit def nelInfo[T: ClassTag: TypeInformation](implicit
      ts: TypeSerializer[T],
      ls: TypeSerializer[NonEmptyList[T]]
  ): TypeInformation[NonEmptyList[T]] =
    new NonEmptyListTypeInformation[T]()

  implicit def arrayInfo[T: ClassTag: TypeInformation](implicit
      ts: TypeSerializer[T],
      ls: TypeSerializer[Array[T]]
  ): TypeInformation[Array[T]] =
    new ArrayTypeInformation[T]()

  implicit def mapInfo[K: ClassTag: TypeInformation, V: ClassTag: TypeInformation](implicit
      ks: TypeSerializer[K],
      vs: TypeSerializer[V],
      ms: TypeSerializer[Map[K, V]]
  ): TypeInformation[Map[K, V]] =
    new MapTypeInformation[K, V]()
}
