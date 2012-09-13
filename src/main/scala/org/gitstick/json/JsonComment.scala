package org.gitstick.json

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

import java.util.Date
import net.liftweb.json.JsonDSL._
import net.liftweb.json.JsonAST.JValue
import org.gitstick.lib.Comment

case class JsonComment(comment: String, user: String, time: String)
  extends ToJson
  with FromJson[Comment] {
  import JsonComment._

  def fromJson = Comment(
    comment,
    user,
    (if (time.length > 0) dateFormat.parse(time) else new Date))

  def toJson = (
    ("comment" -> comment) ~
    ("user" -> user) ~
    ("time" -> time))
}

object JsonComment extends DateFormatterForJson {
  def apply(comment: Comment): JsonComment = new JsonComment(comment.text, comment.user, dateFormat.format(comment.time))
}
