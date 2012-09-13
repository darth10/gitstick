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

import java.io.File
import java.util.regex.Pattern
import org.eclipse.jgit.api._
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.util.SystemReader
import org.eclipse.jgit.storage.file.FileRepository
import collection.JavaConversions._
import org.gitstick.lib.Repository

class GitHelper(var repo: FileRepository,
  var rootDir: String = "",
  var userName: String = "",
  var userEmail: String = "") {
  require(repo != null)

  lazy val git: Git = new Git(repo)
  lazy val commits: List[RevCommit] = (git log () call ()).toList

  def getFilePattern(path: String): String = {
    var pattern = path
    if (rootDir.length > 0) {
      if (pattern.startsWith(rootDir))
        pattern = pattern.substring(rootDir length)
    }
    if (pattern.startsWith(File.separator))
      pattern = pattern.substring(1)

    pattern.replaceAll(Pattern.quote(File.separator) + "$", "").replaceAll(Pattern.quote(File.separator), "/")
  }

  def init: FileRepository = {
    val init = new InitCommand()

    val gitInit = init setDirectory (repo getWorkTree) setBare (false) call ()
    new FileRepository(gitInit getRepository () getWorkTree ())
  }

  def isWorkTreeClean: Boolean = {
    var isClean = true

    val status = git status () call ()
    if ((status.getModified.size != 0) || (status.getChanged.size != 0)) {
      isClean = false
    }
    isClean
  }

  def commitAs(message: String, authorName: String, authorEmail: String, all: Boolean = false): Unit = {
    git commit () setAll (all) setMessage (message) setAuthor (authorName, authorEmail) call ()
  }

  def commit = commitAs(_: String, userName, userEmail)

  def commitAll = commitAs(_: String, userName, userEmail, true)

  def addFile(filename: String, text: String = ""): String = addFile("", filename, text)

  def addFile(dir: String, filename: String, text: String): String = {
    var path = rootDir + File.separator + filename
    if (dir.length > 0)
      path = rootDir + File.separator + dir + File.separator + filename

    val pattern = getFilePattern(path)
    if (text.length > 0)
      createFile(path, text)

    git add () addFilepattern (pattern) call ()
    path
  }

  def removeFile(filename: String): String = removeFile("", filename)

  def removeFile(dir: String, filename: String): String = {
    var path = rootDir + File.separator + filename
    if (dir.length > 0)
      path = rootDir + File.separator + dir + File.separator + filename

    val pattern = getFilePattern(path)
    git rm () addFilepattern (pattern) call ()
    path
  }

  def getBranches: List[String] = {
    git branchList () call () map (_ getName () replaceAll ("^refs/heads/", "")) toList
  }

  def hasBranch(branch: String): Boolean = {
    var hasBranch = false
    var branches = getBranches
    if ((branches filter (_ equals branch)).size > 0)
      hasBranch = true

    hasBranch
  }

  def createBranch(branch: String): Unit = {
    git checkout () setCreateBranch (true) setName (branch) call ()
  }

  def createEmptyBranch(branch: String): Unit = {
    var repoDir = new File(rootDir)
    def deleteFiles(dfile: File): Unit = {
      if (!(dfile getName () startsWith (".git"))) {
        if (dfile.isDirectory)
          dfile.listFiles foreach { f => deleteFiles(f) }
        if (!(repoDir.getAbsolutePath equals dfile.getAbsolutePath))
          dfile.delete
      }
    }

    createBranch(branch)
    deleteFiles(repoDir)
    commitAll("Emptied branch '" + branch + "'")
  }

  def switchToBranch(branch: String): Unit = {
    git checkout () setName (branch) call ()
  }

  private def createFile(filename: String, text: String): Unit = {
    var fullpath = filename
    var file = new File(fullpath)

    //make absolute
    if (file.getAbsolutePath() equals fullpath)
      fullpath = repo.getDirectory.getParent + File.separator + fullpath

    if (file.isDirectory)
      file.delete

    FileHelper.printToFile(file, text)
  }
}

object GitHelper {
  lazy val anonUserName = "anon"
  lazy val anonUserEmail = anonUserName + "@" + SystemReader.getInstance().getHostname

  def apply(repo: Repository): GitHelper = {
    require(repo.fileRepository != null)

    new GitHelper(
      repo.fileRepository,
      repo.workingDir,
      repo.userName,
      repo.userEmail)
  }

  def apply(repoPath: String): Option[GitHelper] = {
    require(repoPath.length != 0)
    var gitDir = new File(repoPath)

    if (!gitDir.getName.equals(".git"))
      gitDir = new File(repoPath, ".git")

    var repo = new FileRepository(gitDir getAbsolutePath)
    var gitHelper = if (repo.getWorkTree != null) {
      Some(new GitHelper(repo))
    } else None

    gitHelper
  }

  def isGitRepository(path: String): Boolean = {
    var isValidRepo = false
    var gitDir = new File(path, ".git")

    if ((gitDir exists) && (gitDir isDirectory)) {
      try {
        var repo = new FileRepository(gitDir getAbsolutePath)
        if (repo.getWorkTree != null) {
          isValidRepo = true
        }
      } catch {
        case e: Exception => isValidRepo = false
      }
    }

    isValidRepo
  }
}
