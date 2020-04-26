package org.texmacs

import ammonite.util.Res.{ Failure, Success }
import org.texmacs.repl.Repl
import org.texmacs.Protocol._

import scala.collection.mutable.ArrayBuffer
import scala.io.StdIn
import scala.util.control.Breaks._

object TeXmacs {
  val repl = new Repl
  var line: Int = -1

  /**
   * Eval the code snippets and return the result string
   * @param code Code snippets to eval
   * @return
   */
  def eval(code: String): Unit = {
    line = line + 1
    try {
      repl.run(code, line) match {
        case (Success(_), _, result, _, _, _) =>
          flushVerbatim(result)
        case (Failure(_), _, _, _, errorMessage, _) =>
          flushVerbatim(errorMessage)
      }
    } catch {
      case e: Throwable =>
        e.getMessage
    }
  }

  def loop(): Unit = {
    while (true) {
      val line = StdIn.readLine()
      Option(line).filter(_.nonEmpty).foreach(l => {
        if (l(0) == DATA_COMMAND) {

        } else {
          val buffer = ArrayBuffer[String]()
          buffer.append(l)

          breakable {
            while (true) {
              val moreLine = StdIn.readLine()
              if (moreLine.startsWith("<EOF>")) break
              buffer.append(moreLine)
            }
          }
          eval(buffer.mkString("\n"))
        }
      })
    }
  }

  def main(arr: Array[String]): Unit = {
    flushPrompt(repl.replApi.prompt.apply())
    loop()
  }
}
