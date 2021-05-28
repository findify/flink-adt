package io.findify.flinkadt

import com.google.common.io.Files
import io.findify.flinkadt.SchemaEvolutionTest.{Click, Event}
import org.apache.flink.core.memory.{DataInputViewStreamWrapper, DataOutputViewStreamWrapper}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.io.{ByteArrayOutputStream, File}

class SchemaEvolutionTest extends AnyFlatSpec with Matchers {
  import io.findify.flinkadt.api.typeinfo._
  import io.findify.flinkadt.api.serializer._
  import io.findify.flinkadt.instances.all._

  val eventSerializer = deriveADTSerializer[Event]

//  it should "generate blob for event=click+purchase" in {
//    val buffer = new ByteArrayOutputStream()
//    eventSerializer.serialize(Click("p1"), new DataOutputViewStreamWrapper(buffer))
//    Files.write(buffer.toByteArray, new File("/tmp/out.dat"))
//  }

  it should "decode click when we added view" in {
    val buffer = this.getClass.getResourceAsStream("/click.dat")
    val click = eventSerializer.deserialize(new DataInputViewStreamWrapper(buffer))
    click shouldBe Click("p1")
  }
}


object SchemaEvolutionTest {
  sealed trait Event
  case class Click(id: String) extends Event
  case class Purchase(price: Double) extends Event
  case class View(ts: Long) extends Event
}