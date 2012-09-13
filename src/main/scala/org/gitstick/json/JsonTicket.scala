package org.gitstick.json

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

import java.util.Date
import net.liftweb.json.JsonDSL._
import net.liftweb.json.JsonAST.JValue
import org.gitstick.lib.{ Ticket, Comment, Repository }

case class JsonTicket(name: String, title: String, time: String, assigned: String, state: String,
  tags: List[String],
  comments: List[JsonComment])
  extends ToJson
  with FromJson[Ticket] {

  import JsonTicket._

  var repo: Repository = null

  def fromJson = Ticket(
    repo,
    title,
    assigned,
    Symbol(state),
    tags,
    comments map (_ fromJson),
    (if (time.length != 0) dateFormat.parse(time) else new Date),
    (if (name.length != 0) name else Ticket getTicketName (title)))

  def toJson = (
    ("name" -> name) ~
    ("title" -> title) ~
    ("time" -> time) ~
    ("assigned" -> assigned) ~
    ("state" -> state) ~
    ("tags" -> tags) ~
    ("comments" -> (comments map (_ toJson))))
}

object JsonTicket extends DateFormatterForJson {
  def apply(ticket: Ticket): JsonTicket = {
    val ticketName = if ((ticket name) equals ("")) Ticket getTicketName (ticket title) else (ticket name)
    val retval = new JsonTicket(
      ticketName, ticket.title, dateFormat.format(ticket.time), ticket.assignedTo, ticket.state.name,
      (ticket tags) map (_ replaceAll ("-", " ")),
      (ticket comments) map (JsonComment(_)))

    retval repo = ticket.repo
    retval
  }
}
