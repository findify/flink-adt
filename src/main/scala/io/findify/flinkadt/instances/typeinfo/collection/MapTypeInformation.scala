package io.findify.flinkadt.instances.typeinfo.collection

import io.findify.flinkadt.api.typeinfo.SimpleTypeInformation
import org.apache.flink.api.common.typeutils.TypeSerializer

import scala.reflect.ClassTag

case class MapTypeInformation[K: ClassTag: TypeSerializer, V: ClassTag: TypeSerializer](
    implicit s: TypeSerializer[Map[K, V]]
) extends SimpleTypeInformation[Map[K, V]] {}
