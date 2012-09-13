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

import java.io.File
import java.io.PrintWriter
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.Specification
import org.gitstick.util.FileHelper
import org.gitstick.tests.common.Randomness

@RunWith(classOf[JUnitRunner])
class FileHelperSpec extends Specification with Randomness {
  def is =
    "FileHelper" ^
      "should read text from files" ! readText ^
      "should write text to files (curry)" ! writeText(true) ^
      "should write lines to files (curry)" ! writeLines(true, false) ^
      "should write text to files" ! writeText(false) ^
      "should write list of lines to files" ! writeLines(false, false) ^
      "should write array of lines to files" ! writeLines(false, true) ^
      "should create an empty directory" ! createEmptyDir(false) ^
      "should create an empty directory (overwrite file)" ! createEmptyDir(true) ^
      "should empty a directory file" ! emptyDir(false) ^
      "should empty a directory path" ! emptyDir(true) ^
      "should delete a file that exists" ! deleteFile(true) ^
      end

  def readText = {
    val expectedText = getRandomName
    val testFile = getRandomFile
    val testFilePath = testFile getAbsolutePath
    val testFileWriter = new PrintWriter(testFile)

    testFileWriter println (expectedText)
    testFileWriter close ()

    try {
      val actualText = FileHelper readFromFile (testFilePath)
      actualText equals expectedText
    } catch {
      case e: Exception => throw e
    } finally if (testFile exists) testFile delete
  }

  def writeText(curry: Boolean) = {
    val expectedText = getRandomName
    val testFile = getRandomFile
    val testFilePath = testFile getAbsolutePath

    try {
      if (curry)
        FileHelper.printToFile(testFilePath)((w: PrintWriter) => w.println(expectedText))
      else
        FileHelper.printToFile(testFilePath, expectedText)

      val actualText = FileHelper readFromFile (testFilePath)
      actualText equals expectedText
    } catch {
      case e: Exception => throw e
    } finally if (testFile exists) testFile delete
  }

  def writeLines(curry: Boolean, useArray: Boolean) = {
    val testLines = getRandomLines(10)
    val testFile = getRandomFile
    val testFilePath = testFile getAbsolutePath

    try {
      if (curry)
        FileHelper.printToFile(testFilePath)(w => (testLines.foreach(w.println)))
      else {
        if (useArray)
          FileHelper.printToFile(testFilePath, testLines.toArray)
        else
          FileHelper.printToFile(testFilePath, testLines)
      }

      val expectedText = testLines mkString ("\n")
      val actualText = FileHelper readFromFile (testFilePath)
      actualText equals expectedText
    } catch {
      case e: Exception => throw e
    } finally if (testFile exists) testFile delete
  }

  def createEmptyDir(overwriteFile: Boolean) = {
    val testDir = getRandomFile
    val testText = getRandomName

    if (overwriteFile) {
      FileHelper printToFile (testDir, testText)
    }

    try {
      FileHelper emptyDirectory (testDir)
      testDir.exists && testDir.isDirectory
    } catch {
      case e: Exception => throw e
    } finally if (testDir exists) testDir delete
  }

  def emptyDir(useString: Boolean) = {
    val testParentDir = getRandomFile
    val testDir = new File(testParentDir, getRandomName)
    testDir.mkdirs

    val testFile = new File(testDir, getRandomName)
    FileHelper printToFile (testFile, getRandomName)

    try {
      if (useString)
        FileHelper emptyDirectory (testParentDir.getAbsolutePath)
      else
        FileHelper emptyDirectory (testParentDir)

      testParentDir.exists && (testParentDir.listFiles.length == 0)
    } catch {
      case e: Exception => throw e
    } finally if (testParentDir exists) testParentDir delete
  }

  def deleteFile(createFile: Boolean) = {
    val testFile = getRandomFile
    val testText = getRandomName

    if (createFile) FileHelper printToFile (testFile, testText)

    try {
      FileHelper deleteFile (testFile getAbsolutePath)
    } catch {
      case e: Exception => throw e
    } finally if (testFile exists) testFile delete
  }
}
