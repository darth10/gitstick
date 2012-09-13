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

import org.gitstick.lib.User
import org.gitstick.lib.UserList

case class UserPrinter(users: List[User])
  extends Printer {
  final val passwordMaxLength = 10

  def getColumnLengths = List(
    getLongestAtColumn(0)(users map (_ name)),
    getLongestAtColumn(1)(users map (_ email)),
    getLongestWithLimitAtColumn(passwordMaxLength)(2)(users map (_ password)))

  def getHeaders = List(
    "User",
    "Email",
    "Password")

  def print: Unit = {
    printData(users) { (user, index) =>
      println(getLineFormat format (
        user name,
        user email,
        getTextWithLimit(getColumnLengthAt(2))(user password)))
    }

    log info (users.size + " user(s)")
  }
}

object UserPrinter {
  def apply(userList: UserList): UserPrinter = UserPrinter(userList users)
}
