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

import java.io.File
import org.eclipse.jgit.lib.Config
import org.eclipse.jgit.lib.UserConfig
import org.eclipse.jgit.storage.file.FileRepository
import org.gitstick.lib.Commit._
import org.gitstick.json.GenericJsonGenerator
import org.gitstick.util.FileHelper
import org.gitstick.util.GitHelper
import org.gitstick.util.GitSupport

// TODO test pending
class Repository(val workingDir: String)
  extends GitSupport {
  require(workingDir.length != 0)
  import GenericJsonGenerator._

  val usersFile = new File(workingDir, ".users")
  var fileRepository: FileRepository = new FileRepository(new File(workingDir, ".git"))
  var userList: UserList = null
  var userName = GitHelper anonUserName
  var userEmail = GitHelper anonUserEmail

  def getRepository = this

  def getTickets: List[Ticket] = fileRepository.getWorkTree.listFiles.toList.reverse. // reverse list for reverse chronological order
    filter(_ isDirectory).filterNot(_ getName () equals (".git")).
    map(Ticket(this, _))

  def getTicketById(ticketId: String): Option[Ticket] = getTickets filter (_.name equalsIgnoreCase (ticketId)) headOption

  def getTags: List[String] = getTickets.
    map(_.tags toSet).
    foldLeft(Set.empty[String])(_ union _).toList.
    sortWith(_ < _)

  def getCommits: List[Commit] = git commits

  def getTicketsAsJson: String = getTickets

  def getTagsAsJson: String = getTags

  def getCommitsAsJson: String = getCommits

  def switchToBranch(branch: String)(emptyBranch: Boolean) = {
    val branchCommand =
      if (git hasBranch (branch))
        git switchToBranch (_)
      else {
        if (emptyBranch)
          git createEmptyBranch (_)
        else
          git createBranch (_)
      }

    branchCommand(branch)
  }

  def switchToGitstickBranch = switchToBranch(Repository defaultBranch)(true)

  def saveUsers(commitMessage: String)(user: User): Unit = {
    UserList save ((usersFile getAbsolutePath), userList)
    git addFile (usersFile.getName)
    git commitAs (commitMessage, user.name, user.email, true)
  }

  def checkAndLoadUserList: Unit = {
    // load user list; create if necessary
    if (usersFile exists) {
      userList = UserList load (usersFile getAbsolutePath)
    } else {
      userList = UserList()
    }

    if (!(userList hasUser (userName))) {
      userList setUser (userName, "", userEmail) // add default user with default password
      saveUsers("added '" + userName + "' to .users")(User(userName, "", userEmail))
    }
  }
}

object Repository {
  lazy val defaultPath = (System.getProperty("java.io.tmpdir") + (FileHelper separator) + ".gitstick")
  lazy val defaultBranch = "gitstick"

  def checkRepository(path: String): Boolean = {
    var valid = false
    val file = new File(path)
    valid |= (GitHelper isGitRepository (path))
    valid |= !(file exists)
    valid |= ((file exists) && (file isDirectory) && (file.listFiles.length == 0))

    valid
  }

  def apply(workingDir: String = defaultPath): Repository = {
    val repo = new Repository(workingDir)
    val git = repo.git
    repo userName = (repo fileRepository).getConfig() get (UserConfig KEY) getAuthorName ()
    repo userEmail = (repo fileRepository).getConfig() get (UserConfig KEY) getAuthorEmail ()

    val gitDir = new File(workingDir)
    val isNewRepository = ((!(gitDir exists)) || (FileHelper isEmpty (gitDir)))
    if (isNewRepository) {
      FileHelper emptyDirectory (gitDir)

      git.init
      repo fileRepository = new FileRepository(new File(gitDir, ".git"))
      git addFile ("", ".hold", "hold")
      git commit ("initialized ticket repository")
    }

    repo.switchToGitstickBranch
    repo.checkAndLoadUserList
    repo
  }
}
