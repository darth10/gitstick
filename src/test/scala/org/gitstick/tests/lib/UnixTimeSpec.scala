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

import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.Specification
import org.gitstick.lib.UnixTime

@RunWith(classOf[JUnitRunner])
class UnixTimeSpec extends Specification {
  def is =
    "UnixTime" ^
      "should know Epoch" ! epochKnown ^
      "should convert to and from java.Util.Date" ! converts ^
      "should be 10 digits long" ! tenDigitsLong ^
      end

  val dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
  dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"))

  def epochKnown = (dateFormatGmt.format(UnixTime fromUnixTime (0))) equals "1970-01-01 00:00:00"
  def converts = (UnixTime getUnixTime) equals (UnixTime toUnixTime (new Date()))
  def tenDigitsLong = ((UnixTime getUnixTime) toString).length() == 10
}
