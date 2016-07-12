package uk.gov.digital.ho.proving.financialstatus.monitor

import java.util.Date

import org.slf4j.Logger

trait Monitor {

  val LOGGER: Logger

}

trait Auditor extends Monitor {

  final def audit[T](comment: String)(functionToAudit: => T): T = {
    val result = functionToAudit
    LOGGER.info(s"AUDIT: $comment => ${result.toString}")
    result
  }

}

trait Timer extends Monitor {

  final def timer[T](comment: String)(functionToTime: => T): T = {
    val start = new Date()
    LOGGER.debug(s"TIMER: Start @ ${start.getTime} $comment")
    val result = functionToTime
    val end = new Date()
    LOGGER.debug(s"TIMER: End @ ${end.getTime}, time (ms) = ${end.getTime - start.getTime} $comment")
    result
  }

}
