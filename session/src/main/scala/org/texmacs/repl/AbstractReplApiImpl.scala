/*
 * Copyright (C) 2020  Darcy Shen
 *
 * This software falls under the GNU general public license version 3 or later.
 * It comes WITHOUT ANY WARRANTY WHATSOEVER. For details, see the file LICENSE
 * in the root directory or <http://www.gnu.org/licenses/gpl-3.0.html>.
 */

package org.texmacs.repl

import ammonite.repl.ReplApiImpl
import ammonite.util.Bind
import ammonite.util.Ref
import ammonite.repl.api.FrontEnd
import ammonite.repl.api.History
import ammonite.util.Colors

trait AbstractReplApiImpl extends ReplApiImpl {
  override def replArgs0 = Vector.empty[Bind[_]]
  override val prompt = Ref("@ ")
  override val frontEnd = Ref[FrontEnd](null)
  override def lastException: Throwable = null
  override def history = new History(Vector())
  override val colors = Ref(Colors.BlackWhite)
  override def width = 80
  override def height = 80
}