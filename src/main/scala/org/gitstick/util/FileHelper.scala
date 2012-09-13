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

import java.io.File
import java.io.PrintWriter
import reflect.Manifest
import io.Source._

/** Helper for performing common file operations. */
object FileHelper {
  lazy val separator = File.separator

  /**
   * Writes to a file with a supplied write action.
   *
   * @param file File to write to
   * @param write Function that performs the write action
   */
  def printToFile(file: File)(write: PrintWriter => Unit): Unit = {
    val writer = new PrintWriter(file)
    try {
      write(writer)
    } finally {
      writer.close
    }
  }

  /**
   * Writes to a file with a supplied write action.
   *
   * @param file File path to write to
   * @param write Function that performs the write action
   */
  def printToFile(file: String)(write: PrintWriter => Unit): Unit = printToFile(new File(file))(write)

  /**
   * Writes specified text to a file.
   *
   * @param file File or file path to write to
   * @param text A line or a sequence of lines to write
   */
  def printToFile(file: AnyRef, text: AnyRef): Unit = {
    lazy val write: (PrintWriter) => Unit = text match {
      case list: Iterable[_] => (writer: PrintWriter) => (list foreach (l => writer.println(l toString)))
      case array: Array[_] => (writer: PrintWriter) => printToFile(file, array.toList)
      case string: String => (writer: PrintWriter) => writer.println(string)
    }

    file match {
      case file: File => printToFile(file)(write)
      case filepath: String => printToFile(new File(filepath))(write)
    }
  }

  /**
   * Reads all the text in a file.
   *
   * @param filepath File path to read
   * @return All text in the file
   */
  def readFromFile(filepath: String): String = {
    val source = fromFile(filepath)
    val text = source getLines () mkString ("\n")
    source.close

    text
  }

  /**
   * Empties a given directory. If the directory does not exist, the directory is created.
   *
   * @param filepath Directory path
   */
  def emptyDirectory(filepath: String): Unit = emptyDirectory(new File(filepath))

  /**
   * Empties a given directory. If the directory does not exist, the directory is created.
   *
   * @param file Directory file
   */
  def emptyDirectory(file: File): Unit = {
    def deleteFiles(dfile: File): Unit = {
      if (dfile.isDirectory)
        dfile.listFiles foreach { f => deleteFiles(f) }
      if (!(file.getAbsolutePath equals dfile.getAbsolutePath))
        dfile.delete
    }

    if (file.exists && !file.isDirectory) {
      file.delete
    }

    if (!file.exists)
      file.mkdirs
    else
      deleteFiles(file)
  }

  /**
   * Checks if a directory exists and is empty.
   *
   * @param dir Directory file
   * @return True if the directory exists and is empty
   */
  def isEmpty(dir: File): Boolean = (dir isDirectory) && (dir.listFiles.length == 0)

  /**
   * Deletes a file
   *
   * @param filepath File path to delete
   * @return True if the file was found
   */
  def deleteFile(filepath: String): Boolean = {
    val file = new File(filepath)
    if (file exists)
      file delete
    else
      false
  }
}
