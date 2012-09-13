package org.gitstick.util

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
import net.iharder.Base64

/** Helper for generating SHA1 hashes and encoding/decoding Base64. */
final object CryptHelper {
  val sha1 = MessageDigest.getInstance("SHA1")

  /**
   * Encodes a string to Base64 encoding.
   *
   * @param s String to encode
   * @return Base64 representation of string
   */
  def encode(s: String) = Base64 encodeBytes (s toCharArray () map (_ toByte))

  /**
   * Decodes a string from Base64 encoding.
   *
   * @param s String to encode
   * @return String decoded from Base64
   */
  def decode(s: String) = new String(Base64 decode (s))

  /**
   * Converts a byte array to hexadecimal representation.
   *
   * @param bytes Bytes to convert
   * @return Hexadecimal representation of bytes
   */
  def bytesToHex(bytes: Array[Byte]) = bytes map (b => "%02x" format (java.lang.Byte.valueOf(b))) mkString ("")

  /**
   * Generates SHA1 hash of a string.
   *
   * @param s String to hash
   * @return SHA1 hash of string
   */
  def hashOf(s: String) = bytesToHex(sha1 digest (s getBytes))
}
