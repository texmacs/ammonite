package org.texmacs

object Protocol {
  val DATA_COMMAND = '\u0010'
  val DATA_BEGIN = '\u0002'
  val DATA_END = '\u0005'

  def flushBegin(): Unit = {
    print(DATA_BEGIN)
  }

  def flushEnd(): Unit = {
    print(DATA_END)
    System.out.flush()
  }

  def flushAny(anyStr: String): Unit = {
    flushBegin()
    print(anyStr)
    flushEnd()
  }

  def flushVerbatim(str: String): Unit = {
    flushAny(s"verbatim:$str")
  }

  def flushPrompt(prompt: String): Unit = {
    flushAny(s"prompt#$prompt")
  }
}
