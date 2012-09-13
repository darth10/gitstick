package org.gitstick.printers

/*
 * Copyright (c) 2012, Akhil Wali <akhil.wali.10@gmail.com>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

import org.gitstick.lib.Ticket
import org.gitstick.lib.Repository

case class TagPrinter(tags: List[String], tickets: List[Ticket], tagsToFilter: Set[String])
  extends Printer {

  lazy val filteredTags =
    if (tagsToFilter.size == 0)
      tags
    else
      tags.toSet.intersect(tagsToFilter).toList

  def getColumnLengths = List(
    getLongestAtColumn(0)(filteredTags),
    getLongestAtColumn(1)(List(tickets.length toString)))

  def getHeaders = List(
    "Tag",
    "Tickets")

  def print: Unit = {
    printData(filteredTags) { (tag, index) =>
      println(getLineFormat format (
        tag,
        tickets filter (_ containsAnyTags (Set(tag))) length))
    }

    log info ("%d tag(s), %d ticket(s)" format (
      filteredTags length,
      tickets filter (_ containsAnyTags (tagsToFilter)) length))
  }
}

object TagPrinter {
  def apply(
    repo: Repository,
    tagsToFilter: List[String]): TagPrinter = TagPrinter(repo.getTags, repo.getTickets, tagsToFilter.toSet)
}
