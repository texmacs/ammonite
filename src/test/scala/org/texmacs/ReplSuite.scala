package org.texmacs

import org.scalatest._

class ReplSuite extends FunSuite {
  test("val a = ") {
    assert(TeXmacs.eval("val a =") === "Invalid Code")
  }
}
