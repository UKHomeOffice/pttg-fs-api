package uk.gov.digital.ho.proving.financialstatus.health

import org.springframework.boot.actuate.endpoint.AbstractEndpoint
import org.springframework.stereotype.Component

/**
  * Endpoint that can be used for liveness checks, but not for Readiness.
  */
@Component
class LivenessEndpoint extends AbstractEndpoint[String]("ping") {
  override def invoke(): String = "pong"
}
