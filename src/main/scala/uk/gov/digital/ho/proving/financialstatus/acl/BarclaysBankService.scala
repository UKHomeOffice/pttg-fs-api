package uk.gov.digital.ho.proving.financialstatus.acl

import java.time.LocalDate

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import uk.gov.digital.ho.proving.financialstatus.client.HttpUtils
import uk.gov.digital.ho.proving.financialstatus.domain.{Account, AccountDailyBalance, AccountDailyBalances}

@Service()
class BarclaysBankService @Autowired()(val objectMapper: ObjectMapper) extends BankService {

  val bankName = "Barclays"
  val bankUrl = "http://localhost:8082/financialstatus/v1"

  private def bankResponseMapper(bankResponse: BankResponse): AccountDailyBalances =
    AccountDailyBalances(
      bankResponse.dailyBalances.balanceRecords.map {
        balance => AccountDailyBalance(balance.date, balance.balance)
      }
    )

  def buildUrl(account: Account, fromDate: LocalDate, toDate: LocalDate) =
    s"""$bankUrl/${account.sortCode}/${account.accountNumber}/balances?fromDate=$fromDate&toDate=$toDate"""

  def fetchAccountDailyBalances(account: Account, fromDate: LocalDate, toDate: LocalDate) = {

    val url = buildUrl(account, fromDate, toDate)
    val httpResponse = HttpUtils.performRequest(url)
    val bankResponse = objectMapper.readValue(httpResponse.body, classOf[DailyBalances])
    bankResponseMapper(BankResponse(httpResponse.httpStatus, bankResponse))

  }

}
