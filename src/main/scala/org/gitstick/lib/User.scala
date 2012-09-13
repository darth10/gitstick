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

import javax.servlet.http.HttpServletRequest
import net.liftweb.json.JValue
import org.gitstick.util.CryptHelper
import org.gitstick.json.GeneratorForJson
import org.gitstick.json.ParserForJson
import org.gitstick.json.JsonUser

// TODO test pending
case class User(
  val name: String,
  val password: String = "",
  val email: String = "",
  val salt: String = User.getSalt)
  extends GeneratorForJson {

  override def toString = getJson

  def getGenerator = JsonUser(this)

  def authenticate(password: String) = if (hasPassword(password)) Some(this) else None

  def hasPassword(passwordToCheck: String) = ((CryptHelper hashOf (salt + passwordToCheck)) == password)

  def setPassword(password: String) = User(name, password, email, salt)

  def setEmail(email: String) = User(name, password, email, salt)
}

object User extends ParserForJson[User] {
  def getJsonParser(jValue: JValue) = jValue.extract[JsonUser]

  def getSalt = UnixTime.getUnixTime toString
}
