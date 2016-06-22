package uk.gov.digital.ho.proving.financialstatus.api.test

import spock.lang.Specification
import uk.gov.digital.ho.proving.financialstatus.domain.MaintenanceThresholdCalculator

class MaintenanceThresholdCalculatorTest extends Specification {

    def "Check threshold for inner London, 2 months and 10000 tuition fees"() {
        // (1265 * 2) + 10000 = 12530
        given:
        def innerLondon = true
        def courseLengthInMonths = 2
        def tuitionFees = new scala.math.BigDecimal(10000)

        when:
        def response = MaintenanceThresholdCalculator.calculate(innerLondon, courseLengthInMonths, tuitionFees)

        then:
        response == 12530
    }

    def "Check threshold for non inner London, 2 months and 10000 tuition fees"() {
        // (1015 * 2) + 10000 = 12030
        given:
        def innerLondon = false
        def courseLengthInMonths = 2
        def tuitionFees = new scala.math.BigDecimal(10000)

        when:
        def response = MaintenanceThresholdCalculator.calculate(innerLondon, courseLengthInMonths, tuitionFees)

        then:
        response == 12030
    }

    def "Check threshold for inner London, 6 months and 8500 tuition fees"() {
        // (1265 * 6) + 8500 = 16090
        given:
        def innerLondon = true
        def courseLengthInMonths = 6
        def tuitionFees = new scala.math.BigDecimal(8500)

        when:
        def response = MaintenanceThresholdCalculator.calculate(innerLondon, courseLengthInMonths, tuitionFees)

        then:
        response == 16090
    }

    def "Check threshold for non inner London, 6 months and 8500 tuition fees"() {
        // (1015 * 6) + 8500 = 14590
        given:
        def innerLondon = false
        def courseLengthInMonths = 6
        def tuitionFees = new scala.math.BigDecimal(8500)

        when:
        def response = MaintenanceThresholdCalculator.calculate(innerLondon, courseLengthInMonths, tuitionFees)

        then:
        response == 14590
    }

    def "Check threshold for inner London, 9 months and 1234 tuition fees"() {
        // (1265 * 9) + 1234 = 12619
        given:
        def innerLondon = true
        def courseLengthInMonths = 9
        def tuitionFees = new scala.math.BigDecimal(1234)

        when:
        def response = MaintenanceThresholdCalculator.calculate(innerLondon, courseLengthInMonths, tuitionFees)

        then:
        response == 12619
    }

    def "Check threshold for non inner London, 9 months and 1234 tuition fees"() {
        // (1015 * 9) + 1234 = 10369
        given:
        def innerLondon = false
        def courseLengthInMonths = 9
        def tuitionFees = new scala.math.BigDecimal(1234)

        when:
        def response = MaintenanceThresholdCalculator.calculate(innerLondon, courseLengthInMonths, tuitionFees)

        then:
        response == 10369
    }



}
