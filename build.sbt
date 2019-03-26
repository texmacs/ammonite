name := "TeXmacs.scala"

scalaVersion := "2.12.8"

libraryDependencies ++= Seq(
  "com.lihaoyi" % "ammonite_2.12.8" % "1.6.5",
  "org.scalatest" %% "scalatest" % "3.0.6" % "test",
)

assemblyJarName in assembly := "texmacs.jar"
