package org.texmacs

import ammonite.util.Res.{Failure, Success}
import org.texmacs.repl.Repl

object TeXmacs {
  val repl = new Repl
  var line = -1

  /**
    * Eval the code snippets and return the result string
    * @param code Code snippets to eval
    * @return
    */
  def eval(code: String): String = {
    line = line + 1
    try {
      repl.run(code, line) match {
        case (Success(_), _, result, _, _, _) =>
          result
        case (Failure(_), _, _, _, errorMessage, _) =>
          errorMessage
      }
    } catch {
      case e: Throwable =>
        e.getMessage
    }
  }
}
