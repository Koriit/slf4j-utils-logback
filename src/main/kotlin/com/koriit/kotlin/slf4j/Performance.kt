// Logging functions are inlined to remove extra entry on the stack
@file:Suppress("NOTHING_TO_INLINE")

package com.koriit.kotlin.slf4j

import net.logstash.logback.argument.StructuredArguments.value
import org.slf4j.Logger
import org.slf4j.Marker
import org.slf4j.MarkerFactory

/**
 * SLF4J marker for performance logs.
 */
val PERFORMANCE: Marker = MarkerFactory.getMarker("PERFORMANCE")

/**
 * Is the logger instance enabled for the INFO level with PERFORMANCE marker?
 *
 * @return True if this Logger is enabled for the INFO with PERFORMANCE marker level, false otherwise.
 */
inline fun Logger.isPerformanceEnabled() = isInfoEnabled(PERFORMANCE)

/**
 * Log a message at the INFO level with PERFORMANCE marker.
 *
 * @param msg the message string to be logged
 * @param duration the duration to log in ms
 */
inline fun Logger.performance(msg: String, duration: Long) = info(PERFORMANCE, msg, value("duration", duration))

/**
 * Log a message at the INFO level with PERFORMANCE marker according to the specified format and argument.
 *
 * This form avoids superfluous object creation when the logger is disabled for the INFO level with PERFORMANCE marker.
 *
 * @param format the format string
 * @param duration the duration to log in ms
 * @param arg he argument
 */
inline fun Logger.performance(format: String, duration: Long, arg: Any?) = info(PERFORMANCE, format, value("duration", duration), arg)

/**
 * Log a message at the INFO level with PERFORMANCE marker according to the specified format and arguments.
 *
 * This form avoids superfluous string concatenation when the logger
 * is disabled for the INFO level with PERFORMANCE marker. However, this variant incurs the hidden
 * (and relatively small) cost of creating an `Object[]` before invoking the method,
 * even if this logger is disabled for INFO with PERFORMANCE marker. The variants taking
 * [one][Logger.performance] and [two][Logger.performance]
 * arguments exist solely in order to avoid this hidden cost.
 *
 * @param format the format string
 * @param duration the duration to log in ms
 * @param arguments a list of 3 or more arguments
 */
inline fun Logger.performance(format: String, duration: Long, vararg arguments: Any?) = info(PERFORMANCE, format, value("duration", duration), *arguments)
