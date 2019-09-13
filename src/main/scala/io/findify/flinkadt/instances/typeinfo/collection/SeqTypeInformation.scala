package io.findify.flinkadt.instances.typeinfo.collection

import io.findify.flinkadt.api.typeinfo.SimpleTypeInformation
import org.apache.flink.api.common.typeutils.TypeSerializer

import scala.reflect.ClassTag

case class SeqTypeInformation[T: ClassTag: TypeSerializer](implicit s: TypeSerializer[Seq[T]])
    extends SimpleTypeInformation[Seq[T]] {}
