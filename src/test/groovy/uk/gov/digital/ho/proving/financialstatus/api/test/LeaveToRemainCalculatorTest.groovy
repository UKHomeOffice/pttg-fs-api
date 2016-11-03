package uk.gov.digital.ho.proving.financialstatus.api.test

import spock.lang.Specification
import uk.gov.digital.ho.proving.financialstatus.domain.LeaveToRemainCalculator

import java.time.LocalDate

import static uk.gov.digital.ho.proving.financialstatus.api.test.TestUtils.*

class LeaveToRemainCalculatorTest extends Specification {

    def "Calculate leave to remain from course given dates"() {

        expect:
        LeaveToRemainCalculator.calculateLeaveToRemain(DataUtils.buildScalaOption(courseStartDate), DataUtils.buildScalaOption(courseEndDate),
            DataUtils.buildScalaOption(continuationEndDate), nonDoctorateLeaveToRemainBoundary, nonDoctorateShortLeaveToRemain, nonDoctorateLongLeaveToRemain
        ).get() == leaveToRemain

        where:
        courseStartDate           | courseEndDate             | continuationEndDate        || leaveToRemain
        LocalDate.of(2000, 1, 1)  | LocalDate.of(2000, 4, 2)  | LocalDate.of(2000, 5, 2)   || 3
        LocalDate.of(2000, 1, 1)  | LocalDate.of(2000, 5, 31) | LocalDate.of(2000, 6, 30)  || 3
        LocalDate.of(2000, 1, 1)  | LocalDate.of(2000, 6, 1)  | LocalDate.of(2000, 7, 1)   || 3
        LocalDate.of(2000, 1, 1)  | LocalDate.of(2000, 7, 14) | LocalDate.of(2000, 11, 14) || 6
        LocalDate.of(2000, 1, 2)  | LocalDate.of(2000, 8, 11) | LocalDate.of(2000, 12, 11) || 8
        LocalDate.of(2000, 2, 2)  | LocalDate.of(2000, 9, 11) | LocalDate.of(2001, 5, 11)  || 12
        LocalDate.of(2000, 1, 11) | LocalDate.of(2001, 4, 5)  | LocalDate.of(2001, 9, 2)   || 9

    }

}
