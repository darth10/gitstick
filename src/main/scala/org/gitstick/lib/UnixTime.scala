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
import java.util.Date._

/** Helper to translate to and from Unix representation i.e. number of milliseconds since Epoch. */
final object UnixTime {

  /**
   * Gets the current time in Unix representation.
   *
   *  @return Current time as number of milliseconds since Epoch
   */
  def getUnixTime: Long = (System.currentTimeMillis() / 1000L) toInt

  /**
   * Converts time to Unix representation.
   *
   *  @param date Time to convert
   *  @return Unix representation of given time
   */
  def toUnixTime(date: Date): Long = (date.getTime() / 1000L) toInt

  /**
   * Converts from Unix representation.
   *
   * @param unixTime Time as number of milliseconds since Epoch
   * @return Standard representation of given time
   */
  def fromUnixTime(unixTime: Long): Date = new Date(unixTime * 1000)
}
