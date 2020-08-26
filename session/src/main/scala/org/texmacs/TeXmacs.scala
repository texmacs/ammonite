/*
 * Copyright (C) 2020  Darcy Shen
 *
 * This software falls under the GNU general public license version 3 or later.
 * It comes WITHOUT ANY WARRANTY WHATSOEVER. For details, see the file LICENSE
 * in the root directory or <http://www.gnu.org/licenses/gpl-3.0.html>.
 */

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

  def welcome(): Unit = {
    flushVerbatim(s"Play with your favorite language (Scala ${util.Properties.versionNumberString}) in TeXmacs\n")
    flushVerbatim(s"Created by Darcy Shen, based on Ammonite ${ammonite.Constants.version}\n")
    flushVerbatim(s"Welcome to star and fork it at https://github.com/texmacs/TeXmacs.scala\n")
  }

  def main(arr: Array[String]): Unit = {
    welcome()
    flushPrompt(repl.replApi.prompt.apply())
    loop()
  }
}
