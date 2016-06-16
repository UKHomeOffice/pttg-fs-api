package uk.gov.digital.ho.proving.financialstatus.api

import java.math.{BigDecimal => JBigDecimal}
import java.time.LocalDate
import java.time.format.DateTimeFormatter

import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.{HttpStatus, MediaType, ResponseEntity}
import org.springframework.web.bind.annotation._
import uk.gov.digital.ho.proving.financialstatus.acl.BarclaysBankService
import uk.gov.digital.ho.proving.financialstatus.domain.{Account, AccountStatusChecker}

@RestController
class DailyBalanceService @Autowired()(barclaysBankService: BarclaysBankService) {

  // TODO get spring to handle LocalDate objects

  @RequestMapping(value = Array("/pttg/financialstatusservice/v1/accounts/{sortCode}/{accountNumber}/dailybalancestatus"),
    method = Array(RequestMethod.GET),
    produces = Array(MediaType.APPLICATION_JSON_VALUE))
  def dailyBalanceStatus(@PathVariable(value = "sortCode") sortCode: String,
                         @PathVariable(value = "accountNumber") accountNumber: String,
                         @RequestParam(value = "minimum") minimum: JBigDecimal,
                         @RequestParam(value = "toDate") toDate: String,
                         @RequestParam(value = "fromDate") fromDate: String) = {

    val LOGGER: Logger = LoggerFactory.getLogger(classOf[DailyBalanceService])
    LOGGER.info("dailybalancecheck request received")

    val bankAccount = Account(sortCode.replace("-", ""), accountNumber)
    val accountStatusChecker = new AccountStatusChecker(barclaysBankService)
    val dailyAccountBalanceCheck = accountStatusChecker.checkDailyBalancesAreAboveMinimum(
      bankAccount, LocalDate.parse(fromDate, DateTimeFormatter.ofPattern("yyyy-M-d")), LocalDate.parse(toDate, DateTimeFormatter.ofPattern("yyyy-M-d")), BigDecimal(minimum).setScale(2, BigDecimal.RoundingMode.HALF_UP)
    )

    new ResponseEntity(AccountDailyBalanceStatusResponse(bankAccount, dailyAccountBalanceCheck, StatusResponse("200", "OK")), HttpStatus.OK)

  }
}
