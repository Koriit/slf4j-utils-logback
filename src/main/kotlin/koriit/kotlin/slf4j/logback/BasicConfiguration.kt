package koriit.kotlin.slf4j.logback

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.PatternLayout
import ch.qos.logback.classic.jul.LevelChangePropagator
import ch.qos.logback.classic.spi.Configurator
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.encoder.LayoutWrappingEncoder
import ch.qos.logback.core.spi.ContextAwareBase
import net.logstash.logback.encoder.LogstashEncoder
import org.slf4j.bridge.SLF4JBridgeHandler

/**
 * Overrides default configuration provided by Logback.
 * Changes the pattern to be more useful and pleasant to the human eye. Also configures j.u.l. bridge.
 *
 * This configuration can still be overridden by any `logback-test.xml`, `logback.groovy` and `logback.xml` on the classpath.
 *
 * You can change ROOT Logger level with `logging.root.level` system property or `LOGGING_ROOT_LEVEL` environment variable.
 * Default is INFO. Possible values are defined in [Level].
 *
 * It is also possible to change to JSON format by providing `logging.format=json` system property or
 * `LOGGING_FORMAT=json` environment variable. By directly logging with JSON documents we can avoid CPU intensive
 * and costly parsing of log strings. The JSON format is achieved with [LogstashEncoder] which is adjusted for ELK, however,
 * any NoSQL/JSON Storage should be able to receive such logs.
 */
class BasicConfiguration : ContextAwareBase(), Configurator {
    override fun configure(lc: LoggerContext) {
        addInfo("Setting up basic configuration.")

        // Install j.u.l. bridge
        SLF4JBridgeHandler.removeHandlersForRootLogger()
        SLF4JBridgeHandler.install()

        // Add LevelChangePropagator to improve j.u.l. logging
        val propagator = LevelChangePropagator()
        propagator.context = lc
        propagator.setResetJUL(true)
        propagator.start()
        lc.addListener(propagator)

        // Configuration
        val loggingFormat = System.getProperty("logging.format") ?: System.getenv("LOGGING_FORMAT") ?: "default"
        val loggingLevel = System.getProperty("logging.root.level") ?: System.getenv("LOGGING_ROOT_LEVEL") ?: "INFO"

        // Root appender
        val appender = ConsoleAppender<ILoggingEvent>()
        appender.context = lc
        appender.name = "console"
        appender.encoder = when (loggingFormat.lowercase()) {
            "json" -> {
                LogstashEncoder().apply {
                    isIncludeMdc = true
                    start()
                }
            }
            else -> {
                LayoutWrappingEncoder<ILoggingEvent>().apply {
                    context = lc
                    layout = PatternLayout().apply {
                        pattern = "%date{yyyy-MM-dd HH:mm:ss.SSS} %highlight(%-5level) [%-15.15thread] %cyan(%-60(\\(%file:%line\\))) : %message %magenta(%mdc{correlationId} %mdc{subCorrelationId}) %n%xException"
                        context = lc
                        start()
                    }
                    start()
                }
            }
        }
        appender.start()

        val rootLogger: Logger = lc.getLogger(Logger.ROOT_LOGGER_NAME)
        rootLogger.level = Level.toLevel(loggingLevel, Level.INFO)
        rootLogger.addAppender(appender)
    }
}
