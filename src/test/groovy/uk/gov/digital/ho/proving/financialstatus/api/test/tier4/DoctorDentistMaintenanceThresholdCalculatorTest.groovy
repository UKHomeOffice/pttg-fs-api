package uk.gov.digital.ho.proving.financialstatus.api.test.tier4

import spock.lang.Specification
import uk.gov.digital.ho.proving.financialstatus.api.test.DataUtils
import uk.gov.digital.ho.proving.financialstatus.domain.MaintenanceThresholdCalculatorT4

import java.time.LocalDate

import static uk.gov.digital.ho.proving.financialstatus.api.test.DataUtils.buildScalaBigDecimal
import static uk.gov.digital.ho.proving.financialstatus.api.test.DataUtils.buildScalaOption


class DoctorDentistMaintenanceThresholdCalculatorTest extends Specification {

    MaintenanceThresholdCalculatorT4 maintenanceThresholdCalculator = TestUtilsTier4.maintenanceThresholdServiceBuilder()


    def "Tier 4 General - Check 'Non Inner London Borough'"() {

        expect:
        def response = maintenanceThresholdCalculator.calculatePostGraduateDoctorDentist(inLondon, buildScalaBigDecimal(accommodationFeesPaid), dependants, courseStartDate, courseEndDate, buildScalaOption(originalCourseStartDate), dependantsOnly)

        assert (response._1 == buildScalaBigDecimal(threshold))

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate    | inLondon | accommodationFeesPaid | dependants | dependantsOnly || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(2023, 8, 6)   | LocalDate.of(2024, 4, 26)  | null                       | false    | 1322.00               | 0          | false          || 765.00    || 1265.00    || 2            || LocalDate.of(2024, 5, 26)
        LocalDate.of(2034, 5, 6)   | LocalDate.of(2034, 7, 10)  | LocalDate.of(2033, 9, 5)   | false    | 0.00                  | 1          | false          || 3390.00   || 0.00       || 2            || LocalDate.of(2034, 8, 10)
        LocalDate.of(2000, 3, 8)   | LocalDate.of(2000, 7, 21)  | LocalDate.of(2000, 2, 10)  | false    | 1552.10               | 3          | false          || 4845.00   || 1265.00    || 2            || LocalDate.of(2000, 8, 21)
        LocalDate.of(1996, 11, 10) | LocalDate.of(1997, 9, 27)  | LocalDate.of(1996, 7, 12)  | false    | 210.69                | 10         | false          || 15419.31  || 0.00       || 2            || LocalDate.of(1997, 10, 27)
        LocalDate.of(1976, 7, 30)  | LocalDate.of(1977, 1, 28)  | LocalDate.of(1976, 1, 16)  | false    | 0.00                  | 6          | false          || 10190.00  || 0.00       || 2            || LocalDate.of(1977, 2, 28)
        LocalDate.of(2012, 1, 12)  | LocalDate.of(2012, 10, 23) | null                       | false    | 0.00                  | 3          | false          || 6110.00   || 0.00       || 2            || LocalDate.of(2012, 11, 23)
        LocalDate.of(1989, 8, 27)  | LocalDate.of(1989, 11, 3)  | null                       | false    | 0.00                  | 0          | false          || 2030.00   || 0.00       || 2            || LocalDate.of(1989, 12, 3)
        LocalDate.of(2023, 6, 4)   | LocalDate.of(2023, 6, 26)  | LocalDate.of(2022, 10, 17) | false    | 527.18                | 3          | false          || 4567.82   || 0.00       || 0            || LocalDate.of(2023, 7, 26)
        LocalDate.of(2012, 6, 10)  | LocalDate.of(2013, 4, 8)   | LocalDate.of(2012, 5, 4)   | false    | 106.06                | 3          | false          || 6003.94   || 0.00       || 2            || LocalDate.of(2013, 5, 8)
        LocalDate.of(2051, 10, 5)  | LocalDate.of(2051, 11, 10) | LocalDate.of(2051, 1, 20)  | false    | 283.87                | 0          | false          || 1746.13   || 0.00       || 0            || LocalDate.of(2051, 12, 10)
        LocalDate.of(2046, 7, 14)  | LocalDate.of(2046, 8, 25)  | null                       | false    | 1338.90               | 0          | false          || 765.00    || 1265.00    || 0            || LocalDate.of(2046, 9, 25)
        LocalDate.of(2035, 8, 23)  | LocalDate.of(2035, 11, 13) | LocalDate.of(2035, 7, 24)  | false    | 648.96                | 5          | false          || 8181.04   || 0.00       || 2            || LocalDate.of(2035, 12, 13)
        LocalDate.of(2046, 11, 24) | LocalDate.of(2047, 9, 2)   | LocalDate.of(2046, 5, 27)  | false    | 0.00                  | 0          | false          || 2030.00   || 0.00       || 2            || LocalDate.of(2047, 10, 2)
        LocalDate.of(2035, 8, 30)  | LocalDate.of(2036, 4, 30)  | LocalDate.of(2034, 10, 6)  | false    | 1747.93               | 2          | false          || 3485.00   || 1265.00    || 2            || LocalDate.of(2036, 5, 30)
        LocalDate.of(1997, 10, 17) | LocalDate.of(1997, 11, 10) | null                       | false    | 0.00                  | 0          | false          || 1015.00   || 0.00       || 0            || LocalDate.of(1997, 12, 10)
        LocalDate.of(1985, 8, 21)  | LocalDate.of(1986, 5, 7)   | LocalDate.of(1984, 11, 13) | false    | 1283.23               | 0          | false          || 765.00    || 1265.00    || 2            || LocalDate.of(1986, 6, 7)
        LocalDate.of(2041, 6, 16)  | LocalDate.of(2041, 8, 31)  | LocalDate.of(2040, 6, 27)  | false    | 0.00                  | 2          | false          || 4750.00   || 0.00       || 2            || LocalDate.of(2041, 9, 30)
        LocalDate.of(2040, 4, 3)   | LocalDate.of(2041, 2, 5)   | null                       | false    | 0.00                  | 8          | false          || 12910.00  || 0.00       || 2            || LocalDate.of(2041, 3, 5)
        LocalDate.of(1984, 7, 25)  | LocalDate.of(1985, 2, 23)  | null                       | false    | 0.00                  | 0          | false          || 2030.00   || 0.00       || 2            || LocalDate.of(1985, 3, 23)
        LocalDate.of(1986, 10, 17) | LocalDate.of(1987, 8, 11)  | null                       | false    | 0.00                  | 14         | false          || 21070.00  || 0.00       || 2            || LocalDate.of(1987, 9, 11)
    }

    def "Tier 4 General - Check 'Inner London Borough'"() {

        expect:
        def response = maintenanceThresholdCalculator.calculatePostGraduateDoctorDentist(inLondon, buildScalaBigDecimal(accommodationFeesPaid), dependants, courseStartDate, courseEndDate, buildScalaOption(originalCourseStartDate), dependantsOnly)

        assert (response._1 == buildScalaBigDecimal(threshold))

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate    | inLondon | accommodationFeesPaid | dependants | dependantsOnly || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(2037, 7, 17)  | LocalDate.of(2038, 8, 21)  | LocalDate.of(2036, 9, 29)  | true     | 0.00                  | 4          | false          || 9290.00   || 0.00       || 2            || LocalDate.of(2038, 9, 21)
        LocalDate.of(1986, 2, 4)   | LocalDate.of(1987, 1, 2)   | LocalDate.of(1985, 1, 1)   | true     | 0.00                  | 14         | false          || 26190.00  || 0.00       || 2            || LocalDate.of(1987, 2, 2)
        LocalDate.of(2012, 4, 21)  | LocalDate.of(2012, 10, 21) | LocalDate.of(2011, 7, 3)   | true     | 0.00                  | 5          | false          || 10980.00  || 0.00       || 2            || LocalDate.of(2012, 11, 21)
        LocalDate.of(2036, 6, 12)  | LocalDate.of(2036, 11, 13) | null                       | true     | 0.00                  | 0          | false          || 2530.00   || 0.00       || 2            || LocalDate.of(2036, 12, 13)
        LocalDate.of(1980, 12, 10) | LocalDate.of(1981, 2, 6)   | LocalDate.of(1980, 11, 20) | true     | 146.57                | 12         | false          || 22663.43  || 0.00       || 0            || LocalDate.of(1981, 3, 6)
        LocalDate.of(2015, 1, 31)  | LocalDate.of(2015, 11, 19) | null                       | true     | 179.48                | 14         | false          || 26010.52  || 0.00       || 2            || LocalDate.of(2015, 12, 19)
        LocalDate.of(2033, 1, 18)  | LocalDate.of(2033, 9, 26)  | LocalDate.of(2032, 3, 19)  | true     | 0.00                  | 4          | false          || 9290.00   || 0.00       || 2            || LocalDate.of(2033, 10, 26)
        LocalDate.of(1981, 4, 23)  | LocalDate.of(1982, 3, 11)  | LocalDate.of(1981, 3, 20)  | true     | 288.11                | 9          | false          || 17451.89  || 0.00       || 2            || LocalDate.of(1982, 4, 11)
        LocalDate.of(2004, 10, 13) | LocalDate.of(2005, 6, 15)  | LocalDate.of(2004, 6, 26)  | true     | 547.61                | 14         | false          || 25642.39  || 0.00       || 2            || LocalDate.of(2005, 7, 15)
        LocalDate.of(1989, 7, 18)  | LocalDate.of(1990, 2, 4)   | LocalDate.of(1989, 6, 7)   | true     | 0.00                  | 3          | false          || 7600.00   || 0.00       || 2            || LocalDate.of(1990, 3, 4)
        LocalDate.of(1999, 7, 12)  | LocalDate.of(2000, 8, 15)  | LocalDate.of(1999, 3, 25)  | true     | 820.70                | 12         | false          || 21989.30  || 0.00       || 2            || LocalDate.of(2000, 9, 15)
        LocalDate.of(1991, 3, 27)  | LocalDate.of(1991, 7, 23)  | LocalDate.of(1990, 10, 19) | true     | 323.16                | 10         | false          || 19106.84  || 0.00       || 2            || LocalDate.of(1991, 8, 23)
        LocalDate.of(1981, 6, 20)  | LocalDate.of(1981, 11, 19) | LocalDate.of(1981, 4, 5)   | true     | 0.00                  | 1          | false          || 4220.00   || 0.00       || 2            || LocalDate.of(1981, 12, 19)
        LocalDate.of(2022, 10, 21) | LocalDate.of(2022, 12, 12) | LocalDate.of(2022, 7, 13)  | true     | 0.00                  | 9          | false          || 17740.00  || 0.00       || 0            || LocalDate.of(2023, 1, 12)
        LocalDate.of(2035, 6, 10)  | LocalDate.of(2035, 12, 10) | LocalDate.of(2034, 11, 17) | true     | 0.00                  | 6          | false          || 12670.00  || 0.00       || 2            || LocalDate.of(2036, 1, 10)
        LocalDate.of(2048, 1, 6)   | LocalDate.of(2048, 2, 27)  | LocalDate.of(2047, 10, 2)  | true     | 1000.78               | 0          | false          || 1529.22   || 0.00       || 0            || LocalDate.of(2048, 3, 27)
        LocalDate.of(2010, 1, 4)   | LocalDate.of(2010, 10, 8)  | LocalDate.of(2009, 4, 6)   | true     | 223.09                | 9          | false          || 17516.91  || 0.00       || 2            || LocalDate.of(2010, 11, 8)
        LocalDate.of(2032, 11, 29) | LocalDate.of(2033, 7, 23)  | LocalDate.of(2032, 5, 15)  | true     | 0.00                  | 14         | false          || 26190.00  || 0.00       || 2            || LocalDate.of(2033, 8, 23)
        LocalDate.of(2009, 10, 19) | LocalDate.of(2010, 9, 13)  | LocalDate.of(2008, 10, 10) | true     | 0.00                  | 7          | false          || 14360.00  || 0.00       || 2            || LocalDate.of(2010, 10, 13)
        LocalDate.of(1980, 11, 10) | LocalDate.of(1981, 8, 4)   | LocalDate.of(1980, 7, 13)  | true     | 0.00                  | 13         | false          || 24500.00  || 0.00       || 2            || LocalDate.of(1981, 9, 4)
    }

    def "Tier 4 General - Check 'Accommodation Fees paid'"() {

        expect:
        def response = maintenanceThresholdCalculator.calculatePostGraduateDoctorDentist(inLondon, buildScalaBigDecimal(accommodationFeesPaid), dependants, courseStartDate, courseEndDate, buildScalaOption(originalCourseStartDate), dependantsOnly)

        assert (response._1 == buildScalaBigDecimal(threshold))

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate    | inLondon | accommodationFeesPaid | dependants | dependantsOnly || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(2025, 7, 21)  | LocalDate.of(2025, 10, 11) | LocalDate.of(2025, 1, 25)  | true     | 380.95                | 7          | false          || 13979.05  || 0.00       || 2            || LocalDate.of(2025, 11, 11)
        LocalDate.of(2052, 3, 26)  | LocalDate.of(2053, 4, 18)  | LocalDate.of(2051, 3, 24)  | true     | 1737.41               | 5          | false          || 9715.00   || 1265.00    || 2            || LocalDate.of(2053, 5, 18)
        LocalDate.of(2033, 4, 19)  | LocalDate.of(2034, 1, 18)  | LocalDate.of(2032, 12, 18) | true     | 485.64                | 5          | false          || 10494.36  || 0.00       || 2            || LocalDate.of(2034, 2, 18)
        LocalDate.of(2012, 11, 6)  | LocalDate.of(2013, 3, 4)   | LocalDate.of(2012, 1, 18)  | false    | 1123.47               | 0          | false          || 906.53    || 0.00       || 2            || LocalDate.of(2013, 4, 4)
        LocalDate.of(1982, 7, 11)  | LocalDate.of(1982, 12, 13) | null                       | true     | 1626.01               | 0          | false          || 1265.00   || 1265.00    || 2            || LocalDate.of(1983, 1, 13)
        LocalDate.of(2040, 3, 3)   | LocalDate.of(2040, 6, 4)   | LocalDate.of(2039, 5, 23)  | true     | 667.35                | 11         | false          || 20452.65  || 0.00       || 2            || LocalDate.of(2040, 7, 4)
        LocalDate.of(1979, 10, 22) | LocalDate.of(1980, 9, 15)  | LocalDate.of(1979, 5, 28)  | false    | 1412.52               | 11         | false          || 15725.00  || 1265.00    || 2            || LocalDate.of(1980, 10, 15)
        LocalDate.of(2048, 11, 23) | LocalDate.of(2049, 1, 20)  | null                       | false    | 619.97                | 0          | false          || 1410.03   || 0.00       || 0            || LocalDate.of(2049, 2, 20)
        LocalDate.of(2037, 3, 30)  | LocalDate.of(2037, 8, 8)   | LocalDate.of(2036, 6, 10)  | false    | 1495.72               | 5          | false          || 7565.00   || 1265.00    || 2            || LocalDate.of(2037, 9, 8)
        LocalDate.of(2028, 11, 27) | LocalDate.of(2029, 2, 13)  | LocalDate.of(2028, 2, 14)  | true     | 1861.07               | 2          | false          || 4645.00   || 1265.00    || 2            || LocalDate.of(2029, 3, 13)
        LocalDate.of(2007, 11, 15) | LocalDate.of(2008, 7, 6)   | LocalDate.of(2007, 3, 5)   | false    | 698.70                | 6          | false          || 9491.30   || 0.00       || 2            || LocalDate.of(2008, 8, 6)
        LocalDate.of(2000, 4, 21)  | LocalDate.of(2001, 5, 21)  | LocalDate.of(1999, 4, 14)  | true     | 533.05                | 2          | false          || 5376.95   || 0.00       || 2            || LocalDate.of(2001, 6, 21)
        LocalDate.of(2011, 5, 16)  | LocalDate.of(2011, 10, 29) | LocalDate.of(2011, 2, 26)  | true     | 231.11                | 14         | false          || 25958.89  || 0.00       || 2            || LocalDate.of(2011, 11, 29)
        LocalDate.of(1987, 4, 19)  | LocalDate.of(1987, 5, 1)   | LocalDate.of(1987, 4, 3)   | false    | 907.12                | 13         | false          || 17787.88  || 0.00       || 0            || LocalDate.of(1987, 6, 1)
        LocalDate.of(2030, 8, 28)  | LocalDate.of(2031, 2, 5)   | LocalDate.of(2030, 1, 28)  | false    | 1775.42               | 5          | false          || 7565.00   || 1265.00    || 2            || LocalDate.of(2031, 3, 5)
        LocalDate.of(1994, 2, 5)   | LocalDate.of(1994, 9, 7)   | null                       | true     | 1317.64               | 12         | false          || 21545.00  || 1265.00    || 2            || LocalDate.of(1994, 10, 7)
        LocalDate.of(2049, 9, 3)   | LocalDate.of(2050, 8, 23)  | null                       | false    | 1656.96               | 9          | false          || 13005.00  || 1265.00    || 2            || LocalDate.of(2050, 9, 23)
        LocalDate.of(1995, 7, 14)  | LocalDate.of(1995, 12, 21) | null                       | true     | 1782.30               | 0          | false          || 1265.00   || 1265.00    || 2            || LocalDate.of(1996, 1, 21)
        LocalDate.of(2039, 9, 16)  | LocalDate.of(2040, 4, 5)   | LocalDate.of(2039, 9, 11)  | true     | 679.58                | 0          | false          || 1850.42   || 0.00       || 2            || LocalDate.of(2040, 5, 5)
        LocalDate.of(1976, 3, 16)  | LocalDate.of(1976, 4, 5)   | null                       | true     | 1687.95               | 0          | false          || 0.00      || 1265.00    || 0            || LocalDate.of(1976, 5, 5)
    }

    def "Tier 4 General - Check 'continuations'"() {

        expect:
        def response = maintenanceThresholdCalculator.calculatePostGraduateDoctorDentist(inLondon, buildScalaBigDecimal(accommodationFeesPaid), dependants, courseStartDate, courseEndDate, buildScalaOption(originalCourseStartDate), dependantsOnly)

        def thresholdValue = response._1
        def cappedValues = DataUtils.getCappedValues(response._2)
        def cappedAccommodation = cappedValues.accommodationFeesPaid()
        def cappedCourseLength = cappedValues.courseLength()

        assert thresholdValue == buildScalaBigDecimal(threshold)
        assert DataUtils.compareAccommodationFees(buildScalaBigDecimal(feesCapped), cappedAccommodation) == true
        assert DataUtils.compareCourseLength(courseCapped, cappedCourseLength) == true

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate    | inLondon | accommodationFeesPaid | dependants | dependantsOnly || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(2026, 8, 3)   | LocalDate.of(2026, 9, 13)  | LocalDate.of(2026, 1, 21)  | false    | 0.00                  | 3          | false          || 6110.00   || 0.00       || 0            || LocalDate.of(2026, 10, 13)
        LocalDate.of(2026, 1, 15)  | LocalDate.of(2026, 12, 6)  | LocalDate.of(2025, 4, 9)   | true     | 841.65                | 6          | false          || 11828.35  || 0.00       || 2            || LocalDate.of(2027, 1, 6)
        LocalDate.of(1984, 1, 3)   | LocalDate.of(1984, 1, 11)  | LocalDate.of(1983, 11, 27) | true     | 0.00                  | 8          | false          || 14785.00  || 0.00       || 0            || LocalDate.of(1984, 2, 11)
        LocalDate.of(2050, 4, 21)  | LocalDate.of(2050, 5, 13)  | LocalDate.of(2049, 6, 2)   | false    | 0.00                  | 13         | false          || 18695.00  || 0.00       || 0            || LocalDate.of(2050, 6, 13)
        LocalDate.of(2008, 8, 23)  | LocalDate.of(2008, 12, 17) | LocalDate.of(2007, 11, 23) | true     | 1848.93               | 6          | false          || 11405.00  || 1265.00    || 2            || LocalDate.of(2009, 1, 17)
        LocalDate.of(2009, 11, 29) | LocalDate.of(2010, 10, 7)  | LocalDate.of(2009, 11, 9)  | false    | 1925.91               | 13         | false          || 18445.00  || 1265.00    || 2            || LocalDate.of(2010, 11, 7)
        LocalDate.of(2052, 5, 21)  | LocalDate.of(2053, 5, 20)  | LocalDate.of(2051, 9, 1)   | false    | 0.00                  | 2          | false          || 4750.00   || 0.00       || 2            || LocalDate.of(2053, 6, 20)
        LocalDate.of(2046, 10, 18) | LocalDate.of(2046, 11, 8)  | LocalDate.of(2045, 9, 15)  | true     | 1987.05               | 9          | false          || 15210.00  || 1265.00    || 0            || LocalDate.of(2046, 12, 8)
        LocalDate.of(1977, 3, 26)  | LocalDate.of(1978, 1, 7)   | LocalDate.of(1976, 2, 25)  | true     | 1789.41               | 5          | false          || 9715.00   || 1265.00    || 2            || LocalDate.of(1978, 2, 7)
        LocalDate.of(2030, 6, 10)  | LocalDate.of(2031, 5, 19)  | LocalDate.of(2029, 9, 17)  | false    | 0.00                  | 6          | false          || 10190.00  || 0.00       || 2            || LocalDate.of(2031, 6, 19)
        LocalDate.of(2006, 10, 10) | LocalDate.of(2007, 3, 19)  | LocalDate.of(2006, 10, 5)  | true     | 1101.84               | 10         | false          || 18328.16  || 0.00       || 2            || LocalDate.of(2007, 4, 19)
        LocalDate.of(1973, 4, 15)  | LocalDate.of(1973, 11, 13) | LocalDate.of(1972, 11, 29) | false    | 0.00                  | 1          | false          || 3390.00   || 0.00       || 2            || LocalDate.of(1973, 12, 13)
        LocalDate.of(1990, 11, 1)  | LocalDate.of(1990, 12, 8)  | LocalDate.of(1989, 10, 26) | true     | 1785.84               | 9          | false          || 16475.00  || 1265.00    || 0            || LocalDate.of(1991, 1, 8)
        LocalDate.of(2021, 10, 28) | LocalDate.of(2022, 7, 28)  | LocalDate.of(2021, 3, 6)   | true     | 99.72                 | 14         | false          || 26090.28  || 0.00       || 2            || LocalDate.of(2022, 8, 28)
        LocalDate.of(2044, 3, 24)  | LocalDate.of(2045, 3, 12)  | LocalDate.of(2044, 3, 16)  | false    | 0.00                  | 2          | false          || 4750.00   || 0.00       || 2            || LocalDate.of(2045, 4, 12)
        LocalDate.of(2005, 6, 12)  | LocalDate.of(2006, 2, 7)   | LocalDate.of(2004, 9, 27)  | true     | 1367.69               | 11         | false          || 19855.00  || 1265.00    || 2            || LocalDate.of(2006, 3, 7)
        LocalDate.of(2038, 7, 14)  | LocalDate.of(2039, 3, 27)  | LocalDate.of(2038, 6, 10)  | false    | 1385.44               | 10         | false          || 14365.00  || 1265.00    || 2            || LocalDate.of(2039, 4, 27)
        LocalDate.of(1983, 3, 17)  | LocalDate.of(1983, 5, 18)  | LocalDate.of(1982, 3, 4)   | false    | 1010.35               | 13         | false          || 18699.65  || 0.00       || 2            || LocalDate.of(1983, 6, 18)
        LocalDate.of(1979, 4, 21)  | LocalDate.of(1979, 5, 21)  | LocalDate.of(1978, 7, 4)   | true     | 0.00                  | 3          | false          || 7600.00   || 0.00       || 0            || LocalDate.of(1979, 6, 21)
        LocalDate.of(2009, 2, 6)   | LocalDate.of(2009, 10, 16) | LocalDate.of(2008, 10, 3)  | true     | 1298.18               | 9          | false          || 16475.00  || 1265.00    || 2            || LocalDate.of(2009, 11, 16)
    }

    // Dependants only

    def "Tier 4 General - Check 'Non Inner London Borough' (dependants only)"() {

        expect:
        def response = maintenanceThresholdCalculator.calculatePostGraduateDoctorDentist(inLondon, buildScalaBigDecimal(accommodationFeesPaid), dependants, courseStartDate, courseEndDate, buildScalaOption(originalCourseStartDate), dependantsOnly)

        assert (response._1 == buildScalaBigDecimal(threshold))

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate    | inLondon | accommodationFeesPaid | dependants | dependantsOnly || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(1974, 5, 3)   | LocalDate.of(1974, 8, 23)  | LocalDate.of(1973, 9, 30)  | false    | 0.00                  | 12         | true           || 16320.00  || 0.00       || 2            || LocalDate.of(1974, 9, 23)
        LocalDate.of(2050, 7, 2)   | LocalDate.of(2050, 10, 21) | LocalDate.of(2050, 5, 31)  | false    | 1637.66               | 2          | true           || 2720.00   || 1265.00    || 2            || LocalDate.of(2050, 11, 21)
        LocalDate.of(2025, 8, 14)  | LocalDate.of(2026, 5, 5)   | null                       | false    | 0.00                  | 11         | true           || 14960.00  || 0.00       || 2            || LocalDate.of(2026, 6, 5)
        LocalDate.of(1999, 8, 27)  | LocalDate.of(2000, 1, 11)  | LocalDate.of(1998, 11, 3)  | false    | 0.00                  | 4          | true           || 5440.00   || 0.00       || 2            || LocalDate.of(2000, 2, 11)
        LocalDate.of(1997, 4, 25)  | LocalDate.of(1997, 6, 24)  | LocalDate.of(1996, 6, 18)  | false    | 1074.79               | 2          | true           || 2720.00   || 0.00       || 0            || LocalDate.of(1997, 7, 24)
        LocalDate.of(2015, 1, 14)  | LocalDate.of(2015, 7, 7)   | null                       | false    | 756.52                | 0          | true           || 0.00      || 0.00       || 2            || LocalDate.of(2015, 8, 7)
        LocalDate.of(2032, 11, 14) | LocalDate.of(2032, 12, 24) | LocalDate.of(2032, 10, 2)  | false    | 745.12                | 14         | true           || 19040.00  || 0.00       || 0            || LocalDate.of(2033, 1, 24)
        LocalDate.of(1976, 1, 11)  | LocalDate.of(1976, 6, 6)   | LocalDate.of(1975, 8, 6)   | false    | 0.00                  | 0          | true           || 0.00      || 0.00       || 2            || LocalDate.of(1976, 7, 6)
        LocalDate.of(1978, 8, 3)   | LocalDate.of(1978, 10, 24) | LocalDate.of(1978, 5, 15)  | false    | 1995.17               | 0          | true           || 0.00      || 1265.00    || 2            || LocalDate.of(1978, 11, 24)
        LocalDate.of(1999, 10, 4)  | LocalDate.of(2000, 10, 11) | LocalDate.of(1998, 10, 30) | false    | 1137.04               | 5          | true           || 6800.00   || 0.00       || 2            || LocalDate.of(2000, 11, 11)
        LocalDate.of(2020, 7, 5)   | LocalDate.of(2020, 7, 12)  | null                       | false    | 0.00                  | 0          | true           || 0.00      || 0.00       || 0            || LocalDate.of(2020, 8, 12)
        LocalDate.of(2034, 8, 8)   | LocalDate.of(2034, 9, 29)  | null                       | false    | 1491.19               | 0          | true           || 0.00      || 1265.00    || 0            || LocalDate.of(2034, 10, 29)
        LocalDate.of(2031, 11, 23) | LocalDate.of(2032, 4, 19)  | null                       | false    | 0.00                  | 0          | true           || 0.00      || 0.00       || 2            || LocalDate.of(2032, 5, 19)
        LocalDate.of(2051, 7, 10)  | LocalDate.of(2052, 6, 3)   | LocalDate.of(2050, 9, 18)  | false    | 0.00                  | 4          | true           || 5440.00   || 0.00       || 2            || LocalDate.of(2052, 7, 3)
        LocalDate.of(1998, 6, 6)   | LocalDate.of(1998, 12, 4)  | null                       | false    | 0.00                  | 0          | true           || 0.00      || 0.00       || 2            || LocalDate.of(1999, 1, 4)
        LocalDate.of(2009, 8, 1)   | LocalDate.of(2009, 9, 26)  | null                       | false    | 0.00                  | 0          | true           || 0.00      || 0.00       || 0            || LocalDate.of(2009, 10, 26)
        LocalDate.of(2037, 5, 16)  | LocalDate.of(2037, 7, 22)  | LocalDate.of(2036, 10, 15) | false    | 433.16                | 12         | true           || 16320.00  || 0.00       || 2            || LocalDate.of(2037, 8, 22)
        LocalDate.of(2020, 10, 5)  | LocalDate.of(2021, 11, 7)  | LocalDate.of(2019, 9, 22)  | false    | 825.22                | 14         | true           || 19040.00  || 0.00       || 2            || LocalDate.of(2021, 12, 7)
        LocalDate.of(2018, 9, 25)  | LocalDate.of(2019, 6, 28)  | null                       | false    | 0.00                  | 14         | true           || 19040.00  || 0.00       || 2            || LocalDate.of(2019, 7, 28)
        LocalDate.of(2000, 2, 5)   | LocalDate.of(2000, 7, 8)   | LocalDate.of(1999, 11, 16) | false    | 0.00                  | 1          | true           || 1360.00   || 0.00       || 2            || LocalDate.of(2000, 8, 8)
    }

    def "Tier 4 General - Check 'Inner London Borough' (dependants only)"() {

        expect:
        def response = maintenanceThresholdCalculator.calculatePostGraduateDoctorDentist(inLondon, buildScalaBigDecimal(accommodationFeesPaid), dependants, courseStartDate, courseEndDate, buildScalaOption(originalCourseStartDate), dependantsOnly)

        assert (response._1 == buildScalaBigDecimal(threshold))

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate    | inLondon | accommodationFeesPaid | dependants | dependantsOnly || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(1974, 2, 24)  | LocalDate.of(1975, 1, 12)  | LocalDate.of(1973, 6, 22)  | true     | 0.00                  | 4          | true           || 6760.00   || 0.00       || 2            || LocalDate.of(1975, 2, 12)
        LocalDate.of(2027, 9, 6)   | LocalDate.of(2028, 2, 6)   | null                       | true     | 1153.91               | 0          | true           || 0.00      || 0.00       || 2            || LocalDate.of(2028, 3, 6)
        LocalDate.of(2050, 6, 28)  | LocalDate.of(2050, 8, 11)  | null                       | true     | 0.00                  | 0          | true           || 0.00      || 0.00       || 0            || LocalDate.of(2050, 9, 11)
        LocalDate.of(2044, 4, 16)  | LocalDate.of(2044, 10, 1)  | LocalDate.of(2043, 10, 11) | true     | 178.34                | 9          | true           || 15210.00  || 0.00       || 2            || LocalDate.of(2044, 11, 1)
        LocalDate.of(2048, 4, 26)  | LocalDate.of(2049, 1, 11)  | LocalDate.of(2047, 7, 26)  | true     | 527.04                | 14         | true           || 23660.00  || 0.00       || 2            || LocalDate.of(2049, 2, 11)
        LocalDate.of(2031, 12, 16) | LocalDate.of(2032, 3, 2)   | null                       | true     | 1332.37               | 0          | true           || 0.00      || 1265.00    || 2            || LocalDate.of(2032, 4, 2)
        LocalDate.of(1989, 8, 8)   | LocalDate.of(1989, 10, 9)  | null                       | true     | 367.97                | 0          | true           || 0.00      || 0.00       || 2            || LocalDate.of(1989, 11, 9)
        LocalDate.of(1999, 12, 20) | LocalDate.of(2000, 6, 26)  | LocalDate.of(1998, 12, 25) | true     | 0.00                  | 9          | true           || 15210.00  || 0.00       || 2            || LocalDate.of(2000, 7, 26)
        LocalDate.of(2053, 1, 21)  | LocalDate.of(2053, 9, 28)  | LocalDate.of(2052, 4, 20)  | true     | 96.55                 | 10         | true           || 16900.00  || 0.00       || 2            || LocalDate.of(2053, 10, 28)
        LocalDate.of(1976, 1, 12)  | LocalDate.of(1976, 10, 18) | null                       | true     | 0.00                  | 12         | true           || 20280.00  || 0.00       || 2            || LocalDate.of(1976, 11, 18)
        LocalDate.of(2051, 5, 7)   | LocalDate.of(2051, 7, 18)  | null                       | true     | 0.00                  | 0          | true           || 0.00      || 0.00       || 2            || LocalDate.of(2051, 8, 18)
        LocalDate.of(1993, 4, 26)  | LocalDate.of(1993, 9, 18)  | LocalDate.of(1992, 7, 17)  | true     | 218.06                | 2          | true           || 3380.00   || 0.00       || 2            || LocalDate.of(1993, 10, 18)
        LocalDate.of(2048, 4, 12)  | LocalDate.of(2048, 11, 27) | null                       | true     | 198.97                | 3          | true           || 5070.00   || 0.00       || 2            || LocalDate.of(2048, 12, 27)
        LocalDate.of(1995, 9, 27)  | LocalDate.of(1995, 10, 6)  | LocalDate.of(1995, 8, 6)   | true     | 1111.96               | 10         | true           || 16900.00  || 0.00       || 0            || LocalDate.of(1995, 11, 6)
        LocalDate.of(2031, 3, 28)  | LocalDate.of(2032, 1, 16)  | null                       | true     | 1347.92               | 4          | true           || 6760.00   || 1265.00    || 2            || LocalDate.of(2032, 2, 16)
        LocalDate.of(2003, 8, 27)  | LocalDate.of(2004, 3, 6)   | LocalDate.of(2002, 8, 10)  | true     | 633.27                | 11         | true           || 18590.00  || 0.00       || 2            || LocalDate.of(2004, 4, 6)
        LocalDate.of(1997, 3, 22)  | LocalDate.of(1997, 9, 19)  | LocalDate.of(1996, 10, 29) | true     | 0.00                  | 4          | true           || 6760.00   || 0.00       || 2            || LocalDate.of(1997, 10, 19)
        LocalDate.of(2002, 5, 1)   | LocalDate.of(2003, 4, 12)  | LocalDate.of(2002, 3, 28)  | true     | 0.00                  | 5          | true           || 8450.00   || 0.00       || 2            || LocalDate.of(2003, 5, 12)
        LocalDate.of(2015, 10, 4)  | LocalDate.of(2016, 1, 3)   | LocalDate.of(2014, 12, 29) | true     | 1969.10               | 5          | true           || 8450.00   || 1265.00    || 2            || LocalDate.of(2016, 2, 3)
        LocalDate.of(2041, 6, 18)  | LocalDate.of(2042, 3, 18)  | LocalDate.of(2041, 1, 17)  | true     | 0.00                  | 1          | true           || 1690.00   || 0.00       || 2            || LocalDate.of(2042, 4, 18)
    }

    def "Tier 4 General - Check 'continuations' (dependants only)"() {

        expect:
        def response = maintenanceThresholdCalculator.calculatePostGraduateDoctorDentist(inLondon, buildScalaBigDecimal(accommodationFeesPaid), dependants, courseStartDate, courseEndDate, buildScalaOption(originalCourseStartDate), dependantsOnly)

        def thresholdValue = response._1
        def cappedValues = DataUtils.getCappedValues(response._2)
        def cappedAccommodation = cappedValues.accommodationFeesPaid()
        def cappedCourseLength = cappedValues.courseLength()

        assert thresholdValue == buildScalaBigDecimal(threshold)
        assert DataUtils.compareAccommodationFees(buildScalaBigDecimal(feesCapped), cappedAccommodation) == true
        assert DataUtils.compareCourseLength(courseCapped, cappedCourseLength) == true

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate    | inLondon | accommodationFeesPaid | dependants | dependantsOnly || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(2023, 12, 9)  | LocalDate.of(2024, 2, 28)  | LocalDate.of(2023, 5, 24)  | false    | 32.09                 | 3          | true           || 4080.00   || 0.00       || 2            || LocalDate.of(2024, 3, 28)
        LocalDate.of(1984, 11, 13) | LocalDate.of(1985, 11, 5)  | LocalDate.of(1984, 5, 3)   | true     | 513.93                | 0          | true           || 0.00      || 0.00       || 2            || LocalDate.of(1985, 12, 5)
        LocalDate.of(1993, 2, 10)  | LocalDate.of(1993, 2, 20)  | LocalDate.of(1992, 3, 19)  | false    | 311.04                | 10         | true           || 13600.00  || 0.00       || 0            || LocalDate.of(1993, 3, 20)
        LocalDate.of(1998, 3, 10)  | LocalDate.of(1998, 11, 26) | LocalDate.of(1998, 2, 6)   | false    | 878.78                | 8          | true           || 10880.00  || 0.00       || 2            || LocalDate.of(1998, 12, 26)
        LocalDate.of(2006, 7, 22)  | LocalDate.of(2007, 7, 27)  | LocalDate.of(2005, 8, 5)   | false    | 315.21                | 1          | true           || 1360.00   || 0.00       || 2            || LocalDate.of(2007, 8, 27)
        LocalDate.of(1995, 4, 10)  | LocalDate.of(1995, 8, 22)  | LocalDate.of(1994, 4, 29)  | true     | 629.17                | 4          | true           || 6760.00   || 0.00       || 2            || LocalDate.of(1995, 9, 22)
        LocalDate.of(1990, 7, 15)  | LocalDate.of(1991, 6, 6)   | LocalDate.of(1989, 12, 3)  | true     | 468.07                | 0          | true           || 0.00      || 0.00       || 2            || LocalDate.of(1991, 7, 6)
        LocalDate.of(2025, 8, 29)  | LocalDate.of(2025, 9, 4)   | LocalDate.of(2025, 1, 14)  | false    | 1878.35               | 9          | true           || 12240.00  || 1265.00    || 0            || LocalDate.of(2025, 10, 4)
        LocalDate.of(1975, 6, 2)   | LocalDate.of(1976, 6, 5)   | LocalDate.of(1975, 1, 25)  | false    | 0.00                  | 6          | true           || 8160.00   || 0.00       || 2            || LocalDate.of(1976, 7, 5)
        LocalDate.of(2031, 1, 27)  | LocalDate.of(2031, 11, 10) | LocalDate.of(2030, 8, 5)   | true     | 455.95                | 11         | true           || 18590.00  || 0.00       || 2            || LocalDate.of(2031, 12, 10)
        LocalDate.of(2034, 7, 14)  | LocalDate.of(2034, 9, 3)   | LocalDate.of(2034, 1, 31)  | true     | 0.00                  | 5          | true           || 8450.00   || 0.00       || 0            || LocalDate.of(2034, 10, 3)
        LocalDate.of(2011, 9, 28)  | LocalDate.of(2012, 9, 26)  | LocalDate.of(2011, 5, 15)  | true     | 1862.04               | 1          | true           || 1690.00   || 1265.00    || 2            || LocalDate.of(2012, 10, 26)
        LocalDate.of(1976, 11, 3)  | LocalDate.of(1977, 10, 16) | LocalDate.of(1976, 3, 12)  | false    | 0.00                  | 4          | true           || 5440.00   || 0.00       || 2            || LocalDate.of(1977, 11, 16)
        LocalDate.of(2051, 8, 27)  | LocalDate.of(2052, 1, 30)  | LocalDate.of(2050, 8, 2)   | false    | 0.00                  | 14         | true           || 19040.00  || 0.00       || 2            || LocalDate.of(2052, 2, 29)
        LocalDate.of(2012, 6, 4)   | LocalDate.of(2013, 3, 25)  | LocalDate.of(2011, 12, 24) | false    | 205.93                | 5          | true           || 6800.00   || 0.00       || 2            || LocalDate.of(2013, 4, 25)
        LocalDate.of(2015, 1, 30)  | LocalDate.of(2015, 10, 24) | LocalDate.of(2014, 1, 21)  | true     | 0.00                  | 8          | true           || 13520.00  || 0.00       || 2            || LocalDate.of(2015, 11, 24)
        LocalDate.of(1982, 4, 8)   | LocalDate.of(1983, 3, 7)   | LocalDate.of(1982, 3, 26)  | true     | 0.00                  | 4          | true           || 6760.00   || 0.00       || 2            || LocalDate.of(1983, 4, 7)
        LocalDate.of(1979, 4, 11)  | LocalDate.of(1980, 1, 13)  | LocalDate.of(1978, 4, 15)  | true     | 1675.12               | 8          | true           || 13520.00  || 1265.00    || 2            || LocalDate.of(1980, 2, 13)
        LocalDate.of(1992, 9, 30)  | LocalDate.of(1993, 4, 18)  | LocalDate.of(1992, 8, 20)  | false    | 0.00                  | 1          | true           || 1360.00   || 0.00       || 2            || LocalDate.of(1993, 5, 18)
        LocalDate.of(2041, 10, 13) | LocalDate.of(2042, 5, 25)  | LocalDate.of(2041, 7, 1)   | false    | 843.03                | 9          | true           || 12240.00  || 0.00       || 2            || LocalDate.of(2042, 6, 25)
    }

    // All variants

    def "Tier 4 General - Check 'All variants'"() {

        def response = maintenanceThresholdCalculator.calculatePostGraduateDoctorDentist(inLondon, buildScalaBigDecimal(accommodationFeesPaid), dependants, courseStartDate, courseEndDate, buildScalaOption(originalCourseStartDate), dependantsOnly)

        def thresholdValue = response._1
        def cappedValues = DataUtils.getCappedValues(response._2)
        def cappedAccommodation = cappedValues.accommodationFeesPaid()
        def cappedCourseLength = cappedValues.courseLength()

        assert thresholdValue == buildScalaBigDecimal(threshold)
        assert DataUtils.compareAccommodationFees(buildScalaBigDecimal(feesCapped), cappedAccommodation) == true
        assert DataUtils.compareCourseLength(courseCapped, cappedCourseLength) == true

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate    | inLondon | accommodationFeesPaid | dependants | dependantsOnly || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(2022, 9, 19)  | LocalDate.of(2022, 9, 26)  | null                       | true     | 0.00                  | 0          | true           || 0.00      || 0.00       || 0            || LocalDate.of(2022, 10, 26)
        LocalDate.of(1990, 10, 10) | LocalDate.of(1991, 2, 6)   | null                       | true     | 0.00                  | 0          | true           || 0.00      || 0.00       || 2            || LocalDate.of(1991, 3, 6)
        LocalDate.of(1981, 11, 4)  | LocalDate.of(1982, 1, 23)  | null                       | false    | 0.00                  | 0          | false          || 2030.00   || 0.00       || 2            || LocalDate.of(1982, 2, 23)
        LocalDate.of(1995, 12, 7)  | LocalDate.of(1996, 4, 11)  | null                       | true     | 0.00                  | 0          | true           || 0.00      || 0.00       || 2            || LocalDate.of(1996, 5, 11)
        LocalDate.of(1991, 5, 14)  | LocalDate.of(1992, 5, 9)   | LocalDate.of(1991, 4, 12)  | false    | 1324.18               | 14         | false          || 19805.00  || 1265.00    || 2            || LocalDate.of(1992, 6, 9)
        LocalDate.of(2053, 5, 7)   | LocalDate.of(2054, 4, 17)  | LocalDate.of(2053, 4, 11)  | true     | 0.00                  | 13         | false          || 24500.00  || 0.00       || 2            || LocalDate.of(2054, 5, 17)
        LocalDate.of(2034, 2, 21)  | LocalDate.of(2034, 11, 3)  | LocalDate.of(2034, 2, 13)  | true     | 1922.27               | 11         | false          || 19855.00  || 1265.00    || 2            || LocalDate.of(2034, 12, 3)
        LocalDate.of(2020, 2, 4)   | LocalDate.of(2020, 10, 5)  | LocalDate.of(2019, 3, 22)  | false    | 520.75                | 12         | true           || 16320.00  || 0.00       || 2            || LocalDate.of(2020, 11, 5)
        LocalDate.of(1986, 4, 8)   | LocalDate.of(1986, 4, 30)  | LocalDate.of(1986, 3, 5)   | true     | 0.00                  | 11         | false          || 19855.00  || 0.00       || 0            || LocalDate.of(1986, 5, 30)
        LocalDate.of(1972, 10, 31) | LocalDate.of(1973, 10, 30) | LocalDate.of(1972, 3, 5)   | true     | 1276.51               | 12         | true           || 20280.00  || 1265.00    || 2            || LocalDate.of(1973, 11, 30)
        LocalDate.of(2045, 1, 22)  | LocalDate.of(2046, 1, 19)  | LocalDate.of(2044, 3, 11)  | true     | 0.00                  | 13         | false          || 24500.00  || 0.00       || 2            || LocalDate.of(2046, 2, 19)
        LocalDate.of(2011, 5, 17)  | LocalDate.of(2011, 8, 29)  | LocalDate.of(2010, 11, 28) | true     | 0.00                  | 13         | false          || 24500.00  || 0.00       || 2            || LocalDate.of(2011, 9, 29)
        LocalDate.of(1986, 10, 18) | LocalDate.of(1987, 8, 30)  | LocalDate.of(1985, 12, 12) | false    | 1560.05               | 0          | true           || 0.00      || 1265.00    || 2            || LocalDate.of(1987, 9, 30)
        LocalDate.of(1972, 10, 8)  | LocalDate.of(1973, 10, 13) | LocalDate.of(1972, 3, 9)   | true     | 0.00                  | 3          | true           || 5070.00   || 0.00       || 2            || LocalDate.of(1973, 11, 13)
        LocalDate.of(2027, 9, 9)   | LocalDate.of(2028, 5, 26)  | null                       | true     | 0.00                  | 0          | true           || 0.00      || 0.00       || 2            || LocalDate.of(2028, 6, 26)
        LocalDate.of(1974, 8, 24)  | LocalDate.of(1975, 6, 4)   | null                       | false    | 334.32                | 12         | true           || 16320.00  || 0.00       || 2            || LocalDate.of(1975, 7, 4)
        LocalDate.of(2024, 8, 4)   | LocalDate.of(2025, 3, 15)  | null                       | false    | 0.00                  | 13         | false          || 19710.00  || 0.00       || 2            || LocalDate.of(2025, 4, 15)
        LocalDate.of(2048, 3, 6)   | LocalDate.of(2048, 7, 14)  | LocalDate.of(2047, 3, 2)   | false    | 0.00                  | 4          | false          || 7470.00   || 0.00       || 2            || LocalDate.of(2048, 8, 14)
        LocalDate.of(2042, 8, 16)  | LocalDate.of(2043, 1, 7)   | null                       | false    | 0.00                  | 0          | false          || 2030.00   || 0.00       || 2            || LocalDate.of(2043, 2, 7)
        LocalDate.of(1988, 1, 23)  | LocalDate.of(1989, 2, 19)  | null                       | true     | 290.39                | 8          | true           || 13520.00  || 0.00       || 2            || LocalDate.of(1989, 3, 19)
    }

}
