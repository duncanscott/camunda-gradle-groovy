

import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender
import java.nio.charset.Charset
import static ch.qos.logback.classic.Level.DEBUG
import static ch.qos.logback.classic.Level.INFO

String defaultPattern = "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{5} - %msg%n"

appender("STDOUT", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = defaultPattern
    }
}

appender("ROLLLER", RollingFileAppender) {
  file =  "${System.getProperty('user.home')}/logs/data-generation.log"
  rollingPolicy(FixedWindowRollingPolicy) {
    fileNamePattern = "data-generation.%i.log"
    minIndex = 1
    maxIndex = 9
  }
  triggeringPolicy(SizeBasedTriggeringPolicy) {
    maxFileSize = "10MB"
  }
  encoder(PatternLayoutEncoder) {
    pattern = defaultPattern
  }
}

root INFO, ['ROLLLER']