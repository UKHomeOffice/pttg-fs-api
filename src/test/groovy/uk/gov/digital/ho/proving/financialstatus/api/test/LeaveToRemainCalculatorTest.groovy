package uk.gov.digital.ho.proving.financialstatus.api.test

import spock.lang.Specification
import uk.gov.digital.ho.proving.financialstatus.domain.LeaveToRemainCalculator

import java.time.LocalDate

import static uk.gov.digital.ho.proving.financialstatus.api.test.TestUtils.*

class LeaveToRemainCalculatorTest extends Specification {

    def "Calculate leave to remain from course given dates"() {

        expect:
        LeaveToRemainCalculator.calculateLeaveToRemain(DataUtils.buildScalaOption(courseStartDate), DataUtils.buildScalaOption(courseEndDate),
            DataUtils.buildScalaOption(courseExtensionEndDate), nonDoctorateLeaveToRemainBoundary, nonDoctorateShortLeaveToRemain, nonDoctorateLongLeaveToRemain
        ).get() == leaveToRemain

        where:
        courseStartDate          | courseEndDate              | courseExtensionEndDate     || leaveToRemain
        LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 4, 2)   | LocalDate.of(2000, 5, 2)   || 7
        LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 5, 31)  | LocalDate.of(2000, 6, 30)  || 8
        LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 6, 1)   | LocalDate.of(2000, 7, 1)   || 9
        LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 7, 14)  | LocalDate.of(2000, 11, 14) || 13
        LocalDate.of(2000, 1, 2) | LocalDate.of(2000, 8, 11)  | LocalDate.of(2000, 12, 11) || 16
        LocalDate.of(2000, 2, 2) | LocalDate.of(2000, 9, 11)  | LocalDate.of(2001, 5, 11)  || 20
        LocalDate.of(2000, 2, 2) | LocalDate.of(2000, 12, 11) | LocalDate.of(2001, 1, 11)  || 16

    }

}
