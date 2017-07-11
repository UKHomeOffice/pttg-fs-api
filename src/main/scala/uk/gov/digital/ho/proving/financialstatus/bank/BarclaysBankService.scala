package uk.gov.digital.ho.proving.financialstatus.bank

import java.time.format.DateTimeFormatter._
import java.time.{LocalDate, LocalDateTime}

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.stereotype.Component
import uk.gov.digital.ho.proving.financialstatus.bank.BarclaysBankService.{CONSENT, DAILY_BALANCE}
import uk.gov.digital.ho.proving.financialstatus.client.HttpUtils
import uk.gov.digital.ho.proving.financialstatus.domain.{Account, UserConsent}

@Component
class BarclaysBankService @Autowired()(val objectMapper: ObjectMapper,
                                       httpUtils: HttpUtils,
                                       @Value("${barclays.consent.resource}") val consentResource: String,
                                       @Value("${barclays.balance.resource}") val balanceResource: String) {

  def checkUserConsent(account: Account,
                       dob: LocalDate,
                       userId: String): UserConsent = {

    val formattedDob = dob.format(ISO_LOCAL_DATE)
    val url = bankConsentUrl(account, formattedDob)
    val httpResponse = httpUtils.performRequest(url, userId, generateRequestId(CONSENT))

    objectMapper.readValue(httpResponse.body, classOf[UserConsent])
  }

  def bankConsentUrl(account: Account, dob: String): String =
    s"""$consentResource/${account.sortCode}${account.accountNumber}/consent?dateOfBirth=$dob"""

  def fetchAccountDailyBalances(account: Account,
                                fromDate: LocalDate,
                                toDate: LocalDate,
                                dob: LocalDate,
                                userId: String): DailyBalances = {

    val url = buildBalanceUrl(account, fromDate, toDate, dob)
    val httpResponse = httpUtils.performRequest(url, userId, generateRequestId(DAILY_BALANCE))

    objectMapper.readValue(httpResponse.body, classOf[DailyBalances])
  }

  def buildBalanceUrl(account: Account, fromDate: LocalDate, toDate: LocalDate, dob: LocalDate): String =
    s"""$balanceResource/${account.sortCode}${account.accountNumber}/balances?fromDate=$fromDate&toDate=$toDate&dateOfBirth=$dob"""

  // TODO: make this generate a sequentially unique id
  def generateRequestId(prefix: String): String = prefix + LocalDateTime.now().format(ISO_LOCAL_DATE_TIME)

}

object BarclaysBankService {
  val CONSENT = "HO_CONSENT"
  val DAILY_BALANCE = "HO_DAILY_BALANCE"
}
