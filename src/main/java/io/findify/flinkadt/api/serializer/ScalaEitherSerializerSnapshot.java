package io.findify.flinkadt.api.serializer;

import org.apache.flink.api.common.typeutils.CompositeTypeSerializerSnapshot;
import org.apache.flink.api.common.typeutils.TypeSerializer;

import scala.util.Either;

/**
 * Configuration snapshot for serializers of Scala's {@link Either} type, containing configuration
 * snapshots of the Left and Right serializers.
 */
public class ScalaEitherSerializerSnapshot<L, R>
        extends CompositeTypeSerializerSnapshot<Either<L, R>, EitherSerializer<L, R>> {

    private static final int CURRENT_VERSION = 1;

    /** Constructor for read instantiation. */
    public ScalaEitherSerializerSnapshot() {
        super(EitherSerializer.class);
    }

    /** Constructor to create the snapshot for writing. */
    public ScalaEitherSerializerSnapshot(EitherSerializer<L, R> eitherSerializer) {
        super(eitherSerializer);
    }

    @Override
    public int getCurrentOuterSnapshotVersion() {
        return CURRENT_VERSION;
    }

    @Override
    protected EitherSerializer<L, R> createOuterSerializerWithNestedSerializers(
            TypeSerializer<?>[] nestedSerializers) {
        @SuppressWarnings("unchecked")
        TypeSerializer<L> leftSerializer = (TypeSerializer<L>) nestedSerializers[0];

        @SuppressWarnings("unchecked")
        TypeSerializer<R> rightSerializer = (TypeSerializer<R>) nestedSerializers[1];

        return new EitherSerializer<>(leftSerializer, rightSerializer);
    }

    @Override
    protected TypeSerializer<?>[] getNestedSerializers(EitherSerializer<L, R> outerSerializer) {
        return new TypeSerializer<?>[] {
                outerSerializer.getLeftSerializer(), outerSerializer.getRightSerializer()
        };
    }
}

