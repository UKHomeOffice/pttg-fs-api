package uk.gov.digital.ho.proving.financialstatus.api

import java.math.MathContext
import java.time.LocalDate

import org.slf4j.{Logger, LoggerFactory}
import org.springframework.http.{HttpStatus, MediaType, ResponseEntity}
import org.springframework.web.bind.annotation.{RequestMapping, RequestMethod, RestController}


@RestController
class TransactionService {

  @RequestMapping(value = Array("/incomeproving/v1/individual/transactions"),
    method = Array(RequestMethod.GET),
    produces = Array(MediaType.APPLICATION_JSON_VALUE)
  )
  def transactions = {
    val LOGGER: Logger = LoggerFactory.getLogger(classOf[TransactionService])
    LOGGER.info("Ping received")
    new ResponseEntity[TransactionsResponse](
      TransactionsResponse(Account("12-34-56", "878787878"), AccountTransaction(LocalDate.now(), "Description",
        BigDecimal(-100.00, MathContext.DECIMAL64),
        BigDecimal(2323.00, MathContext.DECIMAL64)
      ), new ResponseStatus("200", "OK")),
      HttpStatus.OK
    )
  }
}

