
scalaVersion := "2.13.8"
val AkkaVersion = "2.5.23"
libraryDependencies += "org.scala-lang" % "scala-library" % "2.13.8"
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % AkkaVersion
libraryDependencies += "com.typesafe.akka" %% "akka-stream-typed" % "2.7.0"
libraryDependencies += "com.lightbend.akka" %% "akka-stream-alpakka-file" % "5.0.0"