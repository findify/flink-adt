package io.findify.flinkadt.instances.typeinfo.collection

import io.findify.flinkadt.api.typeinfo.SimpleTypeInformation
import org.apache.flink.api.common.typeutils.TypeSerializer

import scala.reflect.ClassTag

case class ListTypeInformation[T: ClassTag: TypeSerializer](implicit s: TypeSerializer[List[T]])
    extends SimpleTypeInformation[List[T]] {}
