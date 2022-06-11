package io.findify.flinkadt

import io.circe.{parser, Json}
import io.findify.flinkadt.api._
import io.findify.flinkadt.api.serializer.MappedSerializer.TypeMapper
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CirceJsonTest extends AnyFlatSpec with Matchers {
  import CirceJsonTest._

  it should "derive type information & create serializer for Circe Json" in {
    typeInformation[Json].createSerializer(null) shouldNot be(null)
  }
}

object CirceJsonTest {
  // TODO provide via separate module?
  implicit val circeJsonMapper: TypeMapper[Json, String] = new TypeMapper[Json, String] {
    override def map(a: Json): String = a.noSpaces

    override def contramap(b: String): Json = parser
      .parse(b)
      .getOrElse(Json.Null)
  }
}
