import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.filter.ThresholdFilter

import static ch.qos.logback.classic.Level.*

// Add a status listener to record the state of the logback configuration when the logging system is initialised.
statusListener(OnConsoleStatusListener)

appender("STDOUT", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
    }
}

appender("FILE", FileAppender) {
    file = "financial-status-service-api.log"
    append = true
    encoder(PatternLayoutEncoder) {
        pattern = "%d{HH:mm:ss.SSS} %-4relative [%thread] %-5level %logger{35} - %msg%n"
    }
    filter(ThresholdFilter) {
        level = DEBUG
    }
}

// Define logging levels for specific packages
logger("org.springframework", INFO)

logger("org.mongodb.driver", WARN)

logger("org.apache.http", WARN) // DEBUG level will show the repeated healthcheck calls


root(INFO, ["STDOUT"])

// Check config file every 30 seconds and reload if changed
scan("30 seconds")
