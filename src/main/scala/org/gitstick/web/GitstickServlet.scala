package org.gitstick.web

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

import java.net.InetAddress
import org.scalatra._
import scalate.ScalateSupport
import org.gitstick.lib.{ Repository, User, Ticket }
import org.gitstick.util.AppState
import org.gitstick.util.Logging

class GitstickServlet
  extends ScalatraServlet
  with ScalateSupport
  with AuthenticationSupport
  with Logging {

  lazy val repo = AppState.repo
  lazy val userList = AppState.repo.userList
  lazy val isSecure = AppState.secureMode.isSecureMode

  // tickets

  get("/:ticketid") {
    handleTicketRequest(params get ("ticketid")) { ticket =>
      renderView("/tickets.edit", ("ticketId" -> ticket.name))
    }
  }

  get("/tickets", isJsonRequest(request)) {
    handleRequest(repo getTicketsAsJson)
  }

  get("/tickets/:ticketid", isJsonRequest(request)) {
    handleTicketRequest(params get ("ticketid")) { ticket =>
      ticket toString
    }
  }

  post("/tickets", isJsonRequest(request)) {
    // request body should contain ticket in json format
    val jsonTicket = request body

    handleRequest {
      val ticket = Ticket parseJson (jsonTicket)

      ticket repo = repo
      ticket.create(user)
      ticket toString
    }
  }

  put("/tickets/:ticketid", isJsonRequest(request)) {
    // request body should contain updated ticket in json format
    val jsonTicket = request body

    handleTicketRequest(params get ("ticketid")) { ticket =>
      val newTicket = Ticket parseJson (jsonTicket)

      ticket.update(newTicket.state, newTicket.assignedTo, newTicket.tags)(user)
      ticket toString
    }
  }

  delete("/tickets/:ticketid") {
    handleTicketRequest(params get ("ticketid")) { ticket =>
      ticket.delete(user)
    }
  }

  post("/tickets/:ticketid/comments", isJsonRequest(request)) {
    // request body should contain comment text
    val commentText = request body

    handleTicketRequest(params get ("ticketid")) { ticket =>
      ticket.addComment(commentText)(user)
      ticket toString
    }
  }

  // tags

  get("/tags", isJsonRequest(request)) {
    handleRequest(repo getTagsAsJson)
  }

  // log

  get("/log", isJsonRequest(request)) {
    handleRequest(repo getCommitsAsJson)
  }

  // settings

  get("/user", isJsonRequest(request)) {
    handleRequest(user toString)
  }

  put("/user/:oldpassword", isJsonRequest(request)) {
    // request body should contain user in json format
    // FIXME username in jsonUser is not checked; a user can change other users' passwords
    val password = params get ("oldpassword")
    val jsonUser = request body

    handleRequest {
      if (isSecure)
        userList authenticateAndSetJsonUser (user name, password, jsonUser)
      else
        userList setJsonUser (jsonUser)

      repo.saveUsers("updated user '" + (user name) + "'")(user)
    }
  }

  // navigation URLs

  get("/add") {
    handleRequest(renderView("/tickets.add"))
  }

  get("/info") {
    handleRequest(renderView("/tickets.info"))
  }

  get("/tickets.index") {
    handleRequest(renderView(requestPath))
  }

  get("/log.index") {
    handleRequest(renderView(requestPath))
  }

  get("/settings.index") {
    handleRequest(renderView(requestPath, ("secure" -> isSecure)))
  }

  get("/") {
    handleRequest(redirect("tickets.index"))
  }

  notFound {
    authenticateAndSendResponse(false) {
      serveStaticResource getOrElse resourceNotFound
    }
  }

  override def init(): Unit = {
    super.init()

    if (AppState.repo == null) {
      AppState isWar = true

      // set repository
      val repoInitParam = getInitParameter("RepositoryPath")
      val repoPath =
        if ((repoInitParam == null) || (repoInitParam.length == 0))
          Repository.defaultPath
        else
          repoInitParam

      AppState setRepository (repoPath)

      // set branch
      val branchNameInitParam = getInitParameter("BranchName")
      if ((branchNameInitParam != null) && (branchNameInitParam.length > 0)) {
        AppState setBranch (branchNameInitParam, false)
      }
    }

    if (AppState.secureMode == null) {
      // set open/secure mode
      val openModeInitParam = getInitParameter("OpenMode")
      val secureMode =
        if (openModeInitParam == null)
          true
        else
          openModeInitParam != "1"

      AppState setSecureMode (secureMode)
    }

    // TODO remove later
    log warn ("Still in development!")
  }

  def isJsonRequest(request: javax.servlet.http.HttpServletRequest): Boolean = {
    val contentType = request.getContentType
    (contentType != null) && (contentType equals "application/json")
  }

  def logRequest(request: javax.servlet.http.HttpServletRequest) {
    val reqInfo = new StringBuilder

    val remoteInfo = request.getRemoteAddr
    if (remoteInfo != null)
      reqInfo append ("%s " format (remoteInfo))

    if (user != null) {
      val userInfo = if (user.email.length > 0) user.email else user.name
      reqInfo append ("(" + user.email + ") ")
    } else {
      reqInfo append ("(Unauthorized) ")
    }

    reqInfo append (request.getMethod + " " + request.getRequestURI)

    val contentType = request.getContentType
    if (contentType != null)
      reqInfo append (" [" + contentType + "]")

    log info (reqInfo toString)
  }

  def renderView(path: String, attributes: (String, Any)*) = {
    findTemplate(path) map { path =>
      contentType = "text/html"
      layoutTemplate(path, attributes.toSeq: _*)
    } getOrElse resourceNotFound
  }

  def handleTicketRequest(ticketIdOption: Option[String])(handler: Ticket => Any): Any = {
    val ticketId = ticketIdOption getOrElse ""
    val ticket = repo getTicketById (ticketId)

    ticket map { ticket =>
      handleRequest(handler(ticket))
    } getOrElse resourceNotFound
  }

  def handleRequest = authenticateAndSendResponse(true)_

  def authenticateAndSendResponse(toLog: Boolean)(handler: => Any): Any = {
    basicAuth

    if (toLog) {
      logRequest(request)
    }

    val response = try {
      handler
    } catch {
      case e: Exception => {
        log error (e getMessage)
        log debug ((e getStackTrace) mkString ("\n"))
        halt(500)
      }
    }

    response
  }
}
