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

import org.scalatra.ScalatraKernel
import org.scalatra.auth.ScentryStrategy
import org.scalatra.auth.strategy.BasicAuthStrategy
import org.gitstick.lib.User
import org.gitstick.util.AppState

class GitstickAuthStrategy(app: ScalatraKernel, realm: String, remoteAddr: String)
  extends BasicAuthStrategy[User](app, realm) {
  lazy val userList = AppState.repo.userList

  protected def getUserId(user: User) = user name

  protected def validate(username: String, password: String): Option[User] = {
    var authenticatedUser: Option[User] = None

    if (username.length > 0) {
      authenticatedUser =
        if (AppState secureMode)
          userList authenticate (username, password)
        else Some(
          userList getUser (username) getOrElse (User(username, "", username + "@" + remoteAddr)))
    }

    authenticatedUser
  }
}
