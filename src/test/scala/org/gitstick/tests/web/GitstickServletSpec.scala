package org.gitstick.tests.web

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

import org.junit.runner.RunWith
import scala.Array.canBuildFrom
import org.specs2.runner.JUnitRunner
import org.scalatra.test.specs2.ScalatraSpec
import net.iharder.Base64
import org.gitstick.web.GitstickServlet
import org.gitstick.util.AppState

@RunWith(classOf[JUnitRunner])
class GitstickServletSpec extends ScalatraSpec {
  def is =
    "GET on GitstickServlet" ^
      "'/' should prompt for authorization" ! root401 ^
      "'/tickets.index' prompt for authorization" ! tickets401 ^
      "'/log.index' prompt for authorization" ! log401 ^
      "'/settings.index' prompt for authorization" ! settings401 ^
      "'/' (authorized) should redirect" ! root302 ^
      "'/tickets.index' (authorized) should return success" ! tickets200 ^
      "'/log.index' (authorized) should return success" ! log200 ^
      "'/settings.index' (authorized) should return success" ! settings200 ^
      end

  AppState setSecureMode (false)
  val basicCredentials = "Basic " + Base64.encodeBytes("sbt:sbt" toCharArray () map (_ toByte))
  val authHeader = Map("Authorization" -> basicCredentials)
  addServlet(classOf[GitstickServlet], "/*")

  def root401 = get("/") {
    status must_== 401
  }

  def tickets401 = get("/tickets.index") {
    status must_== 401
  }

  def log401 = get("/log.index") {
    status must_== 401
  }

  def settings401 = get("/settings.index") {
    status must_== 401
  }

  def root302 = get("/", Seq.empty, authHeader) {
    status must_== 302
  }

  def tickets200 = get("/tickets.index", Seq.empty, authHeader) {
    status must_== 200
  }

  def log200 = get("/log.index", Seq.empty, authHeader) {
    status must_== 200
  }

  def settings200 = get("/settings.index", Seq.empty, authHeader) {
    status must_== 200
  }
}
