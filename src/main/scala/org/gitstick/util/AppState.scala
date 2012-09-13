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
import org.gitstick.lib.Repository

object AppState
  extends Logging {
  val userPrompt = "> "
  var repo: Repository = null
  var secureMode: SecureMode = null
  var isWar = false

  def setRepository(path: String): java.io.File = {
    val invalidMesg = "Not a valid git repository or empty directory: "
    val changesMesg = "Repository has uncommitted changes"
    val repoPath = new File(path)
    val repoAbsPath = (repoPath getAbsolutePath)

    if (!(Repository.checkRepository(repoAbsPath))) {
      log error (invalidMesg + (repoAbsPath))
      sys exit (1)
    }

    // FIXME the username is never set! so commits don't have an author/email 
    GitHelper(repoAbsPath) map { git =>
      if (!git.isWorkTreeClean) {
        if (isWar) {
          log error (changesMesg)
          sys exit (1)
        } else {
          // prompt for commit
          log warn ("%s. Do you want to commit the changes and continue? (Y/n)" format (changesMesg))
          val commit = readLine(userPrompt)
          if (commit equals "Y") {
            var commitMesg = ""
            while (commitMesg.length == 0) {
              log info ("Enter commit message")
              commitMesg = readLine(userPrompt)
              if (commitMesg.length == 0) {
                log error ("Invalid commit message")
              }
            }

            git commitAll (commitMesg)
            log info ("All changes commited")
          } else {
            sys exit (0)
          }
        }
      }
    }

    repo = Repository(path)
    log info ("Repository path: " + (AppState.repo.fileRepository.getWorkTree.getAbsolutePath))
    repoPath
  }

  def setSecureMode(secure: Boolean): Unit = {
    secureMode = SecureMode(secure)

    if (secure) {
      log info ("Secure mode")
    } else {
      log warn ("Open mode")
    }
  }

  def setBranch(branch: String, toEmptyBranch: Boolean) = {
    log info ("Switching to branch: " + branch)
    repo.switchToBranch(branch)(toEmptyBranch)
  }
}
