package org.gitstick.tests.util

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

import java.security.MessageDigest
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.Specification
import net.iharder.Base64
import org.gitstick.tests.common.Randomness
import org.gitstick.util.CryptHelper

@RunWith(classOf[JUnitRunner])
class CryptHelperSpec extends Specification with Randomness {
  val sha1 = MessageDigest.getInstance("SHA1")

  def is = "CryptHelper" ^
    "should encode to Base64" ! encodeToB64 ^
    "should decode from Base64" ! decodeToB64 ^
    "should generate SHA1 hash" ! sha1hash ^
    end

  lazy val stringToEncode = getRandomName

  def encodeToB64 = {
    val actualString = CryptHelper encode (stringToEncode)
    val expectedString = Base64.encodeBytes(stringToEncode toCharArray () map (_ toByte))

    actualString equals expectedString
  }

  def decodeToB64 = {
    val encodedString = Base64.encodeBytes(stringToEncode toCharArray () map (_ toByte))
    val actualString = CryptHelper decode (encodedString)
    val expectedString = new String(Base64.decode(encodedString))

    actualString equals expectedString
  }

  def sha1hash = {
    val actualString = CryptHelper hashOf (stringToEncode)
    val expectedString = sha1 digest (stringToEncode getBytes) map (b => "%02x" format (java.lang.Byte.valueOf(b))) mkString ("")

    actualString equals expectedString
  }
}
