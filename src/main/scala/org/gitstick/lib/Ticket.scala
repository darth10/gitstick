package org.gitstick.lib

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

import java.util.Random
import java.util.Date
import java.io.File
import org.gitstick.util.FileHelper
import org.gitstick.util.GitSupport
import org.gitstick.json.GeneratorForJson
import org.gitstick.json.ParserForJson
import org.gitstick.json.JsonTicket
import org.gitstick.json.JsonTicket._
import net.liftweb.json.JsonAST.JValue

// TODO test pending
class Ticket(var repo: Repository)
  extends GeneratorForJson
  with GitSupport {

  var title: String = ""
  var name: String = ""
  var assignedTo: String = ""
  var state: Symbol = 'open
  var tags: List[String] = List.empty[String]
  var comments: List[Comment] = List.empty[Comment]
  var time: Date = new Date

  import Ticket._

  override def toString = getJson

  def getGenerator = JsonTicket(this)

  def getRepository = repo

  def toShortString = "'" + title + "'" + " (" + name + ")"

  def contains[T](set: Set[T])(predicate: Set[T] => Boolean) =
    if ((set.size) == 0)
      true
    else
      predicate(set)

  def containsAnyTags = contains(_: Set[String]) { tagsToCheck =>
    tags.toSet.intersect(tagsToCheck).size > 0
  }

  def containsAllTags = contains(_: Set[String]) { tagsToCheck =>
    tags.toSet.intersect(tagsToCheck) == tagsToCheck
  }

  def containsNoTags = (tags.length == 0)

  def containsAnyState = contains(_: Set[Symbol]) { statesToCheck =>
    statesToCheck contains (state)
  }

  def containsAnyAssigned = contains(_: Set[String]) { assigneesToCheck =>
    assigneesToCheck contains (assignedTo)
  }

  def isUnassigned = (assignedTo.length == 0)

  def create(user: User): Unit = {
    require(title.length != 0)
    require(repo != null)

    name = getTicketName(title)

    val ticketDir = new File(repo.workingDir, name)
    FileHelper emptyDirectory (ticketDir)

    val assignedStr = cleanString(assignedTo)
    val stateStr = getStateFilename(state)

    git addFile (name, "TICKET_ID", name)
    git addFile (name, getAssignedFilename(assignedStr), assignedStr)
    git addFile (name, stateStr, state.name)

    val cleanComments = comments filter(_.text.length > 0)
    cleanComments map { c =>
      git addFile (name, getCommentFilename(user email), c text)
    }

    val cleanTags = tags filter(_.length > 0)
    cleanTags filter(_.length > 0) map { t =>
      val tagFilename = getTagFilename(t)
      git addFile (name, tagFilename, tagFilename)
    }

    git commitAs (("added ticket " + toShortString), user.name, user.email)
  }

  def update(newState: Symbol): User => Unit = update(newState, assignedTo, tags)_

  def update(assignTo: String): User => Unit = update(state, assignTo, tags)_

  def update(newTags: List[String]): User => Unit = update(state, assignedTo, newTags)_

  def update(newState: Symbol, assignTo: String, newTags: List[String])(user: User): Unit = {
    require(states.contains(newState.name))
    var toCommit = false

    if (!(newState.name equals state.name)) {
      val newStateFilename = getStateFilename(newState)
      git addFile (name, newStateFilename, newState.name)
      git removeFile (name, getStateFilename(state))
      state = newState
      toCommit = true
    }

    if (!(assignTo equals assignedTo)) {
      val newAssignedToFilename = getAssignedFilename(cleanString(assignTo))
      git addFile (name, newAssignedToFilename, assignTo)
      git removeFile (name, getAssignedFilename(assignedTo))
      assignedTo = assignTo
      toCommit = true
    }

    val cleanTags = newTags map (cleanString(_).trim)
    val tagsToRemove = tags diff cleanTags
    val tagsToAdd = cleanTags diff tags

    if (tagsToRemove.size > 0) {
      tagsToRemove map (removeTagWithNoCommit(_))
      toCommit = true
    }

    if (tagsToAdd.size > 0) {
      tagsToAdd map (addTagWithNoCommit(_))
      toCommit = true
    }

    if (toCommit)
      git commitAs (("updated ticket " + toShortString), user.name, user.email)
  }

  def delete(user: User): Unit = {
    require((new File(repo.fileRepository.getWorkTree, name)).exists)

    git removeFile (name)
    git commitAs (("deleted ticket " + toShortString), user.name, user.email)
  }

  def addComment(comment: String)(user: User): Unit = {
    require(name.length != 0)
    require(comment.length != 0)

    val commentFilename = getCommentFilename(user.email)
    val commentFilePath = git addFile (name, commentFilename, comment)
    git commitAs ("added comment to ticket " + toShortString, user.name, user.email)

    val newComment = Comment(new File(commentFilePath))
    comments = newComment :: comments
  }

  private def readNameAndTitle(file: String): Unit = {
    name = FileHelper readFromFile (file)
    val tokens = name split ("_")
    require(tokens.length >= 3)

    val ticketTitle = tokens apply (1) replace ("-", " ")
    title = ticketTitle.capitalize
    time = UnixTime fromUnixTime (tokens apply (0) toInt)
  }

  private def addTagWithNoCommit(tag: String) {
    if (!(tags contains (tag))) {
      val tagFilename = getTagFilename(tag)
      git addFile (name, tagFilename, tag)
      tags = tag :: tags
    }
  }

  private def removeTagWithNoCommit(tag: String) {
    if (tags contains (tag)) {
      val tagFilename = getTagFilename(tag)
      git removeFile (name, tagFilename)
      tags = tags filterNot (tag equals (_))
    }
  }
}

object Ticket extends ParserForJson[Ticket] {
  val states = List("open", "resolved", "hold", "ignored")
  private val rand = new Random

  def apply(repo: Repository,
    ticketTitle: String = "",
    ticketAssignedTo: String = "",
    ticketState: Symbol = 'open,
    ticketTags: List[String] = List.empty[String],
    ticketComments: List[Comment] = List.empty[Comment],
    ticketTime: Date = new Date,
    ticketName: String = ""): Ticket =
    new Ticket(repo) {
      title = ticketTitle
      name = if (ticketName.length == 0) getTicketName(ticketTitle) else ticketName
      assignedTo = ticketAssignedTo
      state = ticketState
      tags = ticketTags
      comments = ticketComments
      time = ticketTime
    }

  def apply(repo: Repository, ticketDir: File): Ticket = {
    require(ticketDir.isDirectory)
    val ticket = new Ticket(repo)

    (ticketDir listFiles) map { file =>
      val fileNameParts = file getName () split ("_")

      fileNameParts apply (0) match {
        case "ASSIGNED" if (fileNameParts.length == 2) => ticket assignedTo = fileNameParts apply (1)
        case "COMMENT" => ticket comments = Comment(file) :: (ticket comments)
        case "TAG" => ticket tags = (fileNameParts apply (1)) :: (ticket tags)
        case "STATE" => ticket state = Symbol(fileNameParts apply (1))
        case "TICKET" if (fileNameParts apply (1) equals ("ID")) => ticket readNameAndTitle (file getAbsolutePath)
        case _ => // do nothing
      }
    }

    ticket
  }

  def getJsonParser(jValue: JValue) = jValue.extract[JsonTicket]

  def parseTags = (_: String) split (",") map (_ trim)

  def cleanString = (_: String) toLowerCase () replaceAll ("[^a-z0-9]+", "-")

  def getTicketName = (UnixTime getUnixTime) + "_" + cleanString(_: String) + "_" + rand.nextInt(999)

  def getCommentFilename = "COMMENT_" + (UnixTime getUnixTime) + "_" + (_: String)

  def getTagFilename = "TAG_" + (_: String)

  def getStateFilename = "STATE_" + getStateName(_: Symbol)

  def getAssignedFilename = "ASSIGNED_" + (_: String)

  def getStateName = (_: Symbol).name
}
