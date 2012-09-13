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

import java.util.Date
import java.io.File
import net.liftweb.json.JsonAST.JValue
import org.gitstick.util.FileHelper
import org.gitstick.json.GenericJsonGenerator
import org.gitstick.json.JsonComment
import org.gitstick.json.GeneratorForJson
import org.gitstick.json.ParserForJson

class Comment(
  val text: String,
  val user: String,
  val time: Date)
  extends GeneratorForJson {

  override def toString = getJson

  def getGenerator = JsonComment(this)
}

object Comment extends ParserForJson[Comment] {
  def apply(text: String, user: String, date: Date = new Date()): Comment = new Comment(text, user, date)

  def apply(file: File): Comment = {
    require(file.exists)

    val tokens = file.getName split ("_")
    val commentText = FileHelper readFromFile (file.getAbsolutePath)
    val commentTime = UnixTime fromUnixTime (tokens apply (1) toLong)

    new Comment(
      commentText,
      tokens.apply(2),
      commentTime)
  }

  def getJsonParser(jValue: JValue) = jValue.extract[JsonComment]
}
