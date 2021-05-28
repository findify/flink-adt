package io.findify.flinkadt

import io.findify.flinkadt.ExampleTest.{Click, Event, Purchase}
import org.apache.flink.api.common.RuntimeExecutionMode
import org.apache.flink.api.common.restartstrategy.RestartStrategies
import org.apache.flink.api.scala.ExecutionEnvironment
import org.apache.flink.runtime.testutils.MiniClusterResourceConfiguration
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.test.util.MiniClusterWithClientResource
import org.scalatest.BeforeAndAfterAll
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ExampleTest extends AnyFlatSpec with Matchers with BeforeAndAfterAll {
  lazy val cluster = new MiniClusterWithClientResource(
    new MiniClusterResourceConfiguration.Builder().setNumberSlotsPerTaskManager(1).setNumberTaskManagers(1).build()
  )

  lazy val env = {
    cluster.getTestEnvironment.setAsContext()
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)
    env.setRuntimeMode(RuntimeExecutionMode.BATCH)
    env.enableCheckpointing(1000)
    env.setRestartStrategy(RestartStrategies.noRestart())
    env.getConfig.disableGenericTypes()
    env
  }

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    cluster.before()
  }

  override def afterAll(): Unit = {
    cluster.after()
    super.afterAll()
  }

  it should "run example code" in {
    import io.findify.flinkadt.api._

    implicit val eventTypeInfo = deriveTypeInformation[Event]

    val result = env.fromCollection(List[Event](Click("1"), Purchase(1.0))).executeAndCollect(10)
    result.size shouldBe 2
  }

  it should "not clash with scala.api._" in {
    import io.findify.flinkadt.api._
    import org.apache.flink.api.scala._

    implicit val eventTypeInfo = deriveTypeInformation[Event]

    val result = env.fromCollection(List[Event](Click("1"), Purchase(1.0))).executeAndCollect(10)
    result.size shouldBe 2
  }
}

object ExampleTest {
  sealed trait Event
  case class Click(id: String)       extends Event
  case class Purchase(price: Double) extends Event

}
