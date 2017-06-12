organization := "com.github.workingDog"

name := "stixtoneo4j"

version := (version in ThisBuild).value

scalaVersion := "2.12.2"

crossScalaVersions := Seq("2.12.2")

libraryDependencies ++= Seq(
//  "com.github.workingDog" %% "scalastix" % "1.0"
)

homepage := Some(url("https://github.com/workingDog/StixToNeo4j"))

licenses := Seq("Apache 2" -> url("http://www.apache.org/licenses/LICENSE-2.0"))

mainClass in(Compile, run) := Some("com.kodekutters.StixToNeo4j")

mainClass in assembly := Some("com.kodekutters.StixToNeo4j")

assemblyJarName in assembly := "stixtoneo4j-1.0.jar"
