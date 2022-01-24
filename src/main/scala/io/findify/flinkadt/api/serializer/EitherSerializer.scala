package io.findify.flinkadt.api.serializer

import org.apache.flink.annotation.Internal
import org.apache.flink.api.common.typeutils._
import org.apache.flink.core.memory.{DataInputView, DataOutputView}

/**
 * Serializer for [[Either]].
 * Copied from Flink 1.14.
 */
@Internal
@SerialVersionUID(9219995873023657525L)
class EitherSerializer[A, B](
  val leftSerializer: TypeSerializer[A],
  val rightSerializer: TypeSerializer[B]
) extends TypeSerializer[Either[A, B]] {

  override def duplicate: EitherSerializer[A,B] = {
    val leftDup = leftSerializer.duplicate()
    val rightDup = rightSerializer.duplicate()

    if (leftDup.eq(leftSerializer) && rightDup.eq(rightSerializer)) {
      this
    } else {
      new EitherSerializer[A, B](leftDup, rightDup)
    }
  }

  override def createInstance: Either[A, B] = {
    Left(null).asInstanceOf[Left[A, B]]
  }

  override def isImmutableType: Boolean = {
    (leftSerializer == null || leftSerializer.isImmutableType) &&
      (rightSerializer == null || rightSerializer.isImmutableType)
  }

  override def getLength: Int = -1

  override def copy(from: Either[A, B]): Either[A, B] = from match {
    case Left(a) => Left(leftSerializer.copy(a))
    case Right(b) => Right(rightSerializer.copy(b))
  }

  override def copy(from: Either[A, B], reuse: Either[A, B]): Either[A, B] = copy(from)

  override def copy(source: DataInputView, target: DataOutputView): Unit = {
    val isLeft = source.readBoolean()
    target.writeBoolean(isLeft)
    if (isLeft) {
      leftSerializer.copy(source, target)
    } else {
      rightSerializer.copy(source, target)
    }
  }

  override def serialize(either: Either[A, B], target: DataOutputView): Unit = either match {
    case Left(a) =>
      target.writeBoolean(true)
      leftSerializer.serialize(a, target)
    case Right(b) =>
      target.writeBoolean(false)
      rightSerializer.serialize(b, target)
  }

  override def deserialize(source: DataInputView): Either[A, B] = {
    val isLeft = source.readBoolean()
    if (isLeft) {
      Left(leftSerializer.deserialize(source))
    } else {
      Right(rightSerializer.deserialize(source))
    }
  }

  override def deserialize(reuse: Either[A, B], source: DataInputView): Either[A, B] = {
    val isLeft = source.readBoolean()
    if (isLeft) {
      Left(leftSerializer.deserialize(source))
    } else {
      Right(rightSerializer.deserialize(source))
    }
  }

  override def equals(obj: Any): Boolean = {
    obj match {
      case eitherSerializer: EitherSerializer[_, _] =>
        leftSerializer.equals(eitherSerializer.leftSerializer) &&
          rightSerializer.equals(eitherSerializer.rightSerializer)
      case _ => false
    }
  }

  override def hashCode(): Int = {
    31 * leftSerializer.hashCode() + rightSerializer.hashCode()
  }

  def getLeftSerializer: TypeSerializer[A] = leftSerializer

  def getRightSerializer: TypeSerializer[B] = rightSerializer

  // --------------------------------------------------------------------------------------------
  // Serializer configuration snapshotting & compatibility
  // --------------------------------------------------------------------------------------------

  override def snapshotConfiguration(): ScalaEitherSerializerSnapshot[A, B] = {
    new ScalaEitherSerializerSnapshot[A, B](this)
  }
}
