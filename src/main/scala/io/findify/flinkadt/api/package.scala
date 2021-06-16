package io.findify.flinkadt

import io.findify.flinkadt.api.serializer._
import io.findify.flinkadt.api.typeinfo.{CollectionTypeInformation, CoproductTypeInformation, ProductTypeInformation}
import magnolia.{CaseClass, Magnolia, SealedTrait}
import org.apache.flink.api.common.ExecutionConfig
import org.apache.flink.api.common.typeinfo.{BasicTypeInfo, TypeInformation}
import org.apache.flink.api.common.typeutils.TypeSerializer
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
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.api.scala.typeutils.{
  EitherSerializer,
  NothingSerializer,
  OptionSerializer,
  OptionTypeInfo,
  ScalaCaseClassSerializer
}

import scala.language.experimental.macros
import scala.reflect.runtime.universe._
import scala.annotation.tailrec
import scala.collection.mutable
import scala.reflect.{ClassTag, classTag}
import scala.util.{Failure, Success, Try}

package object api extends LowPrioImplicits {
  val config = new ExecutionConfig()
  val cache  = mutable.Map[String, TypeInformation[_]]()

  type Typeclass[T] = TypeInformation[T]

  def combine[T <: Product: ClassTag: TypeTag](
      ctx: CaseClass[TypeInformation, T]
  ): TypeInformation[T] = {
    cache.get(ctx.typeName.full) match {
      case Some(cached) => cached.asInstanceOf[TypeInformation[T]]
      case None =>
        val clazz = classTag[T].runtimeClass.asInstanceOf[Class[T]]
        val serializer = typeOf[T].typeSymbol.isModuleClass match {
          case true =>
            new ScalaCaseObjectSerializer[T](clazz)
          case false =>
            new ScalaCaseClassSerializer[T](
              clazz = clazz,
              scalaFieldSerializers = ctx.parameters.map(_.typeclass.createSerializer(config)).toArray
            )
        }
        val ti = new ProductTypeInformation[T](
          c = clazz,
          params = ctx.parameters,
          ser = serializer
        )
        cache.put(ctx.typeName.full, ti)
        ti
    }
  }

  def dispatch[T: ClassTag](
      ctx: SealedTrait[TypeInformation, T]
  ): TypeInformation[T] = {
    cache.get(ctx.typeName.full) match {
      case Some(cached) => cached.asInstanceOf[TypeInformation[T]]
      case None =>
        val serializer = new CoproductSerializer[T](
          subtypeClasses = ctx.subtypes
            .map(_.typeName)
            .map(c => {
              guessClass(c.full).getOrElse(throw new ClassNotFoundException(c.full))
            })
            .toArray,
          subtypeSerializers = ctx.subtypes.map(_.typeclass.createSerializer(config)).toArray
        )
        val clazz = classTag[T].runtimeClass.asInstanceOf[Class[T]]
        val ti    = new CoproductTypeInformation[T](clazz, serializer)
        cache.put(ctx.typeName.full, ti)
        ti
    }
  }

  private def loadClass(name: String): Option[Class[_]] = {
    val sanitized = name.replaceAllLiterally("::", "$colon$colon")
    Try(Class.forName(sanitized)) match {
      case Failure(_) =>
        Try(Class.forName(sanitized + "$")) match {
          case Failure(_)     => None
          case Success(value) => Some(value)
        }
      case Success(value) => Some(value)
    }
  }
  private def replaceLast(str: String, what: Char, dest: Char): Option[String] = {
    str.lastIndexOf(what) match {
      case -1 => None
      case pos =>
        val arr = str.toCharArray
        arr(pos) = dest
        Some(new String(arr))
    }
  }
  @tailrec private def guessClass(name: String): Option[Class[_]] = {
    loadClass(name) match {
      case Some(value) => Some(value)
      case None =>
        replaceLast(name, '.', '$') match {
          case None       => None
          case Some(next) => guessClass(next)
        }
    }
  }

  implicit def into2ser[T](implicit ti: TypeInformation[T]): TypeSerializer[T] = ti.createSerializer(config)

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
  implicit def mapSerializer[K: ClassTag, V: ClassTag](implicit
      ks: TypeSerializer[K],
      vs: TypeSerializer[V]
  ): TypeSerializer[Map[K, V]] =
    new MapSerializer[K, V](ks, vs)
  implicit def seqSerializer[T: ClassTag](implicit vs: TypeSerializer[T]): TypeSerializer[Seq[T]] =
    new SeqSerializer[T](vs, classTag[T].runtimeClass.asInstanceOf[Class[T]])
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

  implicit def listInfo[T: ClassTag](implicit ls: TypeSerializer[List[T]]): TypeInformation[List[T]] =
    new CollectionTypeInformation[List[T]](ls)

  implicit def seqInfo[T: ClassTag](implicit ls: TypeSerializer[Seq[T]]): TypeInformation[Seq[T]] =
    new CollectionTypeInformation[Seq[T]](ls)

  implicit def vectorInfo[T: ClassTag](implicit ls: TypeSerializer[Vector[T]]): TypeInformation[Vector[T]] =
    new CollectionTypeInformation[Vector[T]](ls)

  implicit def setInfo[T: ClassTag](implicit ls: TypeSerializer[Set[T]]): TypeInformation[Set[T]] =
    new CollectionTypeInformation[Set[T]](ls)

  implicit def arrayInfo[T: ClassTag](implicit ls: TypeSerializer[Array[T]]): TypeInformation[Array[T]] =
    new CollectionTypeInformation[Array[T]](ls)

  implicit def mapInfo[K: ClassTag, V: ClassTag](implicit
      ms: TypeSerializer[Map[K, V]]
  ): TypeInformation[Map[K, V]] =
    new CollectionTypeInformation[Map[K, V]](ms)

  implicit def optionInfo[T](implicit ls: TypeInformation[T]): TypeInformation[Option[T]] =
    new OptionTypeInfo[T, Option[T]](ls)
}
