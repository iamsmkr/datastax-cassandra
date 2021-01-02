name := "exercises"

version := "0.1"

scalaVersion := "2.13.4"

libraryDependencies ++= Seq(
  "com.datastax.oss" % "java-driver-core" % "4.9.0",
  "com.datastax.oss" % "java-driver-query-builder" % "4.9.0",
  "com.typesafe" % "config" % "1.4.1"
)
