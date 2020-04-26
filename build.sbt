name := "TeXmacs.scala"

scalaVersion := "2.12.11"

libraryDependencies ++= Seq(
  "com.lihaoyi" % "ammonite_2.12.11" % "2.1.0",
  "org.scalatest" %% "scalatest" % "3.0.6" % "test",
)

assemblyJarName in assembly := "texmacs.jar"

assemblyMergeStrategy in assembly := {
  case "reflect.properties" => MergeStrategy.last
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}
