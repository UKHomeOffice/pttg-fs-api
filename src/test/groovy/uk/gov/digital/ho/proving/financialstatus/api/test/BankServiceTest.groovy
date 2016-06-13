package uk.gov.digital.ho.proving.financialstatus.api.test

import spock.lang.Specification
import uk.gov.digital.ho.proving.financialstatus.acl.BarclaysBankService
import uk.gov.digital.ho.proving.financialstatus.domain.Account
import uk.gov.digital.ho.proving.financialstatus.domain.AccountStatusChecker

import java.time.LocalDate

class BankServiceTest extends Specification {

    def barlcaysBankService = Mock(BarclaysBankService)
    def accountStatusChecker = new AccountStatusChecker(barlcaysBankService)

    def "Check bankService returns a pass for correct data"() {

        given:
        1 * barlcaysBankService.fetchAccountDailyBalances(_, _, _) >> DataUtils.randomBankResponseOK(LocalDate.of(2016, 6, 9), 28, 2560.23, 3500, true, false)

        when:
        def account = new Account("12-34-56", "87654321")
        def threshold = new scala.math.BigDecimal(2560.23)
        def response = accountStatusChecker.checkDailyBalancesAreAboveThreshold(account, LocalDate.of(2016, 6, 9), 28, threshold)

        then:
        response.minimumAboveThreshold()
        response.applicationRaisedDate().equals(LocalDate.of(2016, 6, 9))
        response.assessmentStartDate().equals(LocalDate.of(2016, 5, 12))
        response.threshold() == threshold

    }

    def "Check bankService returns a failure for incorrect data"() {

        given:
        1 * barlcaysBankService.fetchAccountDailyBalances(_, _, _) >> DataUtils.randomBankResponseOK(LocalDate.of(2016, 6, 9), 28, 2060.23, 3500, true, false)

        when:
        def account = new Account("12-34-56", "87654321")
        def threshold = new scala.math.BigDecimal(2560.23)
        def response = accountStatusChecker.checkDailyBalancesAreAboveThreshold(account, LocalDate.of(2016, 6, 9), 28, threshold)

        then:
        !response.minimumAboveThreshold()
        response.applicationRaisedDate().equals(LocalDate.of(2016, 6, 9))
        response.assessmentStartDate().equals(LocalDate.of(2016, 5, 12))
        response.threshold() == threshold

    }

    def "Check bankService returns a failure for nonconsecutive date"() {

        given:
        1 * barlcaysBankService.fetchAccountDailyBalances(_, _, _) >> DataUtils.randomBankResponseNonConsecutiveDates(LocalDate.of(2016, 6, 9), 28, 2060.23, 3500, true, false)

        when:
        def account = new Account("12-34-56", "87654321")
        def threshold = new scala.math.BigDecimal(2560.23)
        def response = accountStatusChecker.checkDailyBalancesAreAboveThreshold(account, LocalDate.of(2016, 6, 9), 28, threshold)

        then:
        !response.minimumAboveThreshold()
        response.applicationRaisedDate().equals(LocalDate.of(2016, 6, 9))
        response.assessmentStartDate().equals(LocalDate.of(2016, 5, 12))
        response.threshold() == threshold

    }

    def "Check bankService returns a failure for not enough data"() {

        given:
        1 * barlcaysBankService.fetchAccountDailyBalances(_, _, _) >> DataUtils.randomBankResponseOK(LocalDate.of(2016, 6, 9), 27, 2060.23, 3500, true, false)

        when:
        def account = new Account("12-34-56", "87654321")
        def threshold = new scala.math.BigDecimal(2560.23)
        def response = accountStatusChecker.checkDailyBalancesAreAboveThreshold(account, LocalDate.of(2016, 6, 9), 28, threshold)

        then:
        !response.minimumAboveThreshold()
        response.applicationRaisedDate().equals(LocalDate.of(2016, 6, 9))
        response.assessmentStartDate().equals(LocalDate.of(2016, 5, 12))
        response.threshold() == threshold

    }



}
