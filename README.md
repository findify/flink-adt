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

Issues:
* as this project relies on macro to derive TypeSerializer instances, if you're using IntelliJ 2020.*, it may
highlight your code with red, hinting that it cannot find corresponding implicits. And this is fine, the code
compiles OK. 2021 is fine with serializers derived with this library.
* this library is built for Flink 1.15, which supports arbitrary Scala versions.
* Supports only Scala 2.12: underlying Magnolia library has no support for 2.11
  
## Usage

`flink-adt` is released to Maven-central. For SBT, add this snippet to `build.sbt`:
```scala
libraryDependencies += "io.findify" %% "flink-adt" % "0.4.5"
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

## Licence

The MIT License (MIT)

Copyright (c) 2021 Findify AB

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
