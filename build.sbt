name := "akka-stream-presentation"
organization := "io.thinkin"

scalaVersion := "2.13.6"

scalacOptions --= Seq(
  "-Wvalue-discard",
  "-Xfatal-warnings"
)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % "2.6.16"
)
