package io.findify.flinkadt.instances.typeinfo.collection

import cats.data.NonEmptyList
import io.findify.flinkadt.api.typeinfo.SimpleTypeInformation
import org.apache.flink.api.common.typeutils.TypeSerializer

import scala.reflect.ClassTag

case class NonEmptyListTypeInformation[T: ClassTag: TypeSerializer](implicit s: TypeSerializer[NonEmptyList[T]])
    extends SimpleTypeInformation[NonEmptyList[T]] {}
