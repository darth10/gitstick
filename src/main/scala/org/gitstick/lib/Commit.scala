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

import org.eclipse.jgit.revwalk.RevCommit
import org.gitstick.json.GeneratorForJson
import org.gitstick.json.JsonCommit

// TODO test pending
case class Commit(revCommit: RevCommit)
  extends GeneratorForJson {
  lazy val message = revCommit getFullMessage
  lazy val time = UnixTime fromUnixTime (revCommit getCommitTime)
  lazy val authorName = revCommit.getAuthorIdent.getName
  lazy val authorEmail = revCommit.getAuthorIdent.getEmailAddress

  override def toString = getJson

  def getGenerator = JsonCommit(this)
}

object Commit {
  implicit def revCommitToCommit(revCommit: RevCommit) = Commit(revCommit)

  implicit def revCommitListToCommitList(revCommits: List[RevCommit]) = revCommits map (Commit(_))
}
