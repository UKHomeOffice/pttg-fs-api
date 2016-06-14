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

  @RequestMapping(value = Array("/incomeproving/v1/individual/dailybalancecheck/{sortCode}/{accountNumber}"),
    method = Array(RequestMethod.GET),
    produces = Array(MediaType.APPLICATION_JSON_VALUE))
  def dailyBalanceCheck(@PathVariable(value = "sortCode") sortCode: String,
                        @PathVariable(value = "accountNumber") accountNumber: String,
                        @RequestParam(value = "threshold") threshold: JBigDecimal,
                        @RequestParam(value = "applicationRaisedDate") applicationRaisedDate: String,
                        @RequestParam(value = "days") days: Int) = {

    val LOGGER: Logger = LoggerFactory.getLogger(classOf[DailyBalanceService])
    LOGGER.info("dailybalancecheck request received")

    val bankAccount = Account(sortCode.replace("-",""), accountNumber)
    val accountStatusChecker = new AccountStatusChecker(barclaysBankService)
    val dailyAccountBalanceCheck = accountStatusChecker.checkDailyBalancesAreAboveThreshold(bankAccount, LocalDate.parse(applicationRaisedDate, DateTimeFormatter.ofPattern("yyyy-M-d")), days, BigDecimal(threshold))

    new ResponseEntity(AccountDailyBalanceCheckResponse(bankAccount, dailyAccountBalanceCheck, StatusResponse("200", "OK")),HttpStatus.OK)

  }
}
