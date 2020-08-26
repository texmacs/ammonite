import mill._, scalalib._

object session extends SbtModule {
  def scalaVersion = "2.13.1"
  def mainClass = Some("org.texmacs.TeXmacs")

  def ivyDeps = Agg(
    ivy"com.lihaoyi:ammonite_${scalaVersion()}:2.2.0"
  )

  object test extends Tests {
    def ivyDeps = Agg(ivy"org.scalatest::scalatest:3.1.1")
    def testFrameworks = Seq("org.scalatest.tools.Framework")
  }
}
