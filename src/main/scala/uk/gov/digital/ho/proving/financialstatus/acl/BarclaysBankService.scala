package uk.gov.digital.ho.proving.financialstatus.acl

import java.time.LocalDate

import org.springframework.stereotype.Service
import uk.gov.digital.ho.proving.financialstatus.domain.{Account, AccountDailyBalance, AccountDailyBalanceCheck}

@Service()
class BarclaysBankService extends BankService {

  def fetchAccountDailyBalances(account: Account, endDate: LocalDate, startDate: LocalDate) = {

    lazy val balances: Stream[AccountDailyBalance] = AccountDailyBalance(endDate, BigDecimal(scala.util.Random.nextInt(500) + 2600)) #::
      balances.map { dailyBalance => AccountDailyBalance(dailyBalance.date.minusDays(1), BigDecimal(scala.util.Random.nextInt(500) + 2600)) }

    val applicableBalances = balances.takeWhile(balance => (!balance.date.isBefore(startDate) && !balance.date.isAfter(endDate))).toVector
    applicableBalances
  }

}
