package org.gitstick.util

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

import org.gitstick.lib.{ Repository, Ticket, User }
import org.gitstick.printers._
import ch.qos.logback.classic.joran.JoranConfigurator

class CommandHelper
  extends OptionParser
  with Logging {
  lazy val repo = AppState.repo
  lazy val userList = AppState.repo.userList
  lazy val repoUser = User(repo.userName, "", repo.userEmail)

  final val usageString = "Usage: gitstick [command] [<git repository path>]"
  override protected val helpMessage = "Show help"

  var portNo: Int = 8080
  var newTicketTitleOption: Option[String] = None
  var branchOption: Option[String] = None
  var addUsers = List.empty[String]
  var removeUsers = List.empty[String]
  var tagsToFilter = List.empty[String]
  var assigneesToFilter = List.empty[String]
  var statesToFilter = List.empty[Symbol]
  var isSecure = true
  var toEmptyBranch = false
  var toStartServer = true
  var toShowUsers = false
  var toShowTickets = false
  var toShowTags = false
  var toShowTicketsWithNoTags = false
  var toShowUnassignedTickets = false
  var hasAllTags = false

  banner = usageString
  separator("")
  separator("Commands:")

  flag("-u", "--show-users", "Show users") { () =>
    toShowUsers = true
  }

  // TODO implement -i option to import tickets from CSV file or git repository
  // TODO implement -x option to export tickets in CSV format

  flag("-g", "--show-tags", "Show tags") { () =>
    toShowTags = true
  }

  flag("-t", "--show-tickets", "Show tickets") { () =>
    toShowTickets = true
  }

  list[String]("", "--tag=T1[,T2,...]", "Filter tickets by tag(s)") { tags =>
    tagsToFilter = tags
  }

  flag("", "--alltags", "Show tickets with all tags") { () =>
    hasAllTags = true
  }

  flag("", "--notags", "Show tickets with no tags") { () =>
    toShowTicketsWithNoTags = true
  }

  list[String]("", "--assigned=A1[,A2,...]", "Filter tickets by assignee(s)") { assignees =>
    assigneesToFilter = assignees
  }

  flag("", "--unassigned", "Show unassigned tickets") { () =>
    toShowUnassignedTickets = true
  }

  list[String]("", "--state=S1[,S2,...]", "Filter tickets by state(s)") { states =>
    statesToFilter = states map (_ toLowerCase) map (Symbol(_))
  }

  // FIXME "gitstick -a "u1:p1" throws exception
  list[String]("-a", "--add-users=U1[,U2,...]", "Add or edit user(s)") { u =>
    addUsers = u
  }

  list[String]("-r", "--rm-users=U1[,U2,...]", "Delete user(s)") { u =>
    removeUsers = u
  }

  reqd[String]("-n", "--new-ticket=TICKETTITLE", "Add a new ticket") { ticketTitle =>
    newTicketTitleOption = Some(ticketTitle)
  }

  // FIXME "gitstick -b st5 -u" shows users in gitstick branch
  reqd[String]("-b", "--branch=BRANCH", "Switch to a branch") { branch =>
    branchOption = Some(branch)
  }

  flag("", "--empty", "Create empty branch if no branch exists") { () =>
    toEmptyBranch = true
  }

  flag("", "--debug", "Debug mode") { () =>
    setDebug
  }

  bool("", "--open", "Open/Secure mode") { open =>
    isSecure = !open
  }

  bool("", "--server", "Start/Skip server") { server =>
    toStartServer = server
  }

  reqd[Int]("-p", "--port=PORT", "Set server port") { port =>
    portNo = port
  }

  reqd[String]("-l", "--log=LOGDIRPATH", "Set log directory path") { logfile =>
    setLogFile(logfile)
  }

  def exit = sys exit (_: Int)

  def toExit: Unit = toStartServer = false

  def checkStates: Unit = statesToFilter map { state =>
    if (!Ticket.states.contains(state name)) {
      log error ("Invalid ticket state: " + (state name))
      exit(1)
    }
  }

  def createTicket = {
    if (newTicketTitleOption isDefined) {
      val newTicketTitle = newTicketTitleOption get

      val newTicket = Ticket(repo, newTicketTitle)
      newTicket.create(repoUser)

      log info ("Added new ticket successfully")
      toExit
    }
  }

  def showTickets = {
    if (toShowTickets) {
      checkStates

      TicketPrinter(
        repo,
        tagsToFilter, assigneesToFilter, statesToFilter,
        hasAllTags, toShowTicketsWithNoTags, toShowUnassignedTickets).print
      toExit
    }
  }

  def showUsers = {
    if (toShowUsers) {
      UserPrinter(userList).print
      toExit
    }
  }

  def showTags = {
    if (toShowTags) {
      TagPrinter(repo, tagsToFilter).print
      toExit
    }
  }

  def setRepository(fileArgs: List[String]) = {
    if (fileArgs.length == 1) {
      AppState setRepository (fileArgs.apply(0))
    } else if (fileArgs.length == 0) {
      AppState setRepository (System.getProperty("user.dir"))
    } else {
      log error ("Too many arguments")
      log info (usageString)
      exit(1)
    }
  }

  def setBranch =
    if (branchOption isDefined)
      AppState setBranch (branchOption.get, toEmptyBranch)

  def setSecureMode = AppState setSecureMode (isSecure)

  def getUserTuple(user: String) = {
    val firstSep = user indexOf (":")
    val lastSep = user lastIndexOf (":")
    val username = user substring (0, firstSep) trim
    val password = user substring (firstSep + 1, lastSep) trim
    val email = user substring (lastSep + 1) trim

    (username, password, email)
  }

  def checkAndAddUsers = {
    val filteredUsers = addUsers map (getUserTuple(_)) filter { case (user, password, email) => user.length != 0 }
    filteredUsers map { case (user, password, email) => userList setUser (user, password, email) }

    if (filteredUsers.length > 0) {
      val userOptionsMessage = "added/edited user(s)"

      repo.saveUsers(userOptionsMessage)(repoUser)
      log info (userOptionsMessage)
      toExit
    }
  }

  def checkAndRemoveUsers = {
    val filteredRemoveUsers = removeUsers map (_ trim) filter (_.length > 0)
    filteredRemoveUsers map (userList removeUser (_))

    if (filteredRemoveUsers.length > 0) {
      val removeUserOptionsMessage = "deleted user(s)"
      val user = User(repo.userName, "", repo.userEmail)

      repo.saveUsers(removeUserOptionsMessage)(repoUser)
      log info (removeUserOptionsMessage)
      toExit
    }
  }

  def checkForSkipServer = {
    if (!toStartServer) {
      exit(0)
    }
  }

  def parseArgs(args: Array[String]) = {
    try {
      val fileArgs = parse(args)

      setRepository(fileArgs)
      setBranch

      createTicket

      showUsers
      showTickets
      showTags

      checkAndAddUsers
      checkAndRemoveUsers

      checkForSkipServer
      setSecureMode

    } catch {
      case opex: OptionParserException => {
        println(opex getMessage)
        log debug ((opex getStackTrace) mkString ("\n"))
        exit(1)
      }
    }
  }
}
