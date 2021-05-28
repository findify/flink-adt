package io.findify.flinkadt.api

import magnolia.{CaseClass, Magnolia, SealedTrait}
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.common.typeutils.TypeSerializer
import scala.language.experimental.macros
import scala.reflect.ClassTag

package object typeinfo {
  type Typeclass[T] = TypeInformation[T]

  def combine[T <: Product: ClassTag: TypeSerializer](
      ctx: CaseClass[TypeInformation, T]
  ): TypeInformation[T] =
    new ProductTypeInformation[T](ctx)

  def dispatch[T: ClassTag: TypeSerializer](
      ctx: SealedTrait[TypeInformation, T]
  ): TypeInformation[T] =
    new CoproductTypeInformation[T]()

  def deriveTypeInformation[T]: TypeInformation[T] = macro Magnolia.gen[T]

}
