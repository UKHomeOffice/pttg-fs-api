package uk.gov.digital.ho.proving.financialstatus.domain

import spock.lang.Specification
import uk.gov.digital.ho.proving.financialstatus.acl.MockBankService
import uk.gov.digital.ho.proving.financialstatus.api.test.DataUtils

import java.time.LocalDate

class AccountStatusCheckerTest extends Specification {
    def date_2016_6_9 = LocalDate.of(2016, 6, 9)
    def date_2016_6_10 = LocalDate.of(2016, 6, 10)
    def date_2016_6_11 = LocalDate.of(2016, 6, 11)
    def bigdecimal_1 = new scala.math.BigDecimal(1)
    def barlcaysBankService = Mock(MockBankService)

    def "GetMinimumBreachDate"() {

        given:
        def minimum = new scala.math.BigDecimal(2560.23)
        def lessThanMinimum = new scala.math.BigDecimal(2000)
        def adbList = [new AccountDailyBalance(date_2016_6_9, minimum), new AccountDailyBalance(date_2016_6_10, lessThanMinimum), new AccountDailyBalance(date_2016_6_11, lessThanMinimum)] as ArrayList

        def accountDailyBalances = new AccountDailyBalances(DataUtils.convertArrayListToScalaList(adbList))

        def checker = new AccountStatusChecker(barlcaysBankService,28)
        when:
        def adb = checker.getFirstBalanceToFail(accountDailyBalances, minimum)

        then:
        adb.get().date() == date_2016_6_10
    }

    def "GetMinimumBreachAmount"() {

        given:
        def minimum = new scala.math.BigDecimal(2560.23)
        def lessThanMinimum = new scala.math.BigDecimal(2000)
        def adbList = [new AccountDailyBalance(date_2016_6_9, minimum), new AccountDailyBalance(date_2016_6_10, lessThanMinimum), new AccountDailyBalance(date_2016_6_11, lessThanMinimum)] as ArrayList
        def accountDailyBalances = new AccountDailyBalances(DataUtils.convertArrayListToScalaList(adbList))

        def checker = new AccountStatusChecker(barlcaysBankService,28)
        when:
        def adb = checker.getFirstBalanceToFail(accountDailyBalances, minimum)

        then:
        adb.get().balance() == lessThanMinimum
    }
}
