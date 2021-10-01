/*
 * Copyright (C) 2020  Darcy Shen
 *
 * This software falls under the GNU general public license version 3 or later.
 * It comes WITHOUT ANY WARRANTY WHATSOEVER. For details, see the file LICENSE
 * in the root directory or <http://www.gnu.org/licenses/gpl-3.0.html>.
 */

package org.texmacs.repl

import ammonite.compiler.iface.{CodeWrapper, CompilerLifecycleManager}
import ammonite.interp.Interpreter
import ammonite.ops.{Path, read}
import ammonite.repl._
import ammonite.repl.api.{FrontEnd, ReplLoad}
import ammonite.runtime.{Frame, Storage}
import ammonite.util.Util.normalizeNewlines
import ammonite.util._
import ammonite.compiler.{CompilerBuilder, DefaultCodeWrapper, Parsers}

import java.io.{ByteArrayOutputStream, PrintStream}
import scala.collection.mutable

class TRepl {
  var allOutput = ""
  def predef: (String, Option[ammonite.ops.Path]) = ("", None)
  def codeWrapper: CodeWrapper = DefaultCodeWrapper

  val tempDir = ammonite.ops.Path(
    java.nio.file.Files.createTempDirectory("ammonite-tester"))

  val outBytes = new ByteArrayOutputStream
  val errBytes = new ByteArrayOutputStream
  val resBytes = new ByteArrayOutputStream
  def outString = new String(outBytes.toByteArray)
  def resString = new String(resBytes.toByteArray)

  val warningBuffer = mutable.Buffer.empty[String]
  val errorBuffer = mutable.Buffer.empty[String]
  val infoBuffer = mutable.Buffer.empty[String]
  val printer0 = Printer(
    new PrintStream(outBytes),
    new PrintStream(errBytes),
    new PrintStream(resBytes),
    x => warningBuffer.append(x + Util.newLine),
    x => errorBuffer.append(x + Util.newLine),
    x => infoBuffer.append(x + Util.newLine))
  val storage = new Storage.Folder(tempDir)
  val frames = Ref(List(Frame.createInitial()))
  val sess0 = new SessionApiImpl(frames)

  var currentLine = 0
  val interp: Interpreter = try {
    new Interpreter(
      compilerBuilder = CompilerBuilder,
      parser = Parsers,
      printer0,
      storage = storage,
      wd = ammonite.ops.pwd,
      colors = Ref(Colors.BlackWhite),
      getFrame = () => frames().head,
      createFrame = () => { val f = sess0.childFrame(frames().head); frames() = f :: frames(); f },
      replCodeWrapper = codeWrapper,
      scriptCodeWrapper = codeWrapper,
      alreadyLoadedDependencies = Seq.empty)
  } catch {
    case e: Throwable =>
      println(infoBuffer.mkString)
      println(outString)
      println(resString)
      println(warningBuffer.mkString)
      println(errorBuffer.mkString)
      throw e
  }

  val replApi = new AbstractReplApiImpl {
    override def printer = printer0
    override def sess = sess0
    override def fullHistory = storage.fullHistory()
    override def newCompiler() = interp.compilerManager.init(force = true)
    override def fullImports = interp.predefImports ++ imports
    override def imports = interp.frameImports
    override def usedEarlierDefinitions = interp.frameUsedEarlierDefinitions
    override def _compilerManager = interp.compilerManager

    object load extends ReplLoad with (String => Unit) {

      def apply(line: String) = {
        interp.processExec(line, currentLine, () => currentLine += 1) match {
          case Res.Failure(s) => throw new CompilationError(s)
          case Res.Exception(t, s) => throw t
          case _ =>
        }
      }

      def exec(file: Path): Unit = {
        interp.watch(file)
        apply(normalizeNewlines(read(file)))
      }
    }

  }

  val basePredefs = Seq(
    PredefInfo(
      Name("defaultPredef"),
      predef._1,
      false,
      None),
    PredefInfo(Name("testPredef"), predef._1, false, predef._2))
  val customPredefs = Seq()
  val extraBridges = Seq(("ammonite.repl.ReplBridge", "repl", replApi))

  for ((error, _) <- interp.initializePredef(basePredefs, customPredefs, extraBridges)) {
    val (msgOpt, causeOpt) = error match {
      case r: Res.Exception => (Some(r.msg), Some(r.t))
      case r: Res.Failure => (Some(r.msg), None)
      case _ => (None, None)
    }

    println(infoBuffer.mkString)
    println(outString)
    println(resString)
    println(warningBuffer.mkString)
    println(errorBuffer.mkString)
    throw new Exception(
      s"Error during predef initialization${msgOpt.fold("")(": " + _)}",
      causeOpt.orNull)
  }

  def session(sess: String): Unit = {
    // Remove the margin from the block and break
    // it into blank-line-delimited steps
    val margin = sess.linesIterator.filter(_.trim != "").map(_.takeWhile(_ == ' ').length).min
    // Strip margin & whitespace

    val steps = sess.replace(
      Util.newLine + margin, Util.newLine).replaceAll(" *\n", "\n").split("\n\n")

    for ((step, index) <- steps.zipWithIndex) {
      // Break the step into the command lines, starting with @,
      // and the result lines
      val (cmdLines, resultLines) =
        step.linesIterator.toArray.map(_.drop(margin)).partition(_.startsWith("@"))

      val commandText = cmdLines.map(_.stripPrefix("@ ")).toVector

      println(cmdLines.mkString(Util.newLine))
      // Make sure all non-empty, non-complete command-line-fragments
      // are considered incomplete during the parse
      //
      // ...except for the empty 0-line fragment, and the entire fragment,
      // both of which are complete.
      for (incomplete <- commandText.inits.toSeq.drop(1).dropRight(1)) {
        assert(Parsers.split(incomplete.mkString(Util.newLine)).isEmpty)
      }

      // Finally, actually run the complete command text through the
      // interpreter and make sure the output is what we expect
      val expected = resultLines.mkString(Util.newLine).trim
      allOutput += commandText.map(Util.newLine + "@ " + _).mkString(Util.newLine)

      val (processed, out, res, warning, error, info) =
        run(commandText.mkString(Util.newLine), currentLine)

      val allOut = out + res

      if (expected.startsWith("error: ")) {
        val strippedExpected = expected.stripPrefix("error: ")
        assert(error.contains(strippedExpected))

      } else if (expected.startsWith("warning: ")) {
        val strippedExpected = expected.stripPrefix("warning: ")
        assert(warning.contains(strippedExpected))

      } else if (expected.startsWith("info: ")) {
        val strippedExpected = expected.stripPrefix("info: ")
        assert(info.contains(strippedExpected))

      } else if (expected == "") {
        processed match {
          case Res.Success(_) => // do nothing
          case Res.Skip => // do nothing
          case _: Res.Failing =>
            assert {
              identity(error)
              identity(warning)
              identity(out)
              identity(res)
              identity(info)
              false
            }
        }

      } else {
        processed match {
          case Res.Success(str) =>
            // Strip trailing whitespace
            def normalize(s: String) =
              s.linesIterator
                .map(_.replaceAll(" *$", ""))
                .mkString(Util.newLine)
                .trim()
            failLoudly(
              assert {
                identity(error)
                identity(warning)
                identity(info)
                normalize(allOut) == normalize(expected)
              })

          case Res.Failure(failureMsg) =>
            assert {
              identity(error)
              identity(warning)
              identity(out)
              identity(res)
              identity(info)
              identity(expected)
              false
            }
          case Res.Exception(ex, failureMsg) =>
            val trace = Repl.showException(
              ex, fansi.Attrs.Empty, fansi.Attrs.Empty, fansi.Attrs.Empty) + Util.newLine + failureMsg
            assert({ identity(trace); identity(expected); false })
          case _ =>
            println(allOutput)
        }
      }
    }
  }

  def run(input: String, index: Int) = {
    outBytes.reset()
    resBytes.reset()
    warningBuffer.clear()
    errorBuffer.clear()
    infoBuffer.clear()
    val splitted = Parsers.split(input).getOrElse {
      throw new Exception("Invalid Code")
    }.getOrElse {
      throw new Exception("Invalid Code")
    }
    val processed = interp.processLine(
      input,
      splitted,
      index,
      false,
      () => currentLine += 1)
    processed match {
      case Res.Failure(s) => printer0.error(s)
      case Res.Exception(throwable, msg) =>
        printer0.error(
          Repl.showException(throwable, fansi.Attrs.Empty, fansi.Attrs.Empty, fansi.Attrs.Empty))

      case _ =>
    }
    Repl.handleOutput(interp, processed)
    (
      processed,
      outString,
      resString,
      warningBuffer.mkString,
      errorBuffer.mkString,
      infoBuffer.mkString)
  }

  def fail(
    input: String,
    failureCheck: String => Boolean = _ => true) = {
    val (processed, out, _, warning, error, info) = run(input, 0)

    processed match {
      case Res.Success(v) => assert({ identity(v); identity(allOutput); false })
      case Res.Failure(s) =>
        failLoudly(assert(failureCheck(s)))
      case Res.Exception(ex, s) =>
        val msg = Repl.showException(
          ex, fansi.Attrs.Empty, fansi.Attrs.Empty, fansi.Attrs.Empty) + Util.newLine + s
        failLoudly(assert(failureCheck(msg)))
      case _ => ???
    }
  }

  def result(input: String, expected: Res[Evaluated]) = {
    val (processed, allOut, _, warning, error, info) = run(input, 0)
    assert(processed == expected)
  }

  def failLoudly[T](t: => T) =
    try t
    catch {
      case e: Exception =>
        println("FAILURE TRACE" + Util.newLine + allOutput)
        throw e
    }
}

