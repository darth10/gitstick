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

import net.liftweb.json.Printer._
import net.liftweb.json.JsonAST._
import org.gitstick.util.FileHelper
import org.gitstick.util.CryptHelper
import org.gitstick.json.GeneratorForJson
import org.gitstick.json.ParserForJson
import org.gitstick.json.JsonUserList

// TODO test pending
class UserList(var users: List[User] = List.empty[User])
  extends GeneratorForJson {
  import CryptHelper._
  import UserList._
  lazy val emptyPassword = hashOf("")

  override def toString = getJson

  def getGenerator = JsonUserList(this)

  override def renderJson(jValue: JValue) = pretty(render(jValue))

  def getUsers(username: String) = users filter (_.name equalsIgnoreCase (username))

  def hasUser = getUsers(_: String).length > 0

  def getUser = getUsers(_: String) headOption

  def removeUser(username: String): Unit =
    users = users filterNot (_.name equalsIgnoreCase (username))

  def authenticate(username: String, password: Option[String]): Option[User] =
    if (password isDefined)
      authenticate(username, password get)
    else
      None

  def authenticate(username: String, password: String): Option[User] =
    getUser(username) map (_ authenticate (password)) getOrElse (None)

  def setUser(username: String, password: String = "", email: String = ""): Unit = {
    require(username.length != 0)
    val user = getUser(username)
    val isNewUser = user isEmpty
    val salt = User.getSalt

    removeUser(username)

    //set password to default if blank, else use hash of password
    val isEmptyPassword = (password.length == 0)
    val saltedPassword = salt + password
    val saltedUsername = salt + username

    val userPasswordHash = if (isNewUser) {
      if (isEmptyPassword) hashOf(saltedUsername) else hashOf(saltedPassword)
    } else {
      if (isEmptyPassword) user.get.password else hashOf(saltedPassword)
    }

    val isEmptyEmail = (email.length == 0)
    val userEmail = if (isNewUser) email else {
      if (isEmptyEmail) user.get.email else email
    }

    val userSalt = if (isNewUser) salt else {
      if (isEmptyPassword) user.get.salt else salt
    }

    val newUser = User(username, userPasswordHash, userEmail, userSalt)
    users = newUser :: users
  }

  def setJsonUser(jsonUser: String): Unit = {
    val user = User parseJson (jsonUser)

    removeUser(user name)

    //set password to default if blank, else use in verbatim
    val isEmptyPassword = (user.password.length == 0)
    val saltedDefaultPassword = user.salt + user.name
    val newUser = if (isEmptyPassword) user setPassword (hashOf(saltedDefaultPassword)) else user

    users = newUser :: users
  }

  def authenticateAndSetJsonUser(
    username: String,
    password: Option[String],
    jsonUser: String) =
    authenticate(username, password) map (user => setJsonUser(jsonUser))
}

object UserList extends ParserForJson[UserList] {
  def apply(users: List[User]) = new UserList(users)

  def apply() = new UserList(List.empty[User])

  def getJsonParser(jValue: JValue) = jValue.extract[JsonUserList]

  def load(filepath: String): UserList = parseJson(FileHelper readFromFile (filepath))

  def save(filepath: String, users: UserList): Unit = FileHelper printToFile (filepath, users getJson)
}
