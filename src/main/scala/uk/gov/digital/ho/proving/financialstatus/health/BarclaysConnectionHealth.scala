package uk.gov.digital.ho.proving.financialstatus.health

import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.boot.actuate.health.{Health, HealthIndicator}
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

import scala.util.{Failure, Success, Try}

/**
  * HealthIndicator contributing to the healthcheck endpoint that is used for readiness checks.
  */
@Component
class BarclaysConnectionHealth @Autowired()(rest: RestTemplate,
                                            @Value("${barclays.service.url}") val bankService: String) extends HealthIndicator {

  val LOGGER: Logger = LoggerFactory.getLogger(classOf[BarclaysConnectionHealth])

  // todo Update for real Barclays API
  val bankHealthUrl = s"$bankService/financialstatus/v1/01061600030000/balances?dateOfBirth=1975-10-10&toDate=2016-06-01&fromDate=2016-05-10"

  override def health(): Health = {

    val response = Try {
      rest.getForEntity(bankHealthUrl, classOf[String])
    }

    // todo check for failure status codes
    response match {
      case Success(response) => Health.up.withDetail("The Barclays API is responding:", "OK").build
      case Failure(exception) => Health.down.withDetail("While trying to read Barclays API, received :", exception.getMessage).build
    }
  }
}
