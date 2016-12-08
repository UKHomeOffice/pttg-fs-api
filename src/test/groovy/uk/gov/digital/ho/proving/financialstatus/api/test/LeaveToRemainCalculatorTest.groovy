package uk.gov.digital.ho.proving.financialstatus.api.test

import spock.lang.Specification
import uk.gov.digital.ho.proving.financialstatus.domain.LeaveToRemainCalculator

import java.time.LocalDate

import static uk.gov.digital.ho.proving.financialstatus.api.test.TestUtils.*

class LeaveToRemainCalculatorTest extends Specification {

    def "Calculate leave to remain from course given dates"() {

        expect:
        LeaveToRemainCalculator.calculateLeaveToRemain(DataUtils.buildScalaOption(courseStartDate), DataUtils.buildScalaOption(courseEndDate),
            DataUtils.buildScalaOption(originalCourseStartDate), nonDoctorateLeaveToRemainBoundary, nonDoctorateShortLeaveToRemain, nonDoctorateLongLeaveToRemain
        ).get() == leaveToRemain.toString()

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate    | preSessional || leaveToRemain
        LocalDate.of(2026, 3, 3)   | LocalDate.of(2026, 12, 20) | null                       | false        || "P11M18D"
        LocalDate.of(1990, 12, 31) | LocalDate.of(1991, 3, 9)   | LocalDate.of(1991, 7, 10)  | false        || "P2M17D"
        LocalDate.of(2042, 6, 10)  | LocalDate.of(2042, 12, 11) | LocalDate.of(2043, 12, 18) | false        || "P6M9D"
        LocalDate.of(1988, 4, 12)  | LocalDate.of(1988, 5, 14)  | LocalDate.of(1989, 6, 2)   | false        || "P1M10D"
        LocalDate.of(2044, 1, 7)   | LocalDate.of(2044, 9, 25)  | null                       | true         || "P10M19D"
        LocalDate.of(2009, 11, 29) | LocalDate.of(2010, 11, 5)  | LocalDate.of(2011, 1, 27)  | false        || "P11M15D"
        LocalDate.of(1994, 7, 17)  | LocalDate.of(1994, 8, 24)  | null                       | true         || "P2M8D"
        LocalDate.of(2005, 6, 3)   | LocalDate.of(2005, 7, 17)  | null                       | true         || "P2M15D"
        LocalDate.of(2032, 5, 18)  | LocalDate.of(2032, 6, 19)  | LocalDate.of(2033, 1, 6)   | false        || "P1M9D"
        LocalDate.of(2040, 7, 30)  | LocalDate.of(2040, 12, 3)  | null                       | false        || "P4M11D"

    }

}
