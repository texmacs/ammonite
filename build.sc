import mill._
import os.up
import scalalib._

object session extends SbtModule with ScalaModule {
  def scalaVersion = "2.13.6"

  override def scalacOptions = Seq("-deprecation")

  override def mainClass = Some("org.texmacs.TeXmacs")

  override def ivyDeps = Agg(
    ivy"com.lihaoyi:ammonite_${scalaVersion()}:2.4.0"
  )

  object test extends Tests {
    override def ivyDeps = Agg(ivy"org.scalatest::scalatest:3.1.1")
    def testFrameworks = Seq("org.scalatest.tools.Framework")
  }

  def install = T {
    assembly()

    // Make the dist
    val distDir = T.dest / "scala"
    if (os.exists (distDir)) os.remove.all(distDir)
    os.makeDir(distDir)
    os.copy(millSourcePath / "progs", distDir / "progs")
    os.copy(
      millSourcePath / up / "out" / "session" / "assembly" / "dest" / "out.jar",
      distDir / "texmacs.jar"
    )

    // Install
    val targetDir = os.home / ".TeXmacs" / "plugins" / "scala"
    if (os.exists(targetDir)) os.remove.all(targetDir)
    os.copy(distDir, targetDir)
  }
}
