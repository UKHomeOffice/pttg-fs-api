package uk.gov.digital.ho.proving.financialstatus.acl

import java.time.LocalDate

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.stereotype.Service
import uk.gov.digital.ho.proving.financialstatus.client.HttpUtils
import uk.gov.digital.ho.proving.financialstatus.domain.{Account, AccountDailyBalance, AccountDailyBalances}

@Service()
class MockBankService @Autowired()(val objectMapper: ObjectMapper, httpUtils: HttpUtils, @Value("${barclays.stub.service}") val bankService: String) extends BankService {

  val bankName = "MockBarclays"

  //todo hardcoded scheme will need to be externalised
  val bankUrl = s"$bankService/financialstatus/v1"

  val LOGGER: Logger = LoggerFactory.getLogger(classOf[MockBankService])

  private def bankResponseMapper(bankResponse: BankResponse): AccountDailyBalances =
    AccountDailyBalances(bankResponse.dailyBalances.accountHolderName, bankResponse.dailyBalances.sortCode,
      bankResponse.dailyBalances.accountNumber,
      bankResponse.dailyBalances.balanceRecords.map {
        balance => AccountDailyBalance(balance.date, balance.balance)
      }
    )

  override def buildUrl(account: Account, fromDate: LocalDate, toDate: LocalDate, dob: LocalDate, userId: String, accountHolderConsent: Boolean): String =
    s"""$bankUrl/${account.sortCode}/${account.accountNumber}/balances?fromDate=$fromDate&toDate=$toDate&dob=$dob&userId=$userId&accountHolderConsent=$accountHolderConsent"""

  override def fetchAccountDailyBalances(account: Account, fromDate: LocalDate, toDate: LocalDate,
                                         dob: LocalDate, userId: String, accountHolderConsent: Boolean): AccountDailyBalances = {

    val url = buildUrl(account, fromDate, toDate, dob, userId, accountHolderConsent)
    LOGGER.info(s"call URL: $url")

    val httpResponse = httpUtils.performRequest(url)
    val bankResponse = objectMapper.readValue(httpResponse.body, classOf[DailyBalances])
    bankResponseMapper(BankResponse(httpResponse.httpStatus, bankResponse))

  }

}
