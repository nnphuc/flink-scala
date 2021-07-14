ThisBuild / resolvers ++= Seq(
  "Apache Development Snapshot Repository" at "https://repository.apache.org/content/repositories/snapshots/",
  Resolver.mavenLocal
)

name := "Flink-scala"

version := "0.1-SNAPSHOT"

organization := "org.example"

ThisBuild / scalaVersion := "2.11.12"

val flinkVersion = "1.12.0"

val flinkDependencies = Seq(
  "org.apache.flink" %% "flink-clients" % flinkVersion % "provided",
  "org.apache.flink" %% "flink-scala" % flinkVersion % "provided",
  "org.apache.flink" %% "flink-streaming-scala" % flinkVersion % "provided",
  "org.apache.flink" %% "flink-connector-kafka" % flinkVersion % "provided",
  "org.apache.flink" %% "flink-cep" % flinkVersion % "provided",
  "org.apache.flink" % "flink-table" % flinkVersion % "provided" pomOnly(),
  "org.apache.flink" % "flink-table-common" % flinkVersion % "provided",
  "org.apache.flink" %% "flink-table-planner" % flinkVersion % "provided",
  "org.apache.flink" %% "flink-table-api-scala-bridge" % flinkVersion % "provided",
  "org.apache.flink" %% "flink-table-api-java-bridge" % flinkVersion % "provided",
  "org.apache.flink" %% "flink-table-planner-blink" % flinkVersion % "provided",
  "org.apache.flink" %% "flink-table-runtime-blink" % flinkVersion % "provided",
  "org.apache.flink" % "flink-csv" % flinkVersion,
  "org.apache.flink" % "force-shading" % flinkVersion,
  "org.apache.flink" %% "flink-connector-kafka" % flinkVersion % "provided"
)

libraryDependencies += "org.apache.logging.log4j" % "log4j-api" % "2.12.1" % "runtime"
libraryDependencies += "org.apache.logging.log4j" % "log4j-core" % "2.12.1" % "runtime"
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.1.2"
libraryDependencies += "com.google.code.findbugs" % "jsr305" % "3.0.2"


lazy val root = (project in file(".")).
  settings(
    libraryDependencies ++= flinkDependencies
  )

assembly / mainClass := Some("org.example.Job")

// make run command include the provided dependencies
Compile / run := Defaults.runTask(Compile / fullClasspath,
  Compile / run / mainClass,
  Compile / run / runner
).evaluated

// stays inside the sbt console when we press "ctrl-c" while a Flink programme executes with "run" or "runMain"
Compile / run / fork := true
Global / cancelable := true

// exclude Scala library from assembly
assembly / assemblyOption := (assembly / assemblyOption).value.copy(includeScala = false)
