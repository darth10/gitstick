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
import java.util.Date
import java.text.SimpleDateFormat
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.Level
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy
import ch.qos.logback.core.rolling.TimeBasedFileNamingAndTriggeringPolicy
import ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP
import ch.qos.logback.core.rolling.RollingFileAppender

/**
 * Trait to abstract application log.
 *
 * Any class/object/trait can mixin this trait for logging.
 */
trait Logging {
  private lazy val loggerContext = LoggerFactory.getILoggerFactory.asInstanceOf[LoggerContext]
  private lazy val loggerPattern = "[%d{HH:mm:ss.SSS} %5level ] %msg%n" // log pattern format for log generated with --log option
  private lazy val logFileName = "gitstick"
  private lazy val rootLoggerName = Logger ROOT_LOGGER_NAME

  /** Application log. */
  lazy val log = loggerContext getLogger (rootLoggerName)

  /** Sets log level to DEBUG. */
  def setDebug = log setLevel (Level DEBUG)

  /**
   * Gets file path of log.
   *
   *  @param logFilePath Path of log directory
   *  @return Log file name and directory
   */
  def getLogFilePath(logFilePath: String) = {
    val logDir = new File(logFilePath)
    val dateFormat = new SimpleDateFormat("-yyyy.MM.dd-HH.mm")

    if (logDir exists) {
      if (logDir isFile)
        logDir delete
    } else
      FileHelper emptyDirectory (logDir)

    (new File(logDir, logFileName + (dateFormat format (new Date())) + "-head.log"), logDir)
  }

  /**
   * Sets output file of application log.
   *
   * @param logFilePath Path of log directory
   */
  def setLogFile(logFilePath: String) = {
    val (logFile, logDir) = getLogFilePath(logFilePath)

    val logEncoder = new PatternLayoutEncoder {
      setContext(loggerContext)
      setPattern(loggerPattern)
      start
    }

    // trigger log rolling every 1 minute, or is the file size exceeds 500KB
    val triggeringPolicy = new SizeAndTimeBasedFNATP[ILoggingEvent] {
      setContext(loggerContext)
      setMaxFileSize("500KB")
    }

    val rollingPolicy = new TimeBasedRollingPolicy[ILoggingEvent] {
      setContext(loggerContext)
      setFileNamePattern((logDir getAbsolutePath) + File.separator + logFileName + "-%d{yyyy.MM.dd-HH.mm}-%i.log")
      setTimeBasedFileNamingAndTriggeringPolicy(triggeringPolicy)
    }

    val appender = new RollingFileAppender[ILoggingEvent] {
      setContext(loggerContext)
      setName(rootLoggerName)
      setFile(logFile getAbsolutePath)
      setAppend(true)
      setEncoder(logEncoder)
      setRollingPolicy(rollingPolicy)
      setTriggeringPolicy(triggeringPolicy)
    }

    triggeringPolicy setTimeBasedRollingPolicy (rollingPolicy)
    rollingPolicy setParent (appender)
    rollingPolicy start ()
    appender start ()

    log addAppender (appender)
  }
}
