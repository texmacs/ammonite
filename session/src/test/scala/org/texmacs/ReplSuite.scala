package org.texmacs

import org.scalatest._

class ReplSuite extends FunSuite {
  test("val a = ") {
    println(TeXmacs.eval("val a = 1"))
    println(TeXmacs.eval("a"))
    println(TeXmacs.eval("res1"))
  }
}
