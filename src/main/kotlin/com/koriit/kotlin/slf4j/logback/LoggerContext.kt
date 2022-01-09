package com.koriit.kotlin.slf4j.logback

import ch.qos.logback.classic.LoggerContext
import org.slf4j.LoggerFactory

/**
 * Finish logging threads and flush buffers.
 */
fun closeLoggers() {
    (LoggerFactory.getILoggerFactory() as LoggerContext).stop()
}
