name := "TeXmacs.scala"

headerLicense := Some(HeaderLicense.Custom(
  """|Copyright (C) 2020  Darcy Shen
     |
     |This software falls under the GNU general public license version 3 or later.
     |It comes WITHOUT ANY WARRANTY WHATSOEVER. For details, see the file LICENSE
     |in the root directory or <http://www.gnu.org/licenses/gpl-3.0.html>.
     |""".stripMargin
))

scalaVersion := "2.13.2"

libraryDependencies ++= Seq(
  "com.lihaoyi" % s"ammonite_${scalaVersion.value}" % "2.1.4",
  "org.scalatest" %% "scalatest" % "3.1.1" % "test",
)

assemblyJarName in assembly := "texmacs.jar"

assemblyMergeStrategy in assembly := {
  case "reflect.properties" => MergeStrategy.last
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}
