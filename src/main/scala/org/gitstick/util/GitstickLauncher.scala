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

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.webapp.WebAppContext
import org.gitstick.web.GitstickServlet
import org.eclipse.jetty.servlet.ServletHolder

object GitstickLauncher extends Logging {
  def main(args: Array[String]) = {
    try {
      val commandHelper = new CommandHelper
      commandHelper parseArgs (args)

      val port = commandHelper portNo
      val server = new Server(port)
      val gitstickWebContextPath = getClass.getClassLoader.getResource("webapp").toExternalForm
      val webContext = new WebAppContext(gitstickWebContextPath, "/")
      server setHandler (webContext)
      server start

      log info ("Started server on port " + port)
      server join ()
    } catch {
      case e: Exception => {
        log error (e getMessage)
        log debug ((e getStackTrace) mkString ("\n"))
        sys exit (1)
      }
    }
  }
}
