package uk.gov.digital.ho.proving.financialstatus.api.test.tier4

import spock.lang.Specification
import uk.gov.digital.ho.proving.financialstatus.api.test.DataUtils
import uk.gov.digital.ho.proving.financialstatus.domain.MaintenanceThresholdCalculatorT4

import java.time.LocalDate

import static uk.gov.digital.ho.proving.financialstatus.api.test.DataUtils.buildScalaBigDecimal
import static uk.gov.digital.ho.proving.financialstatus.api.test.DataUtils.buildScalaOption


class StudentUnionSabbaticalOfficerMaintenanceThresholdCalculatorTest extends Specification {

    MaintenanceThresholdCalculatorT4 maintenanceThresholdCalculator = TestUtilsTier4.maintenanceThresholdServiceBuilder()


    def "Tier 4 General - Check 'Non Inner London Borough'"() {

        expect:
        def response = maintenanceThresholdCalculator.calculateStudentUnionSabbaticalOfficer(inLondon, buildScalaBigDecimal(accommodationFeesPaid), dependants, courseStartDate, courseEndDate, buildScalaOption(originalCourseStartDate), dependantsOnly)

        assert (response._1 == buildScalaBigDecimal(threshold))

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate    | inLondon | accommodationFeesPaid | dependants | dependantsOnly || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(1977, 2, 2)   | LocalDate.of(1977, 4, 19)  | null                       | false    | 287.44                | 0          | false          || 1742.56   || 0.00       || 2            || LocalDate.of(1977, 4, 26)
        LocalDate.of(2029, 10, 2)  | LocalDate.of(2029, 10, 24) | LocalDate.of(2029, 6, 13)  | false    | 1101.64               | 5          | false          || 3313.36   || 0.00       || 0            || LocalDate.of(2029, 10, 31)
        LocalDate.of(2016, 6, 22)  | LocalDate.of(2017, 4, 17)  | LocalDate.of(2016, 2, 2)   | false    | 705.44                | 12         | false          || 17644.56  || 0.00       || 2            || LocalDate.of(2017, 8, 17)
        LocalDate.of(2047, 8, 24)  | LocalDate.of(2048, 3, 12)  | LocalDate.of(2047, 3, 25)  | false    | 0.00                  | 11         | false          || 16990.00  || 0.00       || 2            || LocalDate.of(2048, 5, 12)
        LocalDate.of(2039, 1, 20)  | LocalDate.of(2039, 7, 20)  | null                       | false    | 445.47                | 5          | false          || 8384.53   || 0.00       || 2            || LocalDate.of(2039, 9, 20)
        LocalDate.of(2027, 9, 14)  | LocalDate.of(2028, 6, 25)  | LocalDate.of(2027, 7, 20)  | false    | 368.05                | 8          | false          || 12541.95  || 0.00       || 2            || LocalDate.of(2028, 8, 25)
        LocalDate.of(2013, 10, 11) | LocalDate.of(2013, 12, 28) | null                       | false    | 0.00                  | 0          | false          || 2030.00   || 0.00       || 2            || LocalDate.of(2014, 1, 4)
        LocalDate.of(1982, 8, 26)  | LocalDate.of(1983, 3, 6)   | LocalDate.of(1981, 8, 29)  | false    | 0.00                  | 11         | false          || 16990.00  || 0.00       || 2            || LocalDate.of(1983, 7, 6)
        LocalDate.of(2046, 2, 22)  | LocalDate.of(2046, 10, 16) | LocalDate.of(2046, 2, 14)  | false    | 113.03                | 7          | false          || 11436.97  || 0.00       || 2            || LocalDate.of(2046, 12, 16)
        LocalDate.of(1996, 2, 29)  | LocalDate.of(1996, 5, 10)  | LocalDate.of(1996, 1, 6)   | false    | 0.00                  | 4          | false          || 7470.00   || 0.00       || 2            || LocalDate.of(1996, 5, 17)
        LocalDate.of(2005, 11, 2)  | LocalDate.of(2006, 1, 21)  | null                       | false    | 0.00                  | 0          | false          || 2030.00   || 0.00       || 2            || LocalDate.of(2006, 1, 28)
        LocalDate.of(2043, 11, 7)  | LocalDate.of(2044, 10, 24) | LocalDate.of(2043, 10, 24) | false    | 0.00                  | 10         | false          || 15630.00  || 0.00       || 2            || LocalDate.of(2045, 2, 24)
        LocalDate.of(1974, 5, 1)   | LocalDate.of(1975, 4, 4)   | null                       | false    | 299.52                | 5          | false          || 8530.48   || 0.00       || 2            || LocalDate.of(1975, 6, 4)
        LocalDate.of(2051, 12, 1)  | LocalDate.of(2052, 12, 19) | LocalDate.of(2050, 12, 18) | false    | 0.00                  | 4          | false          || 7470.00   || 0.00       || 2            || LocalDate.of(2053, 4, 19)
        LocalDate.of(2050, 5, 21)  | LocalDate.of(2051, 4, 8)   | LocalDate.of(2049, 12, 14) | false    | 0.00                  | 10         | false          || 15630.00  || 0.00       || 2            || LocalDate.of(2051, 8, 8)
        LocalDate.of(2000, 11, 13) | LocalDate.of(2001, 7, 18)  | null                       | false    | 444.64                | 11         | false          || 16545.36  || 0.00       || 2            || LocalDate.of(2001, 9, 18)
        LocalDate.of(1991, 7, 15)  | LocalDate.of(1992, 7, 12)  | LocalDate.of(1991, 1, 10)  | false    | 0.00                  | 12         | false          || 18350.00  || 0.00       || 2            || LocalDate.of(1992, 11, 12)
        LocalDate.of(2021, 8, 16)  | LocalDate.of(2021, 9, 16)  | LocalDate.of(2021, 5, 24)  | false    | 951.03                | 5          | false          || 7878.97   || 0.00       || 0            || LocalDate.of(2021, 9, 23)
        LocalDate.of(1996, 3, 22)  | LocalDate.of(1997, 4, 13)  | LocalDate.of(1996, 3, 15)  | false    | 1463.25               | 1          | false          || 2125.00   || 1265.00    || 2            || LocalDate.of(1997, 8, 13)
        LocalDate.of(2011, 4, 14)  | LocalDate.of(2012, 2, 13)  | null                       | false    | 0.00                  | 4          | false          || 7470.00   || 0.00       || 2            || LocalDate.of(2012, 4, 13)
    }

    def "Tier 4 General - Check 'Inner London Borough'"() {

        expect:
        def response = maintenanceThresholdCalculator.calculateStudentUnionSabbaticalOfficer(inLondon, buildScalaBigDecimal(accommodationFeesPaid), dependants, courseStartDate, courseEndDate, buildScalaOption(originalCourseStartDate), dependantsOnly)

        assert (response._1 == buildScalaBigDecimal(threshold))

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate    | inLondon | accommodationFeesPaid | dependants | dependantsOnly || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(2016, 1, 21)  | LocalDate.of(2016, 9, 1)   | null                       | true     | 337.27                | 3          | false          || 7262.73   || 0.00       || 2            || LocalDate.of(2016, 11, 1)
        LocalDate.of(2015, 1, 6)   | LocalDate.of(2015, 6, 30)  | LocalDate.of(2014, 7, 4)   | true     | 0.00                  | 14         | false          || 26190.00  || 0.00       || 2            || LocalDate.of(2015, 8, 30)
        LocalDate.of(1981, 4, 28)  | LocalDate.of(1982, 2, 27)  | null                       | true     | 806.97                | 3          | false          || 6793.03   || 0.00       || 2            || LocalDate.of(1982, 4, 27)
        LocalDate.of(2046, 9, 16)  | LocalDate.of(2047, 2, 4)   | LocalDate.of(2046, 9, 7)   | true     | 303.25                | 14         | false          || 25886.75  || 0.00       || 2            || LocalDate.of(2047, 2, 11)
        LocalDate.of(2033, 12, 15) | LocalDate.of(2034, 1, 20)  | LocalDate.of(2033, 11, 22) | true     | 0.00                  | 6          | false          || 12670.00  || 0.00       || 0            || LocalDate.of(2034, 1, 27)
        LocalDate.of(2050, 2, 26)  | LocalDate.of(2050, 11, 17) | null                       | true     | 0.00                  | 10         | false          || 19430.00  || 0.00       || 2            || LocalDate.of(2051, 1, 17)
        LocalDate.of(1995, 9, 14)  | LocalDate.of(1996, 4, 24)  | null                       | true     | 755.81                | 13         | false          || 23744.19  || 0.00       || 2            || LocalDate.of(1996, 6, 24)
        LocalDate.of(1973, 1, 21)  | LocalDate.of(1973, 4, 30)  | LocalDate.of(1972, 9, 4)   | true     | 1101.12               | 7          | false          || 13258.88  || 0.00       || 2            || LocalDate.of(1973, 6, 30)
        LocalDate.of(2039, 5, 3)   | LocalDate.of(2040, 2, 6)   | null                       | true     | 1260.02               | 5          | false          || 9719.98   || 0.00       || 2            || LocalDate.of(2040, 4, 6)
        LocalDate.of(2039, 3, 7)   | LocalDate.of(2039, 3, 25)  | LocalDate.of(2038, 2, 25)  | true     | 1715.25               | 2          | false          || 3380.00   || 1265.00    || 0            || LocalDate.of(2039, 7, 25)
        LocalDate.of(1973, 11, 5)  | LocalDate.of(1974, 11, 23) | null                       | true     | 651.91                | 4          | false          || 8638.09   || 0.00       || 2            || LocalDate.of(1975, 3, 23)
        LocalDate.of(2044, 8, 12)  | LocalDate.of(2045, 9, 6)   | LocalDate.of(2044, 2, 15)  | true     | 0.00                  | 3          | false          || 7600.00   || 0.00       || 2            || LocalDate.of(2046, 1, 6)
        LocalDate.of(1999, 10, 22) | LocalDate.of(1999, 11, 13) | LocalDate.of(1999, 8, 24)  | true     | 0.00                  | 9          | false          || 8870.00   || 0.00       || 0            || LocalDate.of(1999, 11, 20)
        LocalDate.of(2013, 7, 14)  | LocalDate.of(2014, 7, 27)  | LocalDate.of(2012, 11, 16) | true     | 0.00                  | 8          | false          || 16050.00  || 0.00       || 2            || LocalDate.of(2014, 11, 27)
        LocalDate.of(2048, 11, 14) | LocalDate.of(2049, 4, 14)  | null                       | true     | 0.00                  | 0          | false          || 2530.00   || 0.00       || 2            || LocalDate.of(2049, 4, 21)
        LocalDate.of(2018, 9, 1)   | LocalDate.of(2019, 6, 1)   | LocalDate.of(2018, 5, 24)  | true     | 0.00                  | 6          | false          || 12670.00  || 0.00       || 2            || LocalDate.of(2019, 10, 1)
        LocalDate.of(1999, 9, 11)  | LocalDate.of(1999, 10, 31) | LocalDate.of(1999, 8, 15)  | true     | 0.00                  | 4          | false          || 9290.00   || 0.00       || 0            || LocalDate.of(1999, 11, 7)
        LocalDate.of(2030, 12, 24) | LocalDate.of(2031, 12, 2)  | LocalDate.of(2030, 5, 30)  | true     | 1253.19               | 9          | false          || 16486.81  || 0.00       || 2            || LocalDate.of(2032, 4, 2)
        LocalDate.of(2026, 9, 4)   | LocalDate.of(2027, 8, 17)  | null                       | true     | 1423.64               | 12         | false          || 21545.00  || 1265.00    || 2            || LocalDate.of(2027, 10, 17)
        LocalDate.of(1997, 11, 23) | LocalDate.of(1998, 12, 3)  | LocalDate.of(1997, 8, 3)   | true     | 1781.34               | 3          | false          || 6335.00   || 1265.00    || 2            || LocalDate.of(1999, 4, 3)
    }

    def "Tier 4 General - Check 'Accommodation Fees paid'"() {

        expect:
        def response = maintenanceThresholdCalculator.calculateStudentUnionSabbaticalOfficer(inLondon, buildScalaBigDecimal(accommodationFeesPaid), dependants, courseStartDate, courseEndDate, buildScalaOption(originalCourseStartDate), dependantsOnly)

        assert (response._1 == buildScalaBigDecimal(threshold))

        where:
        courseStartDate            | courseEndDate             | originalCourseStartDate    | inLondon | accommodationFeesPaid | dependants | dependantsOnly || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(1990, 4, 28)  | LocalDate.of(1990, 9, 14) | LocalDate.of(1989, 9, 24)  | false    | 1731.41               | 14         | false          || 19805.00  || 1265.00    || 2            || LocalDate.of(1990, 11, 14)
        LocalDate.of(1974, 11, 19) | LocalDate.of(1975, 3, 9)  | LocalDate.of(1973, 11, 20) | false    | 1311.13               | 13         | false          || 18445.00  || 1265.00    || 2            || LocalDate.of(1975, 7, 9)
        LocalDate.of(2016, 11, 21) | LocalDate.of(2017, 3, 9)  | LocalDate.of(2016, 10, 1)  | true     | 1112.14               | 13         | false          || 23387.86  || 0.00       || 2            || LocalDate.of(2017, 3, 16)
        LocalDate.of(2029, 8, 27)  | LocalDate.of(2030, 4, 3)  | null                       | true     | 1740.98               | 9          | false          || 16475.00  || 1265.00    || 2            || LocalDate.of(2030, 6, 3)
        LocalDate.of(2039, 4, 22)  | LocalDate.of(2040, 5, 16) | LocalDate.of(2039, 4, 7)   | true     | 1347.36               | 3          | false          || 6335.00   || 1265.00    || 2            || LocalDate.of(2040, 9, 16)
        LocalDate.of(2040, 4, 2)   | LocalDate.of(2041, 5, 6)  | null                       | false    | 610.11                | 12         | false          || 17739.89  || 0.00       || 2            || LocalDate.of(2041, 9, 6)
        LocalDate.of(2003, 6, 23)  | LocalDate.of(2003, 8, 18) | LocalDate.of(2002, 6, 16)  | true     | 545.28                | 14         | false          || 25644.72  || 0.00       || 0            || LocalDate.of(2003, 12, 18)
        LocalDate.of(2022, 8, 11)  | LocalDate.of(2022, 9, 24) | LocalDate.of(2022, 2, 16)  | false    | 569.45                | 8          | false          || 12340.55  || 0.00       || 0            || LocalDate.of(2022, 11, 24)
        LocalDate.of(2016, 1, 23)  | LocalDate.of(2016, 3, 1)  | LocalDate.of(2015, 12, 20) | true     | 1064.70               | 11         | false          || 20055.30  || 0.00       || 0            || LocalDate.of(2016, 3, 8)
        LocalDate.of(2045, 5, 4)   | LocalDate.of(2046, 5, 4)  | LocalDate.of(2044, 11, 15) | true     | 1172.28               | 3          | false          || 6427.72   || 0.00       || 2            || LocalDate.of(2046, 9, 4)
        LocalDate.of(2008, 3, 10)  | LocalDate.of(2009, 1, 19) | LocalDate.of(2007, 7, 24)  | true     | 1870.13               | 10         | false          || 18165.00  || 1265.00    || 2            || LocalDate.of(2009, 5, 19)
        LocalDate.of(2025, 11, 29) | LocalDate.of(2026, 7, 21) | null                       | true     | 177.36                | 9          | false          || 17562.64  || 0.00       || 2            || LocalDate.of(2026, 9, 21)
        LocalDate.of(2025, 4, 6)   | LocalDate.of(2025, 9, 11) | LocalDate.of(2024, 4, 10)  | false    | 244.87                | 10         | false          || 15385.13  || 0.00       || 2            || LocalDate.of(2026, 1, 11)
        LocalDate.of(2009, 4, 8)   | LocalDate.of(2010, 1, 2)  | null                       | true     | 1652.86               | 2          | false          || 4645.00   || 1265.00    || 2            || LocalDate.of(2010, 3, 2)
        LocalDate.of(2038, 10, 17) | LocalDate.of(2038, 12, 4) | LocalDate.of(2037, 12, 7)  | false    | 381.06                | 6          | false          || 9808.94   || 0.00       || 0            || LocalDate.of(2039, 2, 4)
        LocalDate.of(1983, 11, 3)  | LocalDate.of(1984, 6, 1)  | LocalDate.of(1983, 1, 12)  | false    | 195.69                | 1          | false          || 3194.31   || 0.00       || 2            || LocalDate.of(1984, 10, 1)
        LocalDate.of(2008, 1, 12)  | LocalDate.of(2008, 5, 18) | LocalDate.of(2007, 5, 27)  | true     | 399.06                | 9          | false          || 17340.94  || 0.00       || 2            || LocalDate.of(2008, 7, 18)
        LocalDate.of(2017, 9, 23)  | LocalDate.of(2018, 1, 18) | LocalDate.of(2017, 5, 15)  | false    | 342.87                | 2          | false          || 4407.13   || 0.00       || 2            || LocalDate.of(2018, 3, 18)
        LocalDate.of(2014, 8, 18)  | LocalDate.of(2015, 8, 20) | LocalDate.of(2014, 7, 29)  | false    | 978.82                | 10         | false          || 14651.18  || 0.00       || 2            || LocalDate.of(2015, 12, 20)
        LocalDate.of(2015, 6, 1)   | LocalDate.of(2015, 9, 20) | LocalDate.of(2015, 2, 4)   | true     | 275.55                | 8          | false          || 15774.45  || 0.00       || 2            || LocalDate.of(2015, 11, 20)
    }

    def "Tier 4 General - Check 'continuations'"() {

        expect:
        def response = maintenanceThresholdCalculator.calculateStudentUnionSabbaticalOfficer(inLondon, buildScalaBigDecimal(accommodationFeesPaid), dependants, courseStartDate, courseEndDate, buildScalaOption(originalCourseStartDate), dependantsOnly)

        def thresholdValue = response._1
        def cappedValues = DataUtils.getCappedValues(response._2)
        def cappedAccommodation = cappedValues.accommodationFeesPaid()
        def cappedCourseLength = cappedValues.courseLength()

        assert thresholdValue == buildScalaBigDecimal(threshold)
        assert DataUtils.compareAccommodationFees(buildScalaBigDecimal(feesCapped), cappedAccommodation) == true
        assert DataUtils.compareCourseLength(courseCapped, cappedCourseLength) == true

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate    | inLondon | accommodationFeesPaid | dependants | dependantsOnly || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(1985, 7, 25)  | LocalDate.of(1986, 7, 4)   | LocalDate.of(1984, 12, 29) | true     | 0.00                  | 3          | false          || 7600.00   || 0.00       || 2            || LocalDate.of(1986, 11, 4)
        LocalDate.of(1973, 8, 20)  | LocalDate.of(1974, 3, 10)  | LocalDate.of(1973, 4, 15)  | true     | 360.37                | 4          | false          || 8929.63   || 0.00       || 2            || LocalDate.of(1974, 5, 10)
        LocalDate.of(1997, 5, 28)  | LocalDate.of(1998, 3, 28)  | LocalDate.of(1996, 9, 24)  | false    | 1622.77               | 9          | false          || 13005.00  || 1265.00    || 2            || LocalDate.of(1998, 7, 28)
        LocalDate.of(1976, 12, 4)  | LocalDate.of(1977, 12, 18) | LocalDate.of(1976, 4, 29)  | false    | 1647.38               | 14         | false          || 19805.00  || 1265.00    || 2            || LocalDate.of(1978, 4, 18)
        LocalDate.of(2045, 1, 24)  | LocalDate.of(2046, 2, 25)  | LocalDate.of(2044, 11, 29) | true     | 0.00                  | 5          | false          || 10980.00  || 0.00       || 2            || LocalDate.of(2046, 6, 25)
        LocalDate.of(2045, 1, 27)  | LocalDate.of(2045, 6, 26)  | LocalDate.of(2044, 7, 14)  | true     | 1493.66               | 10         | false          || 18165.00  || 1265.00    || 2            || LocalDate.of(2045, 8, 26)
        LocalDate.of(2020, 12, 13) | LocalDate.of(2021, 6, 25)  | LocalDate.of(2020, 11, 6)  | false    | 1171.60               | 9          | false          || 13098.40  || 0.00       || 2            || LocalDate.of(2021, 8, 25)
        LocalDate.of(1988, 10, 18) | LocalDate.of(1989, 5, 4)   | LocalDate.of(1988, 3, 20)  | true     | 0.00                  | 3          | false          || 7600.00   || 0.00       || 2            || LocalDate.of(1989, 9, 4)
        LocalDate.of(2011, 10, 30) | LocalDate.of(2012, 4, 6)   | LocalDate.of(2011, 6, 23)  | false    | 0.00                  | 7          | false          || 11550.00  || 0.00       || 2            || LocalDate.of(2012, 6, 6)
        LocalDate.of(1986, 9, 24)  | LocalDate.of(1987, 5, 4)   | LocalDate.of(1985, 8, 27)  | true     | 842.13                | 11         | false          || 20277.87  || 0.00       || 2            || LocalDate.of(1987, 9, 4)
        LocalDate.of(1991, 4, 6)   | LocalDate.of(1991, 7, 7)   | LocalDate.of(1990, 11, 6)  | true     | 0.00                  | 9          | false          || 17740.00  || 0.00       || 2            || LocalDate.of(1991, 9, 7)
        LocalDate.of(1996, 9, 9)   | LocalDate.of(1997, 2, 13)  | LocalDate.of(1995, 8, 17)  | false    | 193.63                | 1          | false          || 3196.37   || 0.00       || 2            || LocalDate.of(1997, 6, 13)
        LocalDate.of(2053, 11, 4)  | LocalDate.of(2054, 12, 5)  | LocalDate.of(2052, 12, 24) | true     | 0.00                  | 5          | false          || 10980.00  || 0.00       || 2            || LocalDate.of(2055, 4, 5)
        LocalDate.of(2019, 12, 10) | LocalDate.of(2020, 8, 30)  | LocalDate.of(2019, 4, 29)  | true     | 0.00                  | 3          | false          || 7600.00   || 0.00       || 2            || LocalDate.of(2020, 12, 30)
        LocalDate.of(2029, 9, 20)  | LocalDate.of(2029, 12, 26) | LocalDate.of(2029, 4, 24)  | true     | 0.00                  | 9          | false          || 17740.00  || 0.00       || 2            || LocalDate.of(2030, 2, 26)
        LocalDate.of(1999, 4, 15)  | LocalDate.of(1999, 12, 12) | LocalDate.of(1998, 12, 22) | true     | 250.51                | 13         | false          || 24249.49  || 0.00       || 2            || LocalDate.of(2000, 2, 12)
        LocalDate.of(2035, 10, 10) | LocalDate.of(2036, 6, 1)   | LocalDate.of(2035, 1, 7)   | true     | 1941.51               | 9          | false          || 16475.00  || 1265.00    || 2            || LocalDate.of(2036, 10, 1)
        LocalDate.of(2051, 10, 1)  | LocalDate.of(2052, 4, 20)  | LocalDate.of(2051, 7, 16)  | true     | 879.90                | 13         | false          || 23620.10  || 0.00       || 2            || LocalDate.of(2052, 6, 20)
        LocalDate.of(2000, 5, 29)  | LocalDate.of(2000, 11, 30) | LocalDate.of(2000, 1, 18)  | false    | 1763.52               | 10         | false          || 14365.00  || 1265.00    || 2            || LocalDate.of(2001, 1, 30)
        LocalDate.of(2028, 5, 15)  | LocalDate.of(2028, 11, 12) | LocalDate.of(2027, 9, 24)  | true     | 0.00                  | 11         | false          || 21120.00  || 0.00       || 2            || LocalDate.of(2029, 3, 12)
    }

    // Dependants only

    def "Tier 4 General - Check 'Non Inner London Borough' (dependants only)"() {

        expect:
        def response = maintenanceThresholdCalculator.calculateStudentUnionSabbaticalOfficer(inLondon, buildScalaBigDecimal(accommodationFeesPaid), dependants, courseStartDate, courseEndDate, buildScalaOption(originalCourseStartDate), dependantsOnly)

        assert (response._1 == buildScalaBigDecimal(threshold))

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate    | inLondon | accommodationFeesPaid | dependants | dependantsOnly || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(1983, 5, 22)  | LocalDate.of(1983, 12, 2)  | LocalDate.of(1983, 4, 3)   | false    | 564.46                | 13         | true           || 17680.00  || 0.00       || 2            || LocalDate.of(1984, 2, 2)
        LocalDate.of(2035, 1, 25)  | LocalDate.of(2035, 7, 26)  | LocalDate.of(2034, 7, 8)   | false    | 0.00                  | 10         | true           || 13600.00  || 0.00       || 2            || LocalDate.of(2035, 11, 26)
        LocalDate.of(2005, 11, 11) | LocalDate.of(2006, 6, 16)  | LocalDate.of(2005, 9, 12)  | false    | 493.13                | 9          | true           || 12240.00  || 0.00       || 2            || LocalDate.of(2006, 8, 16)
        LocalDate.of(1995, 8, 11)  | LocalDate.of(1995, 10, 3)  | LocalDate.of(1994, 8, 31)  | false    | 0.00                  | 11         | true           || 14960.00  || 0.00       || 0            || LocalDate.of(1996, 2, 3)
        LocalDate.of(2016, 11, 27) | LocalDate.of(2017, 1, 5)   | LocalDate.of(2016, 5, 15)  | false    | 0.00                  | 13         | true           || 17680.00  || 0.00       || 0            || LocalDate.of(2017, 3, 5)
        LocalDate.of(2001, 2, 6)   | LocalDate.of(2001, 8, 28)  | null                       | false    | 0.00                  | 12         | true           || 16320.00  || 0.00       || 2            || LocalDate.of(2001, 10, 28)
        LocalDate.of(2045, 11, 4)  | LocalDate.of(2046, 5, 19)  | LocalDate.of(2045, 7, 2)   | false    | 0.00                  | 10         | true           || 13600.00  || 0.00       || 2            || LocalDate.of(2046, 7, 19)
        LocalDate.of(1974, 10, 12) | LocalDate.of(1974, 11, 21) | null                       | false    | 0.00                  | 0          | true           || 0.00      || 0.00       || 0            || LocalDate.of(1974, 11, 28)
        LocalDate.of(2007, 3, 12)  | LocalDate.of(2007, 6, 10)  | LocalDate.of(2007, 1, 11)  | false    | 0.00                  | 12         | true           || 16320.00  || 0.00       || 2            || LocalDate.of(2007, 6, 17)
        LocalDate.of(2007, 4, 2)   | LocalDate.of(2007, 6, 14)  | LocalDate.of(2007, 2, 20)  | false    | 234.94                | 10         | true           || 13600.00  || 0.00       || 2            || LocalDate.of(2007, 6, 21)
        LocalDate.of(1973, 8, 10)  | LocalDate.of(1973, 11, 9)  | LocalDate.of(1973, 3, 9)   | false    | 0.00                  | 3          | true           || 4080.00   || 0.00       || 2            || LocalDate.of(1974, 1, 9)
        LocalDate.of(1992, 6, 12)  | LocalDate.of(1992, 10, 2)  | null                       | false    | 971.43                | 0          | true           || 0.00      || 0.00       || 2            || LocalDate.of(1992, 10, 9)
        LocalDate.of(2033, 8, 28)  | LocalDate.of(2034, 9, 29)  | LocalDate.of(2033, 4, 21)  | false    | 0.00                  | 9          | true           || 12240.00  || 0.00       || 2            || LocalDate.of(2035, 1, 29)
        LocalDate.of(1986, 10, 26) | LocalDate.of(1987, 9, 14)  | LocalDate.of(1986, 5, 7)   | false    | 824.36                | 11         | true           || 14960.00  || 0.00       || 2            || LocalDate.of(1988, 1, 14)
        LocalDate.of(2050, 9, 17)  | LocalDate.of(2050, 11, 3)  | LocalDate.of(2049, 10, 19) | false    | 492.71                | 14         | true           || 19040.00  || 0.00       || 0            || LocalDate.of(2051, 3, 3)
        LocalDate.of(2041, 4, 13)  | LocalDate.of(2041, 8, 23)  | LocalDate.of(2040, 12, 29) | false    | 994.15                | 14         | true           || 19040.00  || 0.00       || 2            || LocalDate.of(2041, 10, 23)
        LocalDate.of(2050, 4, 17)  | LocalDate.of(2050, 12, 9)  | LocalDate.of(2050, 1, 9)   | false    | 1628.71               | 5          | true           || 6800.00   || 1265.00    || 2            || LocalDate.of(2051, 2, 9)
        LocalDate.of(1977, 7, 14)  | LocalDate.of(1977, 10, 12) | LocalDate.of(1976, 9, 11)  | false    | 1343.97               | 9          | true           || 12240.00  || 1265.00    || 2            || LocalDate.of(1978, 2, 12)
        LocalDate.of(1993, 10, 17) | LocalDate.of(1994, 6, 11)  | LocalDate.of(1993, 7, 7)   | false    | 0.00                  | 2          | true           || 2720.00   || 0.00       || 2            || LocalDate.of(1994, 8, 11)
        LocalDate.of(2044, 8, 1)   | LocalDate.of(2045, 2, 27)  | LocalDate.of(2043, 8, 7)   | false    | 1816.55               | 0          | true           || 0.00      || 1265.00    || 2            || LocalDate.of(2045, 6, 27)
    }

    def "Tier 4 General - Check 'Inner London Borough' (dependants only)"() {

        expect:
        def response = maintenanceThresholdCalculator.calculateStudentUnionSabbaticalOfficer(inLondon, buildScalaBigDecimal(accommodationFeesPaid), dependants, courseStartDate, courseEndDate, buildScalaOption(originalCourseStartDate), dependantsOnly)

        assert (response._1 == buildScalaBigDecimal(threshold))

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate    | inLondon | accommodationFeesPaid | dependants | dependantsOnly || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(1995, 3, 3)   | LocalDate.of(1995, 10, 30) | LocalDate.of(1994, 8, 15)  | true     | 0.00                  | 11         | true           || 18590.00  || 0.00       || 2            || LocalDate.of(1996, 2, 29)
        LocalDate.of(1997, 11, 13) | LocalDate.of(1998, 7, 28)  | LocalDate.of(1997, 5, 29)  | true     | 328.30                | 14         | true           || 23660.00  || 0.00       || 2            || LocalDate.of(1998, 11, 28)
        LocalDate.of(2032, 7, 26)  | LocalDate.of(2032, 11, 11) | null                       | true     | 1866.46               | 0          | true           || 0.00      || 1265.00    || 2            || LocalDate.of(2032, 11, 18)
        LocalDate.of(1978, 11, 19) | LocalDate.of(1979, 7, 30)  | null                       | true     | 1097.25               | 12         | true           || 20280.00  || 0.00       || 2            || LocalDate.of(1979, 9, 30)
        LocalDate.of(2035, 9, 20)  | LocalDate.of(2036, 7, 7)   | null                       | true     | 914.82                | 11         | true           || 18590.00  || 0.00       || 2            || LocalDate.of(2036, 9, 7)
        LocalDate.of(1981, 7, 23)  | LocalDate.of(1981, 12, 23) | null                       | true     | 0.00                  | 0          | true           || 0.00      || 0.00       || 2            || LocalDate.of(1981, 12, 30)
        LocalDate.of(2017, 12, 21) | LocalDate.of(2018, 2, 6)   | LocalDate.of(2016, 11, 29) | true     | 653.92                | 8          | true           || 13520.00  || 0.00       || 0            || LocalDate.of(2018, 6, 6)
        LocalDate.of(2011, 1, 3)   | LocalDate.of(2011, 12, 9)  | LocalDate.of(2010, 12, 17) | true     | 0.00                  | 2          | true           || 3380.00   || 0.00       || 2            || LocalDate.of(2012, 2, 9)
        LocalDate.of(2033, 8, 14)  | LocalDate.of(2034, 3, 25)  | null                       | true     | 443.52                | 9          | true           || 15210.00  || 0.00       || 2            || LocalDate.of(2034, 5, 25)
        LocalDate.of(1978, 7, 6)   | LocalDate.of(1979, 4, 20)  | LocalDate.of(1978, 1, 10)  | true     | 0.00                  | 9          | true           || 15210.00  || 0.00       || 2            || LocalDate.of(1979, 8, 20)
        LocalDate.of(2008, 1, 24)  | LocalDate.of(2008, 8, 3)   | LocalDate.of(2007, 4, 23)  | true     | 747.70                | 12         | true           || 20280.00  || 0.00       || 2            || LocalDate.of(2008, 12, 3)
        LocalDate.of(2047, 9, 16)  | LocalDate.of(2048, 8, 21)  | LocalDate.of(2046, 12, 4)  | true     | 0.00                  | 7          | true           || 11830.00  || 0.00       || 2            || LocalDate.of(2048, 12, 21)
        LocalDate.of(2039, 12, 8)  | LocalDate.of(2040, 5, 16)  | null                       | true     | 0.00                  | 0          | true           || 0.00      || 0.00       || 2            || LocalDate.of(2040, 5, 23)
        LocalDate.of(2013, 4, 30)  | LocalDate.of(2014, 1, 19)  | LocalDate.of(2012, 7, 19)  | true     | 1534.49               | 11         | true           || 18590.00  || 1265.00    || 2            || LocalDate.of(2014, 5, 19)
        LocalDate.of(1987, 6, 27)  | LocalDate.of(1987, 11, 15) | LocalDate.of(1987, 2, 9)   | true     | 35.51                 | 2          | true           || 3380.00   || 0.00       || 2            || LocalDate.of(1988, 1, 15)
        LocalDate.of(2030, 1, 27)  | LocalDate.of(2030, 7, 28)  | LocalDate.of(2029, 4, 30)  | true     | 0.00                  | 4          | true           || 6760.00   || 0.00       || 2            || LocalDate.of(2030, 11, 28)
        LocalDate.of(1999, 7, 30)  | LocalDate.of(2000, 7, 28)  | LocalDate.of(1998, 12, 10) | true     | 0.00                  | 6          | true           || 10140.00  || 0.00       || 2            || LocalDate.of(2000, 11, 28)
        LocalDate.of(1995, 4, 17)  | LocalDate.of(1995, 7, 7)   | LocalDate.of(1994, 9, 28)  | true     | 1324.84               | 2          | true           || 3380.00   || 1265.00    || 2            || LocalDate.of(1995, 9, 7)
        LocalDate.of(1977, 4, 21)  | LocalDate.of(1978, 5, 26)  | LocalDate.of(1976, 9, 21)  | true     | 0.00                  | 2          | true           || 3380.00   || 0.00       || 2            || LocalDate.of(1978, 9, 26)
        LocalDate.of(2006, 5, 30)  | LocalDate.of(2006, 7, 5)   | LocalDate.of(2006, 1, 4)   | true     | 0.77                  | 2          | true           || 3380.00   || 0.00       || 0            || LocalDate.of(2006, 9, 5)
    }

    def "Tier 4 General - Check 'continuations' (dependants only)"() {

        expect:
        def response = maintenanceThresholdCalculator.calculateStudentUnionSabbaticalOfficer(inLondon, buildScalaBigDecimal(accommodationFeesPaid), dependants, courseStartDate, courseEndDate, buildScalaOption(originalCourseStartDate), dependantsOnly)

        def thresholdValue = response._1
        def cappedValues = DataUtils.getCappedValues(response._2)
        def cappedAccommodation = cappedValues.accommodationFeesPaid()
        def cappedCourseLength = cappedValues.courseLength()

        assert thresholdValue == buildScalaBigDecimal(threshold)
        assert DataUtils.compareAccommodationFees(buildScalaBigDecimal(feesCapped), cappedAccommodation) == true
        assert DataUtils.compareCourseLength(courseCapped, cappedCourseLength) == true

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate    | inLondon | accommodationFeesPaid | dependants | dependantsOnly || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(2013, 7, 18)  | LocalDate.of(2013, 12, 12) | LocalDate.of(2013, 5, 27)  | true     | 0.00                  | 8          | true           || 13520.00  || 0.00       || 2            || LocalDate.of(2014, 2, 12)
        LocalDate.of(2019, 2, 7)   | LocalDate.of(2020, 1, 20)  | LocalDate.of(2018, 7, 25)  | false    | 216.81                | 4          | true           || 5440.00   || 0.00       || 2            || LocalDate.of(2020, 5, 20)
        LocalDate.of(1981, 12, 28) | LocalDate.of(1982, 11, 12) | LocalDate.of(1981, 7, 7)   | false    | 1156.63               | 8          | true           || 10880.00  || 0.00       || 2            || LocalDate.of(1983, 3, 12)
        LocalDate.of(2049, 2, 6)   | LocalDate.of(2049, 3, 13)  | LocalDate.of(2048, 6, 9)   | true     | 0.00                  | 2          | true           || 3380.00   || 0.00       || 0            || LocalDate.of(2049, 5, 13)
        LocalDate.of(2042, 12, 24) | LocalDate.of(2043, 11, 20) | LocalDate.of(2042, 2, 6)   | false    | 770.69                | 7          | true           || 9520.00   || 0.00       || 2            || LocalDate.of(2044, 3, 20)
        LocalDate.of(2000, 10, 9)  | LocalDate.of(2001, 5, 25)  | LocalDate.of(2000, 4, 3)   | false    | 0.00                  | 11         | true           || 14960.00  || 0.00       || 2            || LocalDate.of(2001, 9, 25)
        LocalDate.of(1981, 5, 21)  | LocalDate.of(1981, 6, 5)   | LocalDate.of(1980, 9, 16)  | true     | 0.00                  | 2          | true           || 3380.00   || 0.00       || 0            || LocalDate.of(1981, 8, 5)
        LocalDate.of(1999, 8, 26)  | LocalDate.of(2000, 9, 17)  | LocalDate.of(1999, 7, 1)   | false    | 0.00                  | 13         | true           || 17680.00  || 0.00       || 2            || LocalDate.of(2001, 1, 17)
        LocalDate.of(2026, 7, 15)  | LocalDate.of(2026, 8, 13)  | LocalDate.of(2026, 5, 11)  | true     | 1743.76               | 0          | true           || 0.00      || 1265.00    || 0            || LocalDate.of(2026, 8, 20)
        LocalDate.of(2010, 9, 17)  | LocalDate.of(2010, 11, 1)  | LocalDate.of(2009, 10, 31) | true     | 0.00                  | 9          | true           || 15210.00  || 0.00       || 0            || LocalDate.of(2011, 3, 1)
        LocalDate.of(2054, 5, 15)  | LocalDate.of(2054, 11, 29) | LocalDate.of(2053, 7, 20)  | true     | 0.00                  | 2          | true           || 3380.00   || 0.00       || 2            || LocalDate.of(2055, 3, 29)
        LocalDate.of(2020, 9, 27)  | LocalDate.of(2021, 4, 17)  | LocalDate.of(2019, 10, 23) | false    | 0.00                  | 12         | true           || 16320.00  || 0.00       || 2            || LocalDate.of(2021, 8, 17)
        LocalDate.of(2024, 11, 18) | LocalDate.of(2025, 4, 26)  | LocalDate.of(2024, 5, 21)  | true     | 1920.50               | 3          | true           || 5070.00   || 1265.00    || 2            || LocalDate.of(2025, 6, 26)
        LocalDate.of(2034, 10, 30) | LocalDate.of(2035, 6, 1)   | LocalDate.of(2034, 4, 20)  | false    | 0.00                  | 12         | true           || 16320.00  || 0.00       || 2            || LocalDate.of(2035, 10, 1)
        LocalDate.of(2053, 6, 18)  | LocalDate.of(2054, 2, 14)  | LocalDate.of(2052, 10, 25) | false    | 0.00                  | 10         | true           || 13600.00  || 0.00       || 2            || LocalDate.of(2054, 6, 14)
        LocalDate.of(2042, 4, 1)   | LocalDate.of(2042, 10, 2)  | LocalDate.of(2041, 8, 15)  | true     | 0.00                  | 4          | true           || 6760.00   || 0.00       || 2            || LocalDate.of(2043, 2, 2)
        LocalDate.of(1983, 7, 2)   | LocalDate.of(1983, 10, 10) | LocalDate.of(1983, 4, 26)  | false    | 1127.03               | 11         | true           || 14960.00  || 0.00       || 2            || LocalDate.of(1983, 10, 17)
        LocalDate.of(2029, 11, 19) | LocalDate.of(2030, 4, 6)   | LocalDate.of(2029, 11, 14) | true     | 1083.22               | 6          | true           || 10140.00  || 0.00       || 2            || LocalDate.of(2030, 4, 13)
        LocalDate.of(2037, 1, 17)  | LocalDate.of(2037, 9, 29)  | LocalDate.of(2036, 1, 12)  | false    | 0.00                  | 9          | true           || 12240.00  || 0.00       || 2            || LocalDate.of(2038, 1, 29)
        LocalDate.of(2009, 8, 19)  | LocalDate.of(2010, 1, 29)  | LocalDate.of(2009, 1, 30)  | false    | 0.00                  | 9          | true           || 12240.00  || 0.00       || 2            || LocalDate.of(2010, 5, 29)
    }

    // All variants

    def "Tier 4 General - Check 'All variants'"() {

        def response = maintenanceThresholdCalculator.calculateStudentUnionSabbaticalOfficer(inLondon, buildScalaBigDecimal(accommodationFeesPaid), dependants, courseStartDate, courseEndDate, buildScalaOption(originalCourseStartDate), dependantsOnly)

        def thresholdValue = response._1
        def cappedValues = DataUtils.getCappedValues(response._2)
        def cappedAccommodation = cappedValues.accommodationFeesPaid()
        def cappedCourseLength = cappedValues.courseLength()

        assert thresholdValue == buildScalaBigDecimal(threshold)
        assert DataUtils.compareAccommodationFees(buildScalaBigDecimal(feesCapped), cappedAccommodation) == true
        assert DataUtils.compareCourseLength(courseCapped, cappedCourseLength) == true

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate    | inLondon | accommodationFeesPaid | dependants | dependantsOnly || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(1991, 12, 23) | LocalDate.of(1992, 8, 15)  | LocalDate.of(1991, 10, 4)  | true     | 203.97                | 0          | true           || 0.00      || 0.00       || 2            || LocalDate.of(1992, 10, 15)
        LocalDate.of(1994, 10, 5)  | LocalDate.of(1995, 6, 12)  | LocalDate.of(1994, 5, 1)   | false    | 1911.39               | 14         | true           || 19040.00  || 1265.00    || 2            || LocalDate.of(1995, 10, 12)
        LocalDate.of(1986, 11, 9)  | LocalDate.of(1987, 5, 23)  | LocalDate.of(1985, 12, 11) | false    | 0.00                  | 5          | false          || 8830.00   || 0.00       || 2            || LocalDate.of(1987, 9, 23)
        LocalDate.of(2053, 12, 3)  | LocalDate.of(2054, 6, 2)   | null                       | true     | 1474.63               | 0          | true           || 0.00      || 1265.00    || 2            || LocalDate.of(2054, 8, 2)
        LocalDate.of(1973, 4, 9)   | LocalDate.of(1974, 2, 15)  | null                       | false    | 0.00                  | 8          | true           || 10880.00  || 0.00       || 2            || LocalDate.of(1974, 4, 15)
        LocalDate.of(1997, 5, 2)   | LocalDate.of(1997, 12, 10) | LocalDate.of(1997, 4, 16)  | true     | 1808.86               | 7          | false          || 13095.00  || 1265.00    || 2            || LocalDate.of(1998, 2, 10)
        LocalDate.of(2037, 12, 18) | LocalDate.of(2038, 9, 19)  | LocalDate.of(2037, 7, 4)   | true     | 0.00                  | 12         | false          || 22810.00  || 0.00       || 2            || LocalDate.of(2039, 1, 19)
        LocalDate.of(2012, 10, 3)  | LocalDate.of(2012, 10, 11) | null                       | true     | 1685.58               | 0          | false          || 0.00      || 1265.00    || 0            || LocalDate.of(2012, 10, 18)
        LocalDate.of(2047, 4, 4)   | LocalDate.of(2048, 3, 4)   | LocalDate.of(2046, 5, 29)  | false    | 396.87                | 7          | true           || 9520.00   || 0.00       || 2            || LocalDate.of(2048, 7, 4)
        LocalDate.of(2019, 6, 5)   | LocalDate.of(2019, 9, 21)  | null                       | false    | 0.00                  | 0          | true           || 0.00      || 0.00       || 2            || LocalDate.of(2019, 9, 28)
        LocalDate.of(2004, 6, 17)  | LocalDate.of(2005, 6, 29)  | LocalDate.of(2003, 12, 1)  | false    | 32.38                 | 6          | false          || 10157.62  || 0.00       || 2            || LocalDate.of(2005, 10, 29)
        LocalDate.of(2008, 8, 4)   | LocalDate.of(2008, 12, 11) | LocalDate.of(2008, 4, 14)  | false    | 799.43                | 11         | true           || 14960.00  || 0.00       || 2            || LocalDate.of(2009, 2, 11)
        LocalDate.of(2010, 1, 16)  | LocalDate.of(2010, 10, 19) | LocalDate.of(2009, 6, 12)  | false    | 0.00                  | 8          | false          || 12910.00  || 0.00       || 2            || LocalDate.of(2011, 2, 19)
        LocalDate.of(2016, 8, 5)   | LocalDate.of(2017, 7, 22)  | LocalDate.of(2016, 5, 14)  | true     | 992.03                | 9          | false          || 16747.97  || 0.00       || 2            || LocalDate.of(2017, 11, 22)
        LocalDate.of(2006, 1, 4)   | LocalDate.of(2007, 1, 21)  | LocalDate.of(2005, 4, 26)  | false    | 0.00                  | 10         | true           || 13600.00  || 0.00       || 2            || LocalDate.of(2007, 5, 21)
        LocalDate.of(2020, 4, 23)  | LocalDate.of(2021, 2, 28)  | LocalDate.of(2020, 4, 1)   | true     | 0.00                  | 9          | true           || 15210.00  || 0.00       || 2            || LocalDate.of(2021, 4, 28)
        LocalDate.of(2019, 9, 14)  | LocalDate.of(2020, 7, 28)  | LocalDate.of(2019, 3, 23)  | true     | 0.00                  | 0          | true           || 0.00      || 0.00       || 2            || LocalDate.of(2020, 11, 28)
        LocalDate.of(2006, 6, 17)  | LocalDate.of(2006, 9, 24)  | LocalDate.of(2005, 10, 19) | true     | 0.00                  | 12         | false          || 22810.00  || 0.00       || 2            || LocalDate.of(2006, 11, 24)
        LocalDate.of(2052, 8, 15)  | LocalDate.of(2052, 9, 10)  | LocalDate.of(2052, 7, 12)  | true     | 1937.87               | 6          | false          || 10140.00  || 1265.00    || 0            || LocalDate.of(2052, 9, 17)
        LocalDate.of(1994, 9, 10)  | LocalDate.of(1995, 10, 9)  | LocalDate.of(1994, 4, 21)  | false    | 1765.05               | 2          | true           || 2720.00   || 1265.00    || 2            || LocalDate.of(1996, 2, 9)
    }

}
