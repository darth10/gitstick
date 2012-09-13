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

import java.text.SimpleDateFormat
import org.gitstick.lib.Ticket
import org.gitstick.lib.Repository

case class TicketPrinter(
  tickets: List[Ticket],
  tags: Set[String],
  assignees: Set[String],
  states: Set[Symbol],
  allTags: Boolean, noTags: Boolean, unassigned: Boolean)
  extends Printer {
  final val ticketMaxLength = 20
  final val assignedMaxLength = 8

  lazy val dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss")

  lazy val filteredTickets =
    tickets filter { ticket =>
      if (noTags)
        ticket containsNoTags
      else if (allTags)
        ticket containsAllTags (tags)
      else
        ticket containsAnyTags (tags)
    } filter { ticket =>
      if (unassigned)
        ticket isUnassigned
      else
        ticket containsAnyAssigned (assignees)
    } filter (_ containsAnyState (states))

  def getColumnLengths = List(
    getLongestAtColumn(0)(List(filteredTickets.length.toString)),
    getLongestWithLimitAtColumn(ticketMaxLength)(1)(filteredTickets map (_ title)),
    getLongestWithLimitAtColumn(assignedMaxLength)(2)(filteredTickets map (_ assignedTo)),
    getLongestAtColumn(3)(filteredTickets map (_.state name)),
    getLongestAtColumn(4)(filteredTickets map (_.comments length) map (_ toString)),
    getLongestAtColumn(5)(filteredTickets map (t => dateFormat format (t time))))

  def getHeaders = List(
    "#",
    "Title",
    "Assigned To",
    "State",
    "Comments",
    "Created On")

  def print: Unit = {
    printData(filteredTickets) { (ticket, index) =>
      println(getLineFormat format (
        (index + 1) toString,
        getTextWithLimit(getColumnLengthAt(1))(ticket title),
        getTextWithLimit(getColumnLengthAt(2))(ticket assignedTo),
        ticket.state name,
        ticket.comments length,
        dateFormat format (ticket time)))
    }

    log info ("%d ticket(s)" format ((filteredTickets length)))

    if (filteredTickets.length > 0) {
      val statsMesg = Ticket.states map { s =>
        "%d %s" format ((filteredTickets filter (_.state == Symbol(s)) length), s)
      } mkString (", ")

      log info (statsMesg)
    }
  }
}

object TicketPrinter {
  def apply(
    repo: Repository,
    tags: Seq[String] = Seq.empty[String],
    assignees: Seq[String] = Seq.empty[String],
    states: Seq[Symbol] = Seq.empty[Symbol],
    allTags: Boolean = false,
    noTags: Boolean = false,
    unassigned: Boolean = false): TicketPrinter =
    TicketPrinter(repo.getTickets, tags.toSet, assignees.toSet, states.toSet, allTags, noTags, unassigned)
}
