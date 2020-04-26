name := "TeXmacs.scala"

headerLicense := Some(HeaderLicense.Custom(
  """|Copyright (C) 2020  Darcy Shen
     |
     |This software falls under the GNU general public license version 3 or later.
     |It comes WITHOUT ANY WARRANTY WHATSOEVER. For details, see the file LICENSE
     |in the root directory or <http://www.gnu.org/licenses/gpl-3.0.html>.
     |""".stripMargin
))

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
