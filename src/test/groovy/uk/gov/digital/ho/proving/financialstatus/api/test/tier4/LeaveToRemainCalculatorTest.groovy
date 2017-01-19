package uk.gov.digital.ho.proving.financialstatus.api.test.tier4

import spock.lang.Specification
import uk.gov.digital.ho.proving.financialstatus.api.test.DataUtils
import uk.gov.digital.ho.proving.financialstatus.domain.LeaveToRemainCalculator

import java.time.LocalDate

class LeaveToRemainCalculatorTest extends Specification {

    def "Calculate leave to remain from course given dates"() {

        expect:
        LeaveToRemainCalculator.calculateLeaveToRemain(DataUtils.buildScalaOption(courseStartDate), DataUtils.buildScalaOption(courseEndDate),
            DataUtils.buildScalaOption(originalCourseStartDate), preSessional).get().toString() == leaveToRemain.toString()

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate    | preSessional || leaveToRemain
        LocalDate.of(1973, 11, 13) | LocalDate.of(1974, 11, 28) | LocalDate.of(1973, 10, 12) | false        || "1975-03-28"
        LocalDate.of(1996, 4, 10)  | LocalDate.of(1996, 11, 14) | null                       | true         || "1997-01-14"
        LocalDate.of(2050, 3, 10)  | LocalDate.of(2050, 11, 20) | LocalDate.of(2049, 5, 11)  | false        || "2051-03-20"
        LocalDate.of(1983, 2, 22)  | LocalDate.of(1983, 3, 22)  | LocalDate.of(1982, 9, 22)  | false        || "1983-05-22"
        LocalDate.of(2014, 3, 11)  | LocalDate.of(2014, 5, 8)   | LocalDate.of(2013, 11, 29) | false        || "2014-05-15"
        LocalDate.of(1987, 10, 16) | LocalDate.of(1987, 11, 18) | LocalDate.of(1987, 4, 8)   | false        || "1988-01-18"
        LocalDate.of(1974, 3, 1)   | LocalDate.of(1975, 1, 1)   | null                       | false        || "1975-03-01"
        LocalDate.of(2037, 10, 15) | LocalDate.of(2038, 2, 17)  | LocalDate.of(2037, 8, 26)  | false        || "2038-02-24"
        LocalDate.of(1984, 11, 6)  | LocalDate.of(1985, 1, 10)  | LocalDate.of(1984, 6, 27)  | false        || "1985-03-10"
        LocalDate.of(1976, 11, 7)  | LocalDate.of(1977, 4, 13)  | null                       | false        || "1977-04-20"

    }

}
