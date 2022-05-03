/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.findify.flinkadt.api.serializer

import io.findify.flinkadt.api.serializer.ScalaCaseClassSerializer.lookupConstructor
import org.apache.flink.api.common.typeutils.{TypeSerializer, TypeSerializerSnapshot}

import java.io.ObjectInputStream

/** This is a non macro-generated, concrete Scala case class serializer.
  * Copied from Flink 1.14 without `SelfResolvingTypeSerializer`.
  */
@SerialVersionUID(1L)
class ScalaCaseClassSerializer[T <: Product](
    clazz: Class[T],
    scalaFieldSerializers: Array[TypeSerializer[_]]
) extends CaseClassSerializer[T](clazz, scalaFieldSerializers) {

  @transient
  private var constructor = lookupConstructor(clazz)

  override def createInstance(fields: Array[AnyRef]): T = {
    constructor(fields)
  }

  override def snapshotConfiguration(): TypeSerializerSnapshot[T] =
    new ScalaCaseClassSerializerSnapshot[T](this)

  // Do NOT delete this method, it is used by ser/de even though it is private.
  // This should be removed once we make sure that serializer are no long java serialized.
  private def readObject(in: ObjectInputStream): Unit = {
    in.defaultReadObject()
    constructor = lookupConstructor(clazz)
  }
}

// Indirectly enable constructor look-up.
// Underlying implementation is major version-specific (Scala 2 vs. Scala 3).
object ScalaCaseClassSerializer extends ConstructorCompat
