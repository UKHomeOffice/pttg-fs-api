package uk.gov.digital.ho.proving.financialstatus.api.test

import spock.lang.Specification
import uk.gov.digital.ho.proving.financialstatus.domain.CourseLengthCalculator
import uk.gov.digital.ho.proving.financialstatus.domain.MaintenanceThresholdCalculator

import java.time.LocalDate

import static uk.gov.digital.ho.proving.financialstatus.api.test.TestUtils.*

class CourseLengthCalculatorTest extends Specification {

    def "Calculate course length from given dates"() {

        expect:
        CourseLengthCalculator.calculateCourseLength(courseStartDate, courseEndDate, DataUtils.buildScalaOption(courseExtensionEndDate), capValue) == courseLength

        where:
        courseStartDate          | courseEndDate              | courseExtensionEndDate     | capValue || courseLength
        LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 1, 2)   | LocalDate.of(2000, 4, 2)   | 9        || 3
        LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 1, 31)  | LocalDate.of(2000, 5, 31)  | 9        || 4
        LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1)   | LocalDate.of(2000, 6, 1)   | 9        || 4
        LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 10)  | LocalDate.of(2000, 7, 14)  | 9        || 6
        LocalDate.of(2000, 1, 2) | LocalDate.of(2000, 2, 11)  | LocalDate.of(2000, 8, 11)  | 9        || 6
        LocalDate.of(2000, 2, 2) | LocalDate.of(2000, 2, 11)  | LocalDate.of(2000, 12, 12) | 0        || 11
        LocalDate.of(2000, 2, 2) | LocalDate.of(2000, 12, 11) | null                       | 9        || 9
        LocalDate.of(2000, 2, 2) | LocalDate.of(2001, 5, 11)  | null                       | 9        || 9

    }

}
