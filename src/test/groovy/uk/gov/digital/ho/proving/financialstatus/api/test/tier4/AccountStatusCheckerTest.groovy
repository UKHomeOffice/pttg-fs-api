package uk.gov.digital.ho.proving.financialstatus.api.test.tier4

import spock.lang.Specification
import uk.gov.digital.ho.proving.financialstatus.api.test.DataUtils
import uk.gov.digital.ho.proving.financialstatus.bank.BarclaysBankService
import uk.gov.digital.ho.proving.financialstatus.domain.Account
import uk.gov.digital.ho.proving.financialstatus.domain.AccountStatusChecker

import java.time.LocalDate

class AccountStatusCheckerTest extends Specification {

    def mockBankService = Mock(BarclaysBankService)
    def accountStatusChecker = new AccountStatusChecker(mockBankService, 28)

    def "Check bankService returns a pass for correct data"() {

        given:
        def account = new Account("12-34-56", "12345678")
        def minimum = new scala.math.BigDecimal(2560.23)
        def toDate = LocalDate.of(2016, 6, 9)
        def fromDate = toDate.minusDays(27)
        def dob = LocalDate.of(2000, 1, 1)
        def userId = "user123456"
        def accountHolderConsent = true

        1 * mockBankService.fetchAccountDailyBalances(_, _, _, _, _) >> DataUtils.generateRandomBankResponseOK(fromDate, toDate, 2560.23, 3500, true, false)

        when:
        def response = accountStatusChecker.checkDailyBalancesAreAboveMinimum(account, fromDate, toDate, minimum, dob, userId).get()

        then:
        response.pass()
        response.toDate().equals(LocalDate.of(2016, 6, 9))
        response.fromDate().equals(LocalDate.of(2016, 5, 13))
        response.minimum() == minimum

    }

    def "Check bankService returns a failure for incorrect data"() {

        given:
        def account = new Account("12-34-56", "12345678")
        def minimum = new scala.math.BigDecimal(2560.23)
        def toDate = LocalDate.of(2016, 6, 9)
        def fromDate = toDate.minusDays(27)
        def dob = LocalDate.of(2000, 1, 1)
        def userId = "user123456"
        def accountHolderConsent = true

        1 * mockBankService.fetchAccountDailyBalances(_, _, _, _, _) >> DataUtils.generateRandomBankResponseOK(fromDate, toDate, 2060.23, 3500, true, false)

        when:
        def response = accountStatusChecker.checkDailyBalancesAreAboveMinimum(account, fromDate, toDate, minimum, dob, userId).get()

        then:
        !response.pass()
        response.toDate().equals(LocalDate.of(2016, 6, 9))
        response.fromDate().equals(LocalDate.of(2016, 5, 13))
        response.minimum() == minimum

    }

    def "Check bankService returns a failure for nonconsecutive date"() {

        given:
        def account = new Account("12-34-56", "12345678")
        def minimum = new scala.math.BigDecimal(2560.23)
        def toDate = LocalDate.of(2016, 6, 9)
        def fromDate = toDate.minusDays(27)
        def dob = LocalDate.of(2000, 1, 1)
        def userId = "user123456"
        def accountHolderConsent = true

        1 * mockBankService.fetchAccountDailyBalances(_, _, _, _, _) >> DataUtils.generateRandomBankResponseNonConsecutiveDates(fromDate, toDate, 2060.23, 3500, true, false)

        when:
        def response = accountStatusChecker.checkDailyBalancesAreAboveMinimum(account, fromDate, toDate, minimum, dob, userId).get()

        then:
        !response.pass()
        response.toDate().equals(LocalDate.of(2016, 6, 9))
        response.fromDate().equals(LocalDate.of(2016, 5, 13))
        response.minimum() == minimum

    }

    def "Check bankService returns a failure for not enough data"() {

        given:
        def account = new Account("12-34-56", "12345678")
        def minimum = new scala.math.BigDecimal(2560.23)
        def toDate = LocalDate.of(2016, 6, 9)
        def fromDate = toDate.minusDays(27)
        def dob = LocalDate.of(2000, 1, 1)
        def userId = "user123456"
        def accountHolderConsent = true

        1 * mockBankService.fetchAccountDailyBalances(_, _, _, _, _) >> DataUtils.generateRandomBankResponseOK(fromDate, toDate, 2060.23, 3500, true, false)

        when:
        def response = accountStatusChecker.checkDailyBalancesAreAboveMinimum(account, fromDate, toDate, minimum, dob, userId).get()

        then:
        !response.pass()
        response.toDate().equals(LocalDate.of(2016, 6, 9))
        response.fromDate().equals(LocalDate.of(2016, 5, 13))
        response.minimum() == minimum

    }


}
