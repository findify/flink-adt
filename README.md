# Scala ADT support for Apache Flink

[![CI Status](https://github.com/findify/flink-adt/workflows/CI/badge.svg)](https://github.com/findify/flink-adt/actions)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.findify/flink-adt_2.12/badge.svg?style=plastic)](https://maven-badges.herokuapp.com/maven-central/io.github.metarank/cfor_2.13)
[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](https://opensource.org/licenses/MIT)

This is a prototype of Magnolia-based serializer framework for Apache Flink, with
more Scala-specific TypeSerializer & TypeInformation derivation support.

* can support ADTs (Algebraic data types, sealed trait hierarchies)
* correctly handles `case object` 
* can be extended with custom serializers even for deeply-nested types, as it uses implicitly available serializers
  in the current scope
* has no silent fallback to Kryo: it will just fail the compilation in a case when serializer cannot be made
* reuses all the low-level serialization code from Flink for basic Java and Scala types
* supports Scala 2.12, 2.13 & 3.
* built for scala-free Flink 1.15, which supports arbitrary Scala versions.

Issues:
* as this project relies on macro to derive TypeSerializer instances, if you're using IntelliJ 2020.*, it may
highlight your code with red, hinting that it cannot find corresponding implicits. And this is fine, the code
compiles OK. 2022.1 is fine with serializers derived with this library.
  
## Usage

`flink-adt` is released to Maven-central. For SBT, add this snippet to `build.sbt`:
```scala
libraryDependencies += "io.findify" %% "flink-adt" % "0.6.1"
```

To use this library, swap `import org.apache.flink.api.scala._` with `import io.findify.flinkadt.api._` and enjoy.

So to derive a TypeInformation for a sealed trait, you can do:
```scala
import io.findify.flinkadt.api._
import org.apache.flink.api.common.typeinfo.TypeInformation

sealed trait Event extends Product with Serializable

object Event {
  final case class Click(id: String) extends Event
  final case class Purchase(price: Double) extends Event

  implicit val eventTypeInfo: TypeInformation[Event] = deriveTypeInformation
}
```

Be careful with a wildcard import of `import org.apache.flink.api.scala._`: it has a `createTypeInformation` implicit
function, which may happily generate you a kryo-based serializer in a place you never expected. So in a case if you want
to do this type of wildcard import, make sure that you explicitly called `deriveTypeInformation`
for all the sealed traits in the current scope.

## Java types

flink-adt is a scala-specific library and won't derive TypeInformation for java classes (as they don't extend the `scala.Product` type).
But you can always fall back to flink's own POJO serializer in this way, so just make it implicit so flink-adt can pick it up:

```scala
import java.date.LocalDate
implicit val localDateTypeInfo: TypeInformation[LocalDate] = TypeInformation.of(classOf[LocalDate])
```

## Type mapping

Sometimes flink-adt may spot a type (usually a java one), which cannot be directly serialized as a case class, like this 
example:
```scala
  class WrappedString {
    private var internal: String = ""

    override def equals(obj: Any): Boolean = obj match {
      case s: WrappedString => s.get == internal
      case _                => false
    }
    def get: String = internal
    def put(value: String) = {
      internal = value
    }
  }
```

You can write a pair of explicit `TypeInformation[WrappedString]` and `Serializer[WrappedString]`, but it's extremely verbose,
and the class itself can be 1-to-1 mapped to a regular `String`. Flink-adt has a mechanism of type mappers to delegate serialization
of non-serializable types to existing serializers. For example:
```scala
  class WrappedMapper extends TypeMapper[WrappedString, String] {
    override def map(a: WrappedString): String = a.get

    override def contramap(b: String): WrappedString = {
      val str = new WrappedString
      str.put(b)
      str
    }
  }
  implicit val mapper: TypeMapper[WrappedString, String] = new WrappedMapper()
  // will treat WrappedString with String typeinfo:
  implicit val ti: TypeInformation[WrappedString] = implicitly[TypeInformation[WrappedString]] 
```

When there is a `TypeMapper[A,B]` in the scope to convert `A` to `B` and back, and type `B` has `TypeInformation[B]` available 
in the scope also, then flink-adt will use a delegated existing typeinfo for `B` when it will spot type `A`.

Warning: on Scala 3, the TypeMapper should not be made anonymous. This example won't work, as anonymous implicit classes in 
scala 3 are private, and Flink cannot instantiate it on restore without jvm17 incompatible reflection hacks:
```scala
  // anonymous class, will fail on runtime on scala 3
  implicit val mapper: TypeMapper[WrappedString, String] = new TypeMapper[WrappedString, String] {
    override def map(a: WrappedString): String = a.get

    override def contramap(b: String): WrappedString = {
      val str = new WrappedString
      str.put(b)
      str
    }
  }
```

## Schema evolution

For the child case classes being part of ADT, `flink-adt` uses a Flink's `ScalaCaseClassSerializer`, so all the compatibility rules
are the same as for normal case classes.

For the sealed trait membership itself, `flink-adt` used an own serialization format with the following rules:
* you cannot reorder trait members, as wire format depends on the compile-time index of each member
* you can add new members at the end of the list
* you cannot remove ADT members
* you cannot replace ADT members

## Compatibility

This project uses a separate set of serializers for collections, instead of Flink's own TraversableSerializer. So probably you
may have issues while migrating state snapshots from TraversableSerializer to FlinkADT ones.

Starting from version 0.5.0+, this project strictly depends on Flink 1.15+, as starting from this version it can be cross-built 
for scala 2.13 and scala 3.

## Licence

The MIT License (MIT)

Copyright (c) 2022 Findify AB

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
