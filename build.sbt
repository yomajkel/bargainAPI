name := "bargainAPI"

version := "0.1"

scalaVersion := "2.12.6"

fork in (Test) := true
javaOptions in Test += "-Dconfig.file=src/main/resources/application.test.conf"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.0.10",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.10",
  "org.scalaz" %% "scalaz-core" % "7.2.25",
  "org.postgresql" % "postgresql" % "9.3-1100-jdbc4",
  "com.typesafe.slick" %% "slick" % "2.1.0",
  "org.flywaydb" % "flyway-core" % "5.1.4",
  "c3p0" % "c3p0" % "0.9.1.2",
  "com.amazonaws" % "aws-java-sdk" % "1.11.377"
)

libraryDependencies ++= Seq(
  "org.scalactic" %% "scalactic" % "3.0.5",
  "org.scalatest" %% "scalatest" % "3.0.5" % "test",
  "com.typesafe.akka" %% "akka-http-testkit" % "10.0.10"
)