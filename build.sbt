name := "TeXmacs.scala"

scalaVersion := "2.12.10"

libraryDependencies ++= Seq(
  "com.lihaoyi" % "ammonite_2.12.10" % "1.8.0",
  "org.scalatest" %% "scalatest" % "3.0.6" % "test",
)

assemblyJarName in assembly := "texmacs.jar"

assemblyMergeStrategy in assembly := {
  case "reflect.properties" => MergeStrategy.last
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}
