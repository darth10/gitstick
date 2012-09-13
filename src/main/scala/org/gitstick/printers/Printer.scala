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

import org.gitstick.util.Logging

// TODO test pending

trait Printer
  extends Logging {

  val pipe = "|"
  val plus = "+"
  val dash = "-"
  val more = ".."

  lazy val infinity = Int.MaxValue

  def getColumnLengths: List[Int]

  def getHeaders: List[String]

  def getHeaderAt = getHeaders apply (_: Int)

  def getColumnLengthAt = getColumnLengths apply (_: Int)

  def getLongestWithLimitAtColumn(limit: Int)(index: Int)(list: List[String]) = math.min(
    getLongestString(getHeaderAt(index) :: list) length,
    math.max(
      limit,
      getHeaderAt(index) length))

  def getTextWithLimit(limit: Int)(text: String) =
    if (text.length > limit)
      text.substring(0, limit - more.length) + more
    else
      text

  def getLongestString = (_: List[String]) sortWith (_.length > _.length) head

  def getLongestAtColumn = getLongestWithLimitAtColumn(infinity)_

  def getLineFormat = pipe + (getColumnLengths map (" %-" + _ + "s ") mkString (pipe)) + pipe

  def getSeparator = plus + (getColumnLengths map (n => dash * (n + 2)) mkString (plus)) + plus

  def printHeader = println(getLineFormat format (getHeaders.toSeq: _*))

  def printSeparator = println(getSeparator)

  def printData[T](data: List[T])(printer: (T, Int) => Unit) {
    if (data.length > 0) {
      printSeparator
      printHeader
      printSeparator

      var index = 0
      data map { item =>
        printer(item, index)
        index += 1
      }

      printSeparator
    }
  }
}
