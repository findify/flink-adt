package io.findify.flinkadt.api

import magnolia.Magnolia
import org.apache.flink.api.common.typeinfo.TypeInformation
import scala.language.experimental.macros

trait LowPrioImplicits {
  implicit def deriveTypeInformation[T]: TypeInformation[T] = macro Magnolia.gen[T]
}
