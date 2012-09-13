package org.gitstick.tests.lib

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
import org.specs2.runner.JUnitRunner
import org.specs2.Specification
import org.gitstick.lib.Repository
import org.gitstick.tests.common._

@RunWith(classOf[JUnitRunner])
class RepositorySpec extends Specification with TestRepository {
  def is =
    "Repository" ^
      "should create a new repository" ! pending ^
      "should load an existing repository" ! pending ^
      "should have default users" ! pending ^
      "should use gitstick branch by default" ! pending ^
      "should create branches" ! pending ^
      "should know commits" ! pending ^
      "should know tags" ! pending ^
      "should know tickets" ! pending ^
      "should fetch a ticket" ! pending ^
      end
}
