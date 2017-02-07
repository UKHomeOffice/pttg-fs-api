package uk.gov.digital.ho.proving.financialstatus.api.test.tier4

import spock.lang.Specification
import uk.gov.digital.ho.proving.financialstatus.api.test.DataUtils
import uk.gov.digital.ho.proving.financialstatus.domain.MaintenanceThresholdCalculatorT4

import java.time.LocalDate

import static uk.gov.digital.ho.proving.financialstatus.api.test.DataUtils.buildScalaBigDecimal
import static uk.gov.digital.ho.proving.financialstatus.api.test.DataUtils.buildScalaOption


class GeneralMaintenanceThresholdCalculatorTest extends Specification {

    MaintenanceThresholdCalculatorT4 maintenanceThresholdCalculator = TestUtilsTier4.maintenanceThresholdServiceBuilder()


    def "Tier 4 General - Check 'Non Inner London Borough'"() {

        expect:
        def response = maintenanceThresholdCalculator.calculateGeneral(inLondon, buildScalaBigDecimal(tuitionFees), buildScalaBigDecimal(tuitionFeesPaid),
            buildScalaBigDecimal(accommodationFeesPaid), dependants, courseStartDate, courseEndDate, buildScalaOption(originalCourseStartDate),
            courseType == "pre-sessional", dependantsOnly)

        assert (response._1 == buildScalaBigDecimal(threshold))

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate    | inLondon | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants | courseType      | dependantsOnly || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(2033, 1, 3)   | LocalDate.of(2034, 1, 15)  | LocalDate.of(2032, 3, 9)   | false    | 6992.36     | 0.00            | 1025.86               | 0          | "main"          | false          || 15101.50  || 0.00       || 9            || LocalDate.of(2034, 5, 15)
        LocalDate.of(1988, 10, 9)  | LocalDate.of(1988, 10, 18) | LocalDate.of(1987, 10, 22) | false    | 3421.95     | 0.00            | 1365.73               | 1          | "main"          | false          || 5211.95   || 1265.00    || 0            || LocalDate.of(1988, 12, 18)
        LocalDate.of(2005, 7, 19)  | LocalDate.of(2006, 6, 23)  | LocalDate.of(2004, 7, 25)  | false    | 0.00        | 0.00            | 0.00                  | 6          | "main"          | false          || 45855.00  || 0.00       || 9            || LocalDate.of(2006, 10, 23)
        LocalDate.of(2016, 8, 31)  | LocalDate.of(2017, 6, 12)  | null                       | false    | 61.09       | 0.00            | 0.00                  | 4          | "main"          | false          || 33676.09  || 0.00       || 9            || LocalDate.of(2017, 8, 12)
        LocalDate.of(1996, 4, 14)  | LocalDate.of(1997, 3, 5)   | null                       | false    | 4256.83     | 6192.79         | 728.50                | 13         | "pre-sessional" | false          || 87966.50  || 0.00       || 9            || LocalDate.of(1997, 5, 5)
        LocalDate.of(2002, 11, 5)  | LocalDate.of(2003, 5, 30)  | null                       | false    | 8665.83     | 9252.73         | 1441.15               | 5          | "main"          | false          || 36440.00  || 1265.00    || 0            || LocalDate.of(2003, 7, 30)
        LocalDate.of(1985, 10, 28) | LocalDate.of(1986, 3, 9)   | LocalDate.of(1985, 6, 25)  | false    | 9332.59     | 7284.55         | 0.00                  | 11         | "main"          | false          || 59483.04  || 0.00       || 0            || LocalDate.of(1986, 5, 9)
        LocalDate.of(2005, 1, 8)   | LocalDate.of(2006, 2, 7)   | null                       | false    | 726.09      | 376.43          | 0.00                  | 14         | "pre-sessional" | false          || 95164.66  || 0.00       || 9            || LocalDate.of(2006, 6, 7)
        LocalDate.of(2025, 12, 13) | LocalDate.of(2026, 11, 6)  | LocalDate.of(2024, 11, 19) | false    | 7608.60     | 283.88          | 0.00                  | 6          | "main"          | false          || 53179.72  || 0.00       || 9            || LocalDate.of(2027, 3, 6)
        LocalDate.of(1973, 10, 20) | LocalDate.of(1974, 3, 10)  | null                       | false    | 7828.86     | 0.00            | 0.00                  | 0          | "pre-sessional" | false          || 12903.86  || 0.00       || 0            || LocalDate.of(1974, 4, 10)
        LocalDate.of(2023, 12, 22) | LocalDate.of(2024, 6, 16)  | null                       | false    | 1278.84     | 0.00            | 1729.65               | 0          | "pre-sessional" | false          || 6103.84   || 1265.00    || 0            || LocalDate.of(2024, 7, 16)
        LocalDate.of(1981, 3, 21)  | LocalDate.of(1982, 1, 8)   | LocalDate.of(1980, 11, 16) | false    | 2576.03     | 3586.32         | 396.94                | 14         | "main"          | false          || 94418.06  || 0.00       || 9            || LocalDate.of(1982, 5, 8)
        LocalDate.of(2037, 12, 12) | LocalDate.of(2038, 12, 13) | LocalDate.of(2037, 7, 25)  | false    | 1386.79     | 2411.51         | 0.00                  | 12         | "main"          | false          || 82575.00  || 0.00       || 9            || LocalDate.of(2039, 4, 13)
        LocalDate.of(1974, 8, 18)  | LocalDate.of(1975, 3, 16)  | LocalDate.of(1974, 1, 28)  | false    | 2238.76     | 4167.23         | 0.00                  | 13         | "main"          | false          || 86665.00  || 0.00       || 0            || LocalDate.of(1975, 7, 16)
        LocalDate.of(1993, 9, 4)   | LocalDate.of(1994, 5, 14)  | null                       | false    | 6193.07     | 8204.09         | 165.31                | 10         | "main"          | false          || 70169.69  || 0.00       || 0            || LocalDate.of(1994, 7, 14)
        LocalDate.of(1997, 1, 11)  | LocalDate.of(1997, 2, 25)  | null                       | false    | 427.84      | 4654.58         | 0.00                  | 0          | "main"          | false          || 2030.00   || 0.00       || 0            || LocalDate.of(1997, 3, 4)
        LocalDate.of(1980, 6, 8)   | LocalDate.of(1980, 7, 5)   | LocalDate.of(1979, 8, 6)   | false    | 7467.72     | 0.00            | 0.00                  | 2          | "main"          | false          || 12562.72  || 0.00       || 0            || LocalDate.of(1980, 9, 5)
        LocalDate.of(2036, 6, 1)   | LocalDate.of(2037, 3, 28)  | null                       | false    | 7140.53     | 0.00            | 0.00                  | 11         | "pre-sessional" | false          || 83595.53  || 0.00       || 9            || LocalDate.of(2037, 5, 28)
        LocalDate.of(2009, 8, 24)  | LocalDate.of(2009, 10, 2)  | LocalDate.of(2009, 3, 22)  | false    | 4334.98     | 0.00            | 893.97                | 11         | "main"          | false          || 35391.01  || 0.00       || 0            || LocalDate.of(2009, 12, 2)
        LocalDate.of(1994, 4, 27)  | LocalDate.of(1994, 11, 1)  | null                       | false    | 0.00        | 0.00            | 1262.13               | 2          | "pre-sessional" | false          || 18082.87  || 0.00       || 0            || LocalDate.of(1995, 1, 1)
    }

    def "Tier 4 General - Check 'Inner London Borough'"() {

        expect:
        def response = maintenanceThresholdCalculator.calculateGeneral(inLondon, buildScalaBigDecimal(tuitionFees), buildScalaBigDecimal(tuitionFeesPaid),
            buildScalaBigDecimal(accommodationFeesPaid), dependants, courseStartDate, courseEndDate, buildScalaOption(originalCourseStartDate),
            courseType == "pre-sessional", dependantsOnly)

        assert (response._1 == buildScalaBigDecimal(threshold))

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate    | inLondon | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants | courseType      | dependantsOnly || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(1998, 5, 31)  | LocalDate.of(1999, 5, 30)  | null                       | true     | 8553.87     | 0.00            | 0.00                  | 4          | "main"          | false          || 50358.87  || 0.00       || 9            || LocalDate.of(1999, 9, 30)
        LocalDate.of(1995, 11, 1)  | LocalDate.of(1996, 5, 15)  | null                       | true     | 127.02      | 0.00            | 1221.16               | 11         | "main"          | false          || 91415.86  || 0.00       || 0            || LocalDate.of(1996, 7, 15)
        LocalDate.of(2037, 3, 11)  | LocalDate.of(2037, 10, 8)  | null                       | true     | 2110.20     | 0.00            | 0.00                  | 2          | "pre-sessional" | false          || 26175.20  || 0.00       || 0            || LocalDate.of(2037, 12, 8)
        LocalDate.of(1990, 1, 4)   | LocalDate.of(1990, 11, 27) | LocalDate.of(1989, 2, 22)  | true     | 1698.53     | 4078.71         | 640.16                | 11         | "main"          | false          || 94399.84  || 0.00       || 9            || LocalDate.of(1991, 3, 27)
        LocalDate.of(2035, 7, 1)   | LocalDate.of(2035, 11, 12) | null                       | true     | 0.00        | 0.00            | 328.11                | 0          | "pre-sessional" | false          || 5996.89   || 0.00       || 0            || LocalDate.of(2035, 12, 12)
        LocalDate.of(2030, 5, 22)  | LocalDate.of(2030, 12, 20) | null                       | true     | 1226.14     | 4686.31         | 0.00                  | 7          | "pre-sessional" | false          || 62090.00  || 0.00       || 0            || LocalDate.of(2031, 2, 20)
        LocalDate.of(1997, 7, 12)  | LocalDate.of(1997, 8, 19)  | LocalDate.of(1996, 12, 26) | true     | 7772.40     | 0.00            | 0.00                  | 1          | "main"          | false          || 13682.40  || 0.00       || 0            || LocalDate.of(1997, 10, 19)
        LocalDate.of(1988, 2, 6)   | LocalDate.of(1988, 11, 29) | null                       | true     | 1519.60     | 231.64          | 0.00                  | 5          | "main"          | false          || 50697.96  || 0.00       || 9            || LocalDate.of(1989, 1, 29)
        LocalDate.of(1993, 3, 27)  | LocalDate.of(1994, 1, 6)   | null                       | true     | 0.00        | 0.00            | 0.00                  | 3          | "main"          | false          || 34200.00  || 0.00       || 9            || LocalDate.of(1994, 3, 6)
        LocalDate.of(1973, 1, 9)   | LocalDate.of(1973, 6, 9)   | null                       | true     | 748.07      | 0.00            | 1242.30               | 0          | "main"          | false          || 7095.77   || 0.00       || 0            || LocalDate.of(1973, 6, 16)
        LocalDate.of(2011, 9, 7)   | LocalDate.of(2012, 5, 28)  | LocalDate.of(2010, 10, 30) | true     | 7257.28     | 0.00            | 0.00                  | 14         | "main"          | false          || 125112.28 || 0.00       || 0            || LocalDate.of(2012, 9, 28)
        LocalDate.of(2053, 6, 28)  | LocalDate.of(2053, 9, 10)  | LocalDate.of(2053, 5, 4)   | true     | 2011.98     | 5747.88         | 1653.98               | 11         | "main"          | false          || 30415.00  || 1265.00    || 0            || LocalDate.of(2053, 9, 17)
        LocalDate.of(1974, 10, 8)  | LocalDate.of(1974, 10, 14) | LocalDate.of(1973, 11, 3)  | true     | 3360.82     | 0.00            | 1892.87               | 6          | "main"          | false          || 18570.82  || 1265.00    || 0            || LocalDate.of(1974, 12, 14)
        LocalDate.of(2000, 4, 25)  | LocalDate.of(2000, 7, 23)  | LocalDate.of(1999, 7, 29)  | true     | 8797.78     | 2303.70         | 0.00                  | 1          | "main"          | false          || 14514.08  || 0.00       || 0            || LocalDate.of(2000, 9, 23)
        LocalDate.of(2045, 6, 6)   | LocalDate.of(2046, 6, 10)  | LocalDate.of(2045, 5, 4)   | true     | 1746.23     | 0.00            | 146.47                | 8          | "main"          | false          || 73824.76  || 0.00       || 9            || LocalDate.of(2046, 10, 10)
        LocalDate.of(2011, 4, 1)   | LocalDate.of(2011, 7, 20)  | LocalDate.of(2010, 11, 24) | true     | 7726.61     | 0.00            | 0.00                  | 3          | "main"          | false          || 27996.61  || 0.00       || 0            || LocalDate.of(2011, 9, 20)
        LocalDate.of(2046, 4, 13)  | LocalDate.of(2047, 5, 2)   | null                       | true     | 3434.43     | 0.00            | 0.00                  | 3          | "pre-sessional" | false          || 37634.43  || 0.00       || 9            || LocalDate.of(2047, 9, 2)
        LocalDate.of(1984, 11, 21) | LocalDate.of(1985, 10, 3)  | null                       | true     | 8442.92     | 0.00            | 630.95                | 4          | "main"          | false          || 49616.97  || 0.00       || 9            || LocalDate.of(1985, 12, 3)
        LocalDate.of(1999, 10, 5)  | LocalDate.of(2000, 10, 2)  | LocalDate.of(1998, 12, 24) | true     | 8580.13     | 0.00            | 0.00                  | 1          | "main"          | false          || 27570.13  || 0.00       || 9            || LocalDate.of(2001, 2, 2)
        LocalDate.of(2020, 4, 8)   | LocalDate.of(2020, 5, 27)  | LocalDate.of(2020, 2, 16)  | true     | 108.00      | 9431.82         | 0.00                  | 0          | "main"          | false          || 2530.00   || 0.00       || 0            || LocalDate.of(2020, 6, 3)

    }

    def "Tier 4 General - Check 'Tuition Fees paid'"() {

        expect:
        def response = maintenanceThresholdCalculator.calculateGeneral(inLondon, buildScalaBigDecimal(tuitionFees), buildScalaBigDecimal(tuitionFeesPaid),
            buildScalaBigDecimal(accommodationFeesPaid), dependants, courseStartDate, courseEndDate, buildScalaOption(originalCourseStartDate),
            courseType == "pre-sessional", dependantsOnly)

        assert (response._1 == buildScalaBigDecimal(threshold))

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate   | inLondon | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants | courseType      | dependantsOnly || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(1992, 7, 11)  | LocalDate.of(1993, 2, 20)  | LocalDate.of(1991, 10, 6) | true     | 3340.37     | 9324.82         | 0.00                  | 6          | "main"          | false          || 55750.00  || 0.00       || 0            || LocalDate.of(1993, 6, 20)
        LocalDate.of(2045, 10, 12) | LocalDate.of(2046, 1, 26)  | null                      | false    | 5880.21     | 7987.96         | 0.00                  | 0          | "pre-sessional" | false          || 4060.00   || 0.00       || 0            || LocalDate.of(2046, 2, 26)
        LocalDate.of(1983, 9, 17)  | LocalDate.of(1983, 11, 17) | LocalDate.of(1983, 5, 7)  | true     | 220.98      | 7861.25         | 913.38                | 11         | "main"          | false          || 49356.62  || 0.00       || 0            || LocalDate.of(1984, 1, 17)
        LocalDate.of(2019, 3, 21)  | LocalDate.of(2019, 7, 11)  | LocalDate.of(2018, 4, 29) | false    | 1696.37     | 7955.46         | 0.00                  | 4          | "main"          | false          || 25820.00  || 0.00       || 0            || LocalDate.of(2019, 11, 11)
        LocalDate.of(1995, 11, 21) | LocalDate.of(1996, 6, 7)   | null                      | false    | 3586.18     | 4617.08         | 228.85                | 1          | "main"          | false          || 12996.15  || 0.00       || 0            || LocalDate.of(1996, 8, 7)
        LocalDate.of(1978, 9, 17)  | LocalDate.of(1979, 7, 9)   | null                      | true     | 6032.59     | 2344.84         | 332.50                | 12         | "pre-sessional" | false          || 106000.25 || 0.00       || 9            || LocalDate.of(1979, 9, 9)
        LocalDate.of(1995, 4, 24)  | LocalDate.of(1996, 3, 31)  | null                      | true     | 7842.49     | 4560.67         | 0.00                  | 4          | "main"          | false          || 45086.82  || 0.00       || 9            || LocalDate.of(1996, 5, 31)
        LocalDate.of(2016, 5, 23)  | LocalDate.of(2016, 6, 3)   | null                      | true     | 6834.30     | 3439.84         | 0.00                  | 0          | "pre-sessional" | false          || 4659.46   || 0.00       || 0            || LocalDate.of(2016, 7, 3)
        LocalDate.of(2021, 6, 6)   | LocalDate.of(2022, 7, 1)   | LocalDate.of(2020, 9, 14) | false    | 8054.41     | 3117.73         | 0.00                  | 14         | "main"          | false          || 99751.68  || 0.00       || 9            || LocalDate.of(2022, 11, 1)
        LocalDate.of(2044, 6, 24)  | LocalDate.of(2045, 5, 11)  | LocalDate.of(2044, 4, 4)  | false    | 6149.34     | 4176.00         | 0.00                  | 2          | "main"          | false          || 23348.34  || 0.00       || 9            || LocalDate.of(2045, 9, 11)
        LocalDate.of(2015, 2, 11)  | LocalDate.of(2016, 1, 22)  | LocalDate.of(2014, 8, 21) | false    | 2703.99     | 3705.82         | 0.00                  | 6          | "main"          | false          || 45855.00  || 0.00       || 9            || LocalDate.of(2016, 5, 22)
        LocalDate.of(1996, 1, 19)  | LocalDate.of(1996, 3, 21)  | null                      | false    | 2140.07     | 1757.85         | 0.00                  | 0          | "pre-sessional" | false          || 3427.22   || 0.00       || 0            || LocalDate.of(1996, 4, 21)
        LocalDate.of(2017, 11, 9)  | LocalDate.of(2018, 9, 6)   | LocalDate.of(2017, 2, 8)  | false    | 7610.83     | 3379.51         | 1831.41               | 5          | "main"          | false          || 42701.32  || 1265.00    || 9            || LocalDate.of(2019, 1, 6)
        LocalDate.of(2012, 4, 10)  | LocalDate.of(2012, 6, 27)  | LocalDate.of(2012, 3, 26) | true     | 3002.32     | 1254.78         | 0.00                  | 0          | "main"          | false          || 5542.54   || 0.00       || 0            || LocalDate.of(2012, 7, 4)
        LocalDate.of(1988, 5, 22)  | LocalDate.of(1988, 8, 21)  | null                      | false    | 3332.95     | 110.11          | 1430.00               | 0          | "main"          | false          || 5002.84   || 1265.00    || 0            || LocalDate.of(1988, 8, 28)
        LocalDate.of(1992, 10, 22) | LocalDate.of(1993, 2, 27)  | LocalDate.of(1992, 8, 25) | false    | 5593.29     | 9062.45         | 0.00                  | 14         | "main"          | false          || 71715.00  || 0.00       || 0            || LocalDate.of(1993, 4, 27)
        LocalDate.of(2054, 1, 23)  | LocalDate.of(2054, 8, 24)  | null                      | true     | 2633.88     | 7457.81         | 0.00                  | 1          | "pre-sessional" | false          || 17725.00  || 0.00       || 0            || LocalDate.of(2054, 10, 24)
        LocalDate.of(2023, 9, 24)  | LocalDate.of(2024, 3, 6)   | LocalDate.of(2023, 7, 27) | false    | 9605.15     | 2148.46         | 0.00                  | 14         | "main"          | false          || 89706.69  || 0.00       || 0            || LocalDate.of(2024, 5, 6)
        LocalDate.of(2030, 5, 30)  | LocalDate.of(2030, 7, 19)  | null                      | true     | 904.25      | 9098.86         | 308.73                | 0          | "main"          | false          || 2221.27   || 0.00       || 0            || LocalDate.of(2030, 7, 26)
        LocalDate.of(2001, 5, 19)  | LocalDate.of(2001, 7, 31)  | LocalDate.of(2000, 8, 8)  | true     | 9381.54     | 2872.55         | 406.92                | 7          | "main"          | false          || 39472.07  || 0.00       || 0            || LocalDate.of(2001, 9, 30)
    }

    def "Tier 4 General - Check 'Accommodation Fees paid'"() {

        expect:
        def response = maintenanceThresholdCalculator.calculateGeneral(inLondon, buildScalaBigDecimal(tuitionFees), buildScalaBigDecimal(tuitionFeesPaid),
            buildScalaBigDecimal(accommodationFeesPaid), dependants, courseStartDate, courseEndDate, buildScalaOption(originalCourseStartDate),
            courseType == "pre-sessional", dependantsOnly)

        assert (response._1 == buildScalaBigDecimal(threshold))

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate    | inLondon | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants | courseType      | dependantsOnly || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(2052, 12, 24) | LocalDate.of(2053, 11, 23) | null                       | false    | 6428.56     | 6785.98         | 1400.47               | 3          | "pre-sessional" | false          || 26230.00  || 1265.00    || 9            || LocalDate.of(2054, 1, 23)
        LocalDate.of(2019, 9, 20)  | LocalDate.of(2019, 12, 17) | LocalDate.of(2019, 7, 20)  | true     | 9506.38     | 0.00            | 1552.36               | 3          | "main"          | false          || 22176.38  || 1265.00    || 0            || LocalDate.of(2019, 12, 24)
        LocalDate.of(1987, 10, 9)  | LocalDate.of(1988, 8, 18)  | null                       | true     | 6394.48     | 4431.42         | 525.04                | 10         | "main"          | false          || 88873.02  || 0.00       || 9            || LocalDate.of(1988, 10, 18)
        LocalDate.of(2017, 7, 5)   | LocalDate.of(2018, 7, 15)  | null                       | true     | 2024.44     | 5663.05         | 1420.79               | 13         | "main"          | false          || 108985.00 || 1265.00    || 9            || LocalDate.of(2018, 11, 15)
        LocalDate.of(1972, 12, 18) | LocalDate.of(1973, 6, 11)  | null                       | true     | 8241.92     | 3549.50         | 1580.45               | 0          | "pre-sessional" | false          || 11017.42  || 1265.00    || 0            || LocalDate.of(1973, 7, 11)
        LocalDate.of(2040, 8, 15)  | LocalDate.of(2041, 5, 4)   | LocalDate.of(2039, 10, 15) | false    | 3187.56     | 7279.32         | 1159.97               | 13         | "main"          | false          || 87535.03  || 0.00       || 0            || LocalDate.of(2041, 9, 4)
        LocalDate.of(2020, 10, 26) | LocalDate.of(2021, 9, 23)  | LocalDate.of(2020, 8, 17)  | true     | 3457.99     | 0.00            | 253.07                | 14         | "main"          | false          || 121059.92 || 0.00       || 9            || LocalDate.of(2022, 1, 23)
        LocalDate.of(2021, 12, 11) | LocalDate.of(2022, 2, 1)   | null                       | false    | 1047.70     | 0.00            | 1637.81               | 0          | "pre-sessional" | false          || 1812.70   || 1265.00    || 0            || LocalDate.of(2022, 3, 1)
        LocalDate.of(2052, 6, 28)  | LocalDate.of(2052, 11, 20) | null                       | true     | 173.38      | 0.00            | 1876.85               | 0          | "pre-sessional" | false          || 5233.38   || 1265.00    || 0            || LocalDate.of(2052, 12, 20)
        LocalDate.of(2051, 11, 30) | LocalDate.of(2052, 9, 4)   | null                       | false    | 9943.81     | 0.00            | 1009.92               | 13         | "main"          | false          || 97628.89  || 0.00       || 9            || LocalDate.of(2052, 11, 4)
        LocalDate.of(2035, 5, 17)  | LocalDate.of(2036, 3, 10)  | LocalDate.of(2035, 3, 19)  | false    | 258.23      | 0.00            | 1792.50               | 1          | "main"          | false          || 14248.23  || 1265.00    || 9            || LocalDate.of(2036, 5, 10)
        LocalDate.of(2053, 9, 8)   | LocalDate.of(2054, 7, 29)  | LocalDate.of(2053, 7, 4)   | false    | 1231.54     | 469.39          | 984.79                | 2          | "main"          | false          || 21152.36  || 0.00       || 9            || LocalDate.of(2054, 11, 29)
        LocalDate.of(2042, 8, 19)  | LocalDate.of(2043, 4, 28)  | null                       | false    | 6.10        | 0.00            | 592.92                | 14         | "main"          | false          || 94228.18  || 0.00       || 0            || LocalDate.of(2043, 6, 28)
        LocalDate.of(2053, 8, 4)   | LocalDate.of(2054, 5, 5)   | LocalDate.of(2053, 3, 8)   | false    | 1450.30     | 0.00            | 698.74                | 2          | "main"          | false          || 22126.56  || 0.00       || 9            || LocalDate.of(2054, 9, 5)
        LocalDate.of(1976, 8, 17)  | LocalDate.of(1977, 8, 11)  | LocalDate.of(1975, 10, 5)  | true     | 4880.73     | 4043.79         | 21.25                 | 13         | "main"          | false          || 111065.69 || 0.00       || 9            || LocalDate.of(1977, 12, 11)
        LocalDate.of(2052, 6, 28)  | LocalDate.of(2052, 7, 3)   | null                       | true     | 0.00        | 0.00            | 1882.50               | 0          | "pre-sessional" | false          || 0.00      || 1265.00    || 0            || LocalDate.of(2052, 8, 3)
        LocalDate.of(2049, 9, 10)  | LocalDate.of(2049, 11, 22) | null                       | false    | 4018.75     | 4171.32         | 946.87                | 0          | "pre-sessional" | false          || 2098.13   || 0.00       || 0            || LocalDate.of(2049, 12, 22)
        LocalDate.of(2047, 2, 16)  | LocalDate.of(2047, 12, 8)  | null                       | false    | 3527.83     | 0.00            | 1958.63               | 8          | "pre-sessional" | false          || 60357.83  || 1265.00    || 9            || LocalDate.of(2048, 2, 8)
        LocalDate.of(2038, 12, 24) | LocalDate.of(2039, 4, 26)  | null                       | false    | 5783.37     | 8805.83         | 1939.21               | 0          | "pre-sessional" | false          || 3810.00   || 1265.00    || 0            || LocalDate.of(2039, 5, 26)
        LocalDate.of(2044, 8, 18)  | LocalDate.of(2045, 8, 18)  | null                       | false    | 6744.43     | 2640.28         | 21.88                 | 5          | "pre-sessional" | false          || 43817.27  || 0.00       || 9            || LocalDate.of(2045, 12, 18)
    }

    def "Tier 4 General - Check 'continuations'"() {

        expect:
        def response = maintenanceThresholdCalculator.calculateGeneral(inLondon, buildScalaBigDecimal(tuitionFees), buildScalaBigDecimal(tuitionFeesPaid),
            buildScalaBigDecimal(accommodationFeesPaid), dependants, courseStartDate, courseEndDate, buildScalaOption(originalCourseStartDate),
            courseType == "pre-sessional", dependantsOnly)

        def thresholdValue = response._1
        def cappedValues = DataUtils.getCappedValues(response._2)
        def cappedAccommodation = cappedValues.accommodationFeesPaid()
        def cappedCourseLength = cappedValues.courseLength()

        assert thresholdValue == buildScalaBigDecimal(threshold)
        assert DataUtils.compareAccommodationFees(buildScalaBigDecimal(feesCapped), cappedAccommodation) == true
        assert DataUtils.compareCourseLength(courseCapped, cappedCourseLength) == true

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate    | inLondon | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants | courseType | dependantsOnly || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(2027, 5, 5)   | LocalDate.of(2027, 10, 28) | LocalDate.of(2026, 6, 12)  | false    | 0.00        | 0.00            | 0.00                  | 2          | "main"     | false          || 18330.00  || 0.00       || 0            || LocalDate.of(2028, 2, 28)
        LocalDate.of(1975, 7, 2)   | LocalDate.of(1975, 9, 8)   | LocalDate.of(1974, 12, 29) | true     | 0.00        | 0.00            | 0.00                  | 6          | "main"     | false          || 29145.00  || 0.00       || 0            || LocalDate.of(1975, 11, 8)
        LocalDate.of(1978, 4, 15)  | LocalDate.of(1979, 2, 15)  | LocalDate.of(1977, 9, 9)   | true     | 0.00        | 0.00            | 0.00                  | 8          | "main"     | false          || 72225.00  || 0.00       || 9            || LocalDate.of(1979, 6, 15)
        LocalDate.of(1996, 1, 4)   | LocalDate.of(1996, 3, 8)   | LocalDate.of(1995, 10, 7)  | true     | 0.00        | 0.00            | 0.00                  | 7          | "main"     | false          || 21540.00  || 0.00       || 0            || LocalDate.of(1996, 3, 15)
        LocalDate.of(1991, 12, 22) | LocalDate.of(1992, 6, 24)  | LocalDate.of(1991, 2, 22)  | false    | 0.00        | 0.00            | 0.00                  | 8          | "main"     | false          || 56065.00  || 0.00       || 0            || LocalDate.of(1992, 10, 24)
        LocalDate.of(1979, 12, 5)  | LocalDate.of(1980, 7, 31)  | LocalDate.of(1978, 11, 27) | false    | 0.00        | 0.00            | 0.00                  | 13         | "main"     | false          || 87680.00  || 0.00       || 0            || LocalDate.of(1980, 11, 30)
        LocalDate.of(2043, 11, 11) | LocalDate.of(2044, 7, 15)  | LocalDate.of(2043, 2, 26)  | false    | 0.00        | 0.00            | 0.00                  | 2          | "main"     | false          || 21375.00  || 0.00       || 0            || LocalDate.of(2044, 11, 15)
        LocalDate.of(1986, 1, 14)  | LocalDate.of(1986, 7, 16)  | LocalDate.of(1985, 11, 14) | true     | 0.00        | 0.00            | 0.00                  | 5          | "main"     | false          || 46880.00  || 0.00       || 0            || LocalDate.of(1986, 9, 16)
        LocalDate.of(2009, 1, 3)   | LocalDate.of(2009, 6, 13)  | LocalDate.of(2008, 4, 22)  | false    | 0.00        | 0.00            | 0.00                  | 4          | "main"     | false          || 30570.00  || 0.00       || 0            || LocalDate.of(2009, 10, 13)
        LocalDate.of(2004, 3, 22)  | LocalDate.of(2004, 9, 3)   | LocalDate.of(2003, 2, 21)  | false    | 0.00        | 0.00            | 0.00                  | 9          | "main"     | false          || 61170.00  || 0.00       || 0            || LocalDate.of(2005, 1, 3)
        LocalDate.of(2035, 2, 22)  | LocalDate.of(2036, 3, 27)  | LocalDate.of(2034, 4, 5)   | true     | 0.00        | 0.00            | 0.00                  | 9          | "main"     | false          || 79830.00  || 0.00       || 9            || LocalDate.of(2036, 7, 27)
        LocalDate.of(2047, 1, 12)  | LocalDate.of(2047, 10, 28) | LocalDate.of(2046, 5, 24)  | true     | 0.00        | 0.00            | 0.00                  | 13         | "main"     | false          || 110250.00 || 0.00       || 9            || LocalDate.of(2048, 2, 28)
        LocalDate.of(2016, 11, 18) | LocalDate.of(2016, 12, 17) | LocalDate.of(2016, 5, 31)  | true     | 0.00        | 0.00            | 0.00                  | 3          | "main"     | false          || 8870.00   || 0.00       || 0            || LocalDate.of(2017, 2, 17)
        LocalDate.of(2012, 4, 17)  | LocalDate.of(2012, 6, 14)  | LocalDate.of(2011, 8, 2)   | true     | 0.00        | 0.00            | 0.00                  | 3          | "main"     | false          || 12670.00  || 0.00       || 0            || LocalDate.of(2012, 8, 14)
        LocalDate.of(2049, 11, 5)  | LocalDate.of(2049, 11, 20) | LocalDate.of(2049, 4, 25)  | false    | 0.00        | 0.00            | 0.00                  | 10         | "main"     | false          || 21415.00  || 0.00       || 0            || LocalDate.of(2050, 1, 20)
        LocalDate.of(1982, 5, 2)   | LocalDate.of(1982, 7, 3)   | LocalDate.of(1982, 3, 22)  | true     | 0.00        | 0.00            | 0.00                  | 7          | "main"     | false          || 21540.00  || 0.00       || 0            || LocalDate.of(1982, 7, 10)
        LocalDate.of(2019, 6, 18)  | LocalDate.of(2019, 9, 2)   | LocalDate.of(2018, 7, 27)  | true     | 0.00        | 0.00            | 0.00                  | 8          | "main"     | false          || 51115.00  || 0.00       || 0            || LocalDate.of(2020, 1, 2)
        LocalDate.of(1990, 7, 29)  | LocalDate.of(1991, 6, 19)  | LocalDate.of(1989, 6, 28)  | true     | 0.00        | 0.00            | 0.00                  | 6          | "main"     | false          || 57015.00  || 0.00       || 9            || LocalDate.of(1991, 10, 19)
        LocalDate.of(1990, 9, 6)   | LocalDate.of(1991, 10, 6)  | LocalDate.of(1990, 6, 9)   | true     | 0.00        | 0.00            | 0.00                  | 5          | "main"     | false          || 49410.00  || 0.00       || 9            || LocalDate.of(1992, 2, 6)
        LocalDate.of(2026, 10, 1)  | LocalDate.of(2027, 2, 27)  | LocalDate.of(2025, 11, 13) | false    | 0.00        | 0.00            | 0.00                  | 11         | "main"     | false          || 72395.00  || 0.00       || 0            || LocalDate.of(2027, 6, 27)
    }

    // Dependants only

    def "Tier 4 General - Check 'Non Inner London Borough' (dependants only)"() {

        expect:
        def response = maintenanceThresholdCalculator.calculateGeneral(inLondon, buildScalaBigDecimal(tuitionFees), buildScalaBigDecimal(tuitionFeesPaid),
            buildScalaBigDecimal(accommodationFeesPaid), dependants, courseStartDate, courseEndDate, buildScalaOption(originalCourseStartDate),
            courseType == "pre-sessional", dependantsOnly)

        assert (response._1 == buildScalaBigDecimal(threshold))

        where:
        courseStartDate            | courseEndDate             | originalCourseStartDate   | inLondon | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants | courseType      | dependantsOnly || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(2001, 1, 4)   | LocalDate.of(2001, 7, 6)  | null                      | false    | 0.00        | 0.00            | 0.00                  | 6          | "pre-sessional" | true           || 36720.00  || 0.00       || 0            || LocalDate.of(2001, 9, 6)
        LocalDate.of(2000, 8, 19)  | LocalDate.of(2001, 3, 21) | null                      | false    | 0.00        | 0.00            | 0.00                  | 2          | "main"          | true           || 12240.00  || 0.00       || 0            || LocalDate.of(2001, 5, 21)
        LocalDate.of(2052, 12, 4)  | LocalDate.of(2053, 4, 20) | null                      | false    | 0.00        | 0.00            | 0.00                  | 0          | "main"          | true           || 0.00      || 0.00       || 0            || LocalDate.of(2053, 4, 27)
        LocalDate.of(2010, 12, 3)  | LocalDate.of(2011, 3, 1)  | LocalDate.of(2009, 11, 9) | false    | 0.00        | 0.00            | 0.00                  | 0          | "main"          | true           || 0.00      || 0.00       || 0            || LocalDate.of(2011, 7, 1)
        LocalDate.of(2004, 4, 11)  | LocalDate.of(2004, 9, 18) | LocalDate.of(2003, 5, 14) | false    | 0.00        | 0.00            | 0.00                  | 6          | "main"          | true           || 36720.00  || 0.00       || 0            || LocalDate.of(2005, 1, 18)
        LocalDate.of(2054, 6, 19)  | LocalDate.of(2055, 4, 28) | null                      | false    | 0.00        | 0.00            | 0.00                  | 1          | "main"          | true           || 6120.00   || 0.00       || 9            || LocalDate.of(2055, 6, 28)
        LocalDate.of(2006, 4, 14)  | LocalDate.of(2007, 3, 26) | LocalDate.of(2005, 5, 15) | false    | 0.00        | 0.00            | 0.00                  | 8          | "main"          | true           || 48960.00  || 0.00       || 9            || LocalDate.of(2007, 7, 26)
        LocalDate.of(2000, 8, 31)  | LocalDate.of(2000, 11, 5) | null                      | false    | 0.00        | 0.00            | 0.00                  | 0          | "main"          | true           || 0.00      || 0.00       || 0            || LocalDate.of(2000, 11, 12)
        LocalDate.of(2014, 11, 4)  | LocalDate.of(2015, 6, 16) | LocalDate.of(2014, 6, 4)  | false    | 0.00        | 0.00            | 0.00                  | 8          | "main"          | true           || 48960.00  || 0.00       || 0            || LocalDate.of(2015, 10, 16)
        LocalDate.of(2012, 12, 23) | LocalDate.of(2014, 1, 25) | null                      | false    | 0.00        | 0.00            | 0.00                  | 2          | "main"          | true           || 12240.00  || 0.00       || 9            || LocalDate.of(2014, 5, 25)
        LocalDate.of(2000, 3, 4)   | LocalDate.of(2000, 3, 15) | null                      | false    | 0.00        | 0.00            | 0.00                  | 0          | "pre-sessional" | true           || 0.00      || 0.00       || 0            || LocalDate.of(2000, 4, 15)
        LocalDate.of(1978, 8, 23)  | LocalDate.of(1979, 3, 16) | null                      | false    | 0.00        | 0.00            | 0.00                  | 13         | "pre-sessional" | true           || 79560.00  || 0.00       || 0            || LocalDate.of(1979, 5, 16)
        LocalDate.of(2047, 3, 10)  | LocalDate.of(2047, 5, 8)  | null                      | false    | 0.00        | 0.00            | 0.00                  | 0          | "main"          | true           || 0.00      || 0.00       || 0            || LocalDate.of(2047, 5, 15)
        LocalDate.of(2002, 4, 18)  | LocalDate.of(2003, 1, 25) | null                      | false    | 0.00        | 0.00            | 0.00                  | 2          | "pre-sessional" | true           || 12240.00  || 0.00       || 9            || LocalDate.of(2003, 3, 25)
        LocalDate.of(2006, 4, 19)  | LocalDate.of(2006, 8, 20) | LocalDate.of(2005, 4, 15) | false    | 0.00        | 0.00            | 0.00                  | 5          | "main"          | true           || 30600.00  || 0.00       || 0            || LocalDate.of(2006, 12, 20)
        LocalDate.of(1976, 3, 24)  | LocalDate.of(1976, 4, 21) | null                      | false    | 0.00        | 0.00            | 0.00                  | 0          | "main"          | true           || 0.00      || 0.00       || 0            || LocalDate.of(1976, 4, 28)
        LocalDate.of(1983, 10, 28) | LocalDate.of(1984, 3, 20) | LocalDate.of(1983, 3, 25) | false    | 0.00        | 0.00            | 0.00                  | 12         | "main"          | true           || 57120.00  || 0.00       || 0            || LocalDate.of(1984, 5, 20)
        LocalDate.of(1986, 2, 16)  | LocalDate.of(1986, 3, 30) | null                      | false    | 0.00        | 0.00            | 0.00                  | 0          | "pre-sessional" | true           || 0.00      || 0.00       || 0            || LocalDate.of(1986, 4, 30)
        LocalDate.of(2041, 9, 26)  | LocalDate.of(2042, 4, 24) | null                      | false    | 0.00        | 0.00            | 0.00                  | 14         | "pre-sessional" | true           || 85680.00  || 0.00       || 0            || LocalDate.of(2042, 6, 24)
        LocalDate.of(1995, 3, 1)   | LocalDate.of(1996, 3, 4)  | null                      | false    | 0.00        | 0.00            | 0.00                  | 13         | "pre-sessional" | true           || 79560.00  || 0.00       || 9            || LocalDate.of(1996, 7, 4)
    }

    def "Tier 4 General - Check 'Inner London Borough' (dependants only)"() {

        expect:
        def response = maintenanceThresholdCalculator.calculateGeneral(inLondon, buildScalaBigDecimal(tuitionFees), buildScalaBigDecimal(tuitionFeesPaid),
            buildScalaBigDecimal(accommodationFeesPaid), dependants, courseStartDate, courseEndDate, buildScalaOption(originalCourseStartDate),
            courseType == "pre-sessional", dependantsOnly)

        assert (response._1 == buildScalaBigDecimal(threshold))

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate    | inLondon | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants | courseType      | dependantsOnly || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(2011, 11, 7)  | LocalDate.of(2012, 1, 27)  | LocalDate.of(2011, 3, 17)  | true     | 0.00        | 0.00            | 0.00                  | 12         | "main"          | true           || 50700.00  || 0.00       || 0            || LocalDate.of(2012, 3, 27)
        LocalDate.of(2000, 3, 23)  | LocalDate.of(2001, 1, 26)  | LocalDate.of(1999, 5, 18)  | true     | 0.00        | 0.00            | 0.00                  | 9          | "main"          | true           || 68445.00  || 0.00       || 9            || LocalDate.of(2001, 5, 26)
        LocalDate.of(1994, 4, 24)  | LocalDate.of(1994, 6, 4)   | null                       | true     | 0.00        | 0.00            | 0.00                  | 0          | "pre-sessional" | true           || 0.00      || 0.00       || 0            || LocalDate.of(1994, 7, 4)
        LocalDate.of(2017, 11, 6)  | LocalDate.of(2018, 4, 29)  | LocalDate.of(2016, 12, 30) | true     | 0.00        | 0.00            | 0.00                  | 1          | "main"          | true           || 7605.00   || 0.00       || 0            || LocalDate.of(2018, 8, 29)
        LocalDate.of(1992, 10, 24) | LocalDate.of(1993, 1, 6)   | null                       | true     | 0.00        | 0.00            | 0.00                  | 0          | "pre-sessional" | true           || 0.00      || 0.00       || 0            || LocalDate.of(1993, 2, 6)
        LocalDate.of(2034, 6, 15)  | LocalDate.of(2034, 6, 29)  | null                       | true     | 0.00        | 0.00            | 0.00                  | 0          | "pre-sessional" | true           || 0.00      || 0.00       || 0            || LocalDate.of(2034, 7, 29)
        LocalDate.of(1977, 7, 13)  | LocalDate.of(1977, 10, 16) | null                       | true     | 0.00        | 0.00            | 0.00                  | 0          | "main"          | true           || 0.00      || 0.00       || 0            || LocalDate.of(1977, 10, 23)
        LocalDate.of(1998, 7, 18)  | LocalDate.of(1999, 1, 2)   | null                       | true     | 0.00        | 0.00            | 0.00                  | 0          | "main"          | true           || 0.00      || 0.00       || 0            || LocalDate.of(1999, 1, 9)
        LocalDate.of(2025, 6, 18)  | LocalDate.of(2026, 6, 3)   | LocalDate.of(2024, 7, 3)   | true     | 0.00        | 0.00            | 0.00                  | 10         | "main"          | true           || 76050.00  || 0.00       || 9            || LocalDate.of(2026, 10, 3)
        LocalDate.of(1999, 9, 25)  | LocalDate.of(1999, 10, 30) | null                       | true     | 0.00        | 0.00            | 0.00                  | 0          | "pre-sessional" | true           || 0.00      || 0.00       || 0            || LocalDate.of(1999, 11, 30)
        LocalDate.of(2003, 3, 14)  | LocalDate.of(2003, 3, 16)  | LocalDate.of(2003, 1, 4)   | true     | 0.00        | 0.00            | 0.00                  | 5          | "main"          | true           || 4225.00   || 0.00       || 0            || LocalDate.of(2003, 3, 23)
        LocalDate.of(2049, 6, 9)   | LocalDate.of(2050, 1, 3)   | null                       | true     | 0.00        | 0.00            | 0.00                  | 11         | "main"          | true           || 83655.00  || 0.00       || 0            || LocalDate.of(2050, 3, 3)
        LocalDate.of(1978, 3, 4)   | LocalDate.of(1978, 7, 17)  | null                       | true     | 0.00        | 0.00            | 0.00                  | 0          | "pre-sessional" | true           || 0.00      || 0.00       || 0            || LocalDate.of(1978, 8, 17)
        LocalDate.of(2022, 12, 12) | LocalDate.of(2022, 12, 23) | LocalDate.of(2022, 9, 12)  | true     | 0.00        | 0.00            | 0.00                  | 13         | "main"          | true           || 10985.00  || 0.00       || 0            || LocalDate.of(2022, 12, 30)
        LocalDate.of(2045, 12, 15) | LocalDate.of(2046, 11, 11) | LocalDate.of(2045, 3, 8)   | true     | 0.00        | 0.00            | 0.00                  | 10         | "main"          | true           || 76050.00  || 0.00       || 9            || LocalDate.of(2047, 3, 11)
        LocalDate.of(2047, 12, 15) | LocalDate.of(2048, 11, 1)  | null                       | true     | 0.00        | 0.00            | 0.00                  | 12         | "main"          | true           || 91260.00  || 0.00       || 9            || LocalDate.of(2049, 1, 1)
        LocalDate.of(2007, 11, 19) | LocalDate.of(2008, 5, 25)  | null                       | true     | 0.00        | 0.00            | 0.00                  | 2          | "main"          | true           || 15210.00  || 0.00       || 0            || LocalDate.of(2008, 7, 25)
        LocalDate.of(1997, 5, 2)   | LocalDate.of(1998, 6, 2)   | LocalDate.of(1997, 3, 10)  | true     | 0.00        | 0.00            | 0.00                  | 12         | "main"          | true           || 91260.00  || 0.00       || 9            || LocalDate.of(1998, 10, 2)
        LocalDate.of(2000, 11, 19) | LocalDate.of(2000, 12, 2)  | LocalDate.of(2000, 3, 26)  | true     | 0.00        | 0.00            | 0.00                  | 13         | "main"          | true           || 32955.00  || 0.00       || 0            || LocalDate.of(2001, 2, 2)
        LocalDate.of(1973, 3, 12)  | LocalDate.of(1973, 6, 12)  | null                       | true     | 0.00        | 0.00            | 0.00                  | 0          | "pre-sessional" | true           || 0.00      || 0.00       || 0            || LocalDate.of(1973, 7, 12)
    }

    def "Tier 4 General - Check 'continuations' (dependants only)"() {

        expect:
        def response = maintenanceThresholdCalculator.calculateGeneral(inLondon, buildScalaBigDecimal(tuitionFees), buildScalaBigDecimal(tuitionFeesPaid),
            buildScalaBigDecimal(accommodationFeesPaid), dependants, courseStartDate, courseEndDate, buildScalaOption(originalCourseStartDate),
            courseType == "pre-sessional", dependantsOnly)

        def thresholdValue = response._1
        def cappedValues = DataUtils.getCappedValues(response._2)
        def cappedAccommodation = cappedValues.accommodationFeesPaid()
        def cappedCourseLength = cappedValues.courseLength()

        assert thresholdValue == buildScalaBigDecimal(threshold)
        assert DataUtils.compareAccommodationFees(buildScalaBigDecimal(feesCapped), cappedAccommodation) == true
        assert DataUtils.compareCourseLength(courseCapped, cappedCourseLength) == true

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate    | inLondon | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants | courseType | dependantsOnly || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(2036, 1, 30)  | LocalDate.of(2036, 4, 12)  | LocalDate.of(2036, 1, 14)  | false    | 0.00        | 0.00            | 0.00                  | 2          | "main"     | true           || 4080.00   || 0.00       || 0            || LocalDate.of(2036, 4, 19)
        LocalDate.of(2002, 12, 4)  | LocalDate.of(2003, 11, 6)  | LocalDate.of(2001, 12, 10) | true     | 0.00        | 0.00            | 0.00                  | 13         | "main"     | true           || 98865.00  || 0.00       || 9            || LocalDate.of(2004, 3, 6)
        LocalDate.of(2049, 11, 19) | LocalDate.of(2050, 8, 30)  | LocalDate.of(2048, 11, 22) | false    | 0.00        | 0.00            | 0.00                  | 10         | "main"     | true           || 61200.00  || 0.00       || 9            || LocalDate.of(2050, 12, 30)
        LocalDate.of(2017, 5, 17)  | LocalDate.of(2018, 4, 11)  | LocalDate.of(2016, 9, 30)  | true     | 0.00        | 0.00            | 0.00                  | 0          | "main"     | true           || 0.00      || 0.00       || 9            || LocalDate.of(2018, 8, 11)
        LocalDate.of(1990, 1, 31)  | LocalDate.of(1990, 3, 18)  | LocalDate.of(1989, 12, 13) | false    | 0.00        | 0.00            | 0.00                  | 2          | "main"     | true           || 2720.00   || 0.00       || 0            || LocalDate.of(1990, 3, 25)
        LocalDate.of(2035, 5, 26)  | LocalDate.of(2036, 2, 3)   | LocalDate.of(2035, 4, 25)  | false    | 0.00        | 0.00            | 0.00                  | 6          | "main"     | true           || 36720.00  || 0.00       || 0            || LocalDate.of(2036, 4, 3)
        LocalDate.of(1998, 11, 26) | LocalDate.of(1999, 3, 21)  | LocalDate.of(1998, 1, 27)  | true     | 0.00        | 0.00            | 0.00                  | 12         | "main"     | true           || 81120.00  || 0.00       || 0            || LocalDate.of(1999, 7, 21)
        LocalDate.of(1973, 11, 20) | LocalDate.of(1974, 2, 6)   | LocalDate.of(1973, 9, 13)  | false    | 0.00        | 0.00            | 0.00                  | 9          | "main"     | true           || 18360.00  || 0.00       || 0            || LocalDate.of(1974, 2, 13)
        LocalDate.of(1987, 12, 27) | LocalDate.of(1988, 1, 6)   | LocalDate.of(1987, 7, 31)  | true     | 0.00        | 0.00            | 0.00                  | 13         | "main"     | true           || 10985.00  || 0.00       || 0            || LocalDate.of(1988, 1, 13)
        LocalDate.of(2037, 9, 6)   | LocalDate.of(2037, 11, 13) | LocalDate.of(2036, 10, 7)  | false    | 0.00        | 0.00            | 0.00                  | 13         | "main"     | true           || 61880.00  || 0.00       || 0            || LocalDate.of(2038, 3, 13)
        LocalDate.of(2036, 8, 28)  | LocalDate.of(2037, 6, 14)  | LocalDate.of(2035, 8, 16)  | false    | 0.00        | 0.00            | 0.00                  | 8          | "main"     | true           || 48960.00  || 0.00       || 9            || LocalDate.of(2037, 10, 14)
        LocalDate.of(2050, 10, 15) | LocalDate.of(2051, 4, 4)   | LocalDate.of(2049, 12, 27) | true     | 0.00        | 0.00            | 0.00                  | 5          | "main"     | true           || 38025.00  || 0.00       || 0            || LocalDate.of(2051, 8, 4)
        LocalDate.of(1999, 5, 8)   | LocalDate.of(2000, 3, 23)  | LocalDate.of(1998, 12, 30) | true     | 0.00        | 0.00            | 0.00                  | 3          | "main"     | true           || 22815.00  || 0.00       || 9            || LocalDate.of(2000, 7, 23)
        LocalDate.of(2027, 10, 22) | LocalDate.of(2028, 9, 19)  | LocalDate.of(2027, 10, 20) | false    | 0.00        | 0.00            | 0.00                  | 13         | "main"     | true           || 79560.00  || 0.00       || 9            || LocalDate.of(2028, 11, 19)
        LocalDate.of(1993, 8, 21)  | LocalDate.of(1994, 1, 4)   | LocalDate.of(1992, 12, 17) | false    | 0.00        | 0.00            | 0.00                  | 4          | "main"     | true           || 24480.00  || 0.00       || 0            || LocalDate.of(1994, 5, 4)
        LocalDate.of(2033, 12, 31) | LocalDate.of(2034, 9, 28)  | LocalDate.of(2033, 5, 14)  | false    | 0.00        | 0.00            | 0.00                  | 4          | "main"     | true           || 24480.00  || 0.00       || 0            || LocalDate.of(2035, 1, 28)
        LocalDate.of(2030, 3, 14)  | LocalDate.of(2030, 4, 18)  | LocalDate.of(2029, 7, 5)   | true     | 0.00        | 0.00            | 0.00                  | 9          | "main"     | true           || 30420.00  || 0.00       || 0            || LocalDate.of(2030, 6, 18)
        LocalDate.of(2016, 5, 22)  | LocalDate.of(2017, 2, 4)   | LocalDate.of(2015, 7, 29)  | true     | 0.00        | 0.00            | 0.00                  | 6          | "main"     | true           || 45630.00  || 0.00       || 0            || LocalDate.of(2017, 6, 4)
        LocalDate.of(2028, 3, 11)  | LocalDate.of(2029, 3, 24)  | LocalDate.of(2027, 8, 13)  | false    | 0.00        | 0.00            | 0.00                  | 3          | "main"     | true           || 18360.00  || 0.00       || 9            || LocalDate.of(2029, 7, 24)
        LocalDate.of(2019, 10, 26) | LocalDate.of(2020, 3, 4)   | LocalDate.of(2019, 2, 27)  | true     | 0.00        | 0.00            | 0.00                  | 4          | "main"     | true           || 30420.00  || 0.00       || 0            || LocalDate.of(2020, 7, 4)
    }

    // All variants

    def "Tier 4 General - Check 'All variants'"() {

        def response = maintenanceThresholdCalculator.calculateGeneral(inLondon, buildScalaBigDecimal(tuitionFees), buildScalaBigDecimal(tuitionFeesPaid),
            buildScalaBigDecimal(accommodationFeesPaid), dependants, courseStartDate, courseEndDate, buildScalaOption(originalCourseStartDate),
            courseType == "pre-sessional", dependantsOnly)
        def thresholdValue = response._1
        def cappedValues = DataUtils.getCappedValues(response._2)
        def cappedAccommodation = cappedValues.accommodationFeesPaid()
        def cappedCourseLength = cappedValues.courseLength()

        assert thresholdValue == buildScalaBigDecimal(threshold)
        assert DataUtils.compareAccommodationFees(buildScalaBigDecimal(feesCapped), cappedAccommodation) == true
        assert DataUtils.compareCourseLength(courseCapped, cappedCourseLength) == true

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate    | inLondon | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants | courseType      | dependantsOnly || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(1994, 2, 11)  | LocalDate.of(1994, 12, 8)  | null                       | true     | 1554.14     | 383.53          | 0.00                  | 9          | "main"          | true           || 68445.00  || 0.00       || 9            || LocalDate.of(1995, 2, 8)
        LocalDate.of(2017, 7, 13)  | LocalDate.of(2018, 2, 8)   | LocalDate.of(2016, 11, 14) | false    | 4237.69     | 0.00            | 257.73                | 3          | "main"          | true           || 18360.00  || 0.00       || 0            || LocalDate.of(2018, 6, 8)
        LocalDate.of(2013, 9, 11)  | LocalDate.of(2014, 10, 7)  | LocalDate.of(2013, 2, 18)  | true     | 7298.77     | 550.21          | 1763.63               | 8          | "main"          | false          || 77708.56  || 1265.00    || 9            || LocalDate.of(2015, 2, 7)
        LocalDate.of(2016, 2, 18)  | LocalDate.of(2016, 10, 7)  | LocalDate.of(2016, 1, 6)   | false    | 6555.38     | 5027.78         | 0.00                  | 11         | "main"          | false          || 76967.60  || 0.00       || 0            || LocalDate.of(2016, 12, 7)
        LocalDate.of(2039, 1, 5)   | LocalDate.of(2039, 1, 24)  | LocalDate.of(2038, 3, 10)  | false    | 4859.44     | 0.00            | 7.56                  | 2          | "main"          | true           || 4080.00   || 0.00       || 0            || LocalDate.of(2039, 3, 24)
        LocalDate.of(2020, 2, 23)  | LocalDate.of(2020, 4, 6)   | null                       | true     | 1837.81     | 9312.14         | 349.27                | 0          | "pre-sessional" | true           || 0.00      || 0.00       || 0            || LocalDate.of(2020, 5, 6)
        LocalDate.of(2044, 10, 31) | LocalDate.of(2045, 3, 24)  | LocalDate.of(2043, 12, 23) | false    | 1590.25     | 0.00            | 0.00                  | 12         | "main"          | false          || 80105.25  || 0.00       || 0            || LocalDate.of(2045, 7, 24)
        LocalDate.of(2034, 5, 12)  | LocalDate.of(2035, 1, 13)  | null                       | false    | 7903.45     | 0.00            | 0.00                  | 12         | "main"          | true           || 73440.00  || 0.00       || 0            || LocalDate.of(2035, 3, 13)
        LocalDate.of(2035, 3, 23)  | LocalDate.of(2035, 12, 16) | LocalDate.of(2034, 11, 14) | false    | 0.00        | 0.00            | 0.00                  | 11         | "main"          | false          || 76455.00  || 0.00       || 0            || LocalDate.of(2036, 4, 16)
        LocalDate.of(2011, 10, 29) | LocalDate.of(2012, 9, 5)   | null                       | false    | 789.46      | 7264.74         | 1185.29               | 3          | "main"          | false          || 26309.71  || 0.00       || 9            || LocalDate.of(2012, 11, 5)
        LocalDate.of(2006, 7, 28)  | LocalDate.of(2007, 2, 28)  | null                       | true     | 5717.38     | 7539.87         | 188.31                | 2          | "pre-sessional" | false          || 25141.69  || 0.00       || 0            || LocalDate.of(2007, 4, 28)
        LocalDate.of(2001, 11, 16) | LocalDate.of(2002, 6, 26)  | null                       | true     | 2118.03     | 5096.08         | 1006.84               | 12         | "pre-sessional" | true           || 91260.00  || 0.00       || 0            || LocalDate.of(2002, 8, 26)
        LocalDate.of(1986, 4, 7)   | LocalDate.of(1986, 12, 26) | LocalDate.of(1986, 2, 26)  | true     | 903.14      | 0.00            | 822.56                | 3          | "main"          | false          || 34280.58  || 0.00       || 0            || LocalDate.of(1987, 2, 26)
        LocalDate.of(2027, 5, 22)  | LocalDate.of(2027, 9, 25)  | null                       | false    | 0.00        | 0.00            | 0.00                  | 0          | "main"          | true           || 0.00      || 0.00       || 0            || LocalDate.of(2027, 10, 2)
        LocalDate.of(2011, 4, 27)  | LocalDate.of(2012, 2, 19)  | LocalDate.of(2010, 8, 17)  | false    | 2869.48     | 8481.63         | 267.54                | 14         | "main"          | false          || 94547.46  || 0.00       || 9            || LocalDate.of(2012, 6, 19)
        LocalDate.of(1993, 3, 18)  | LocalDate.of(1993, 7, 12)  | null                       | true     | 323.13      | 0.00            | 0.00                  | 0          | "pre-sessional" | true           || 0.00      || 0.00       || 0            || LocalDate.of(1993, 8, 12)
        LocalDate.of(2046, 8, 20)  | LocalDate.of(2046, 8, 31)  | null                       | false    | 9665.45     | 0.00            | 251.38                | 0          | "main"          | false          || 10429.07  || 0.00       || 0            || LocalDate.of(2046, 9, 7)
        LocalDate.of(2028, 2, 25)  | LocalDate.of(2028, 11, 26) | LocalDate.of(2027, 2, 8)   | false    | 2759.61     | 6580.59         | 0.00                  | 1          | "main"          | false          || 15255.00  || 0.00       || 9            || LocalDate.of(2029, 3, 26)
        LocalDate.of(2034, 12, 29) | LocalDate.of(2035, 12, 28) | null                       | true     | 9448.87     | 6329.82         | 97.13                 | 4          | "pre-sessional" | false          || 44826.92  || 0.00       || 9            || LocalDate.of(2036, 4, 28)
        LocalDate.of(2050, 8, 9)   | LocalDate.of(2051, 9, 4)   | LocalDate.of(2050, 5, 22)  | true     | 1875.36     | 4160.92         | 0.00                  | 14         | "main"          | true           || 106470.00 || 0.00       || 9            || LocalDate.of(2052, 1, 4)
        LocalDate.of(2050, 3, 26)  | LocalDate.of(2050, 11, 16) | LocalDate.of(2049, 7, 15)  | false    | 8651.26     | 1990.91         | 646.25                | 2          | "main"          | true           || 12240.00  || 0.00       || 0            || LocalDate.of(2051, 3, 16)
        LocalDate.of(1993, 1, 5)   | LocalDate.of(1993, 1, 30)  | LocalDate.of(1992, 8, 19)  | true     | 6581.39     | 3655.30         | 0.00                  | 11         | "main"          | false          || 22781.09  || 0.00       || 0            || LocalDate.of(1993, 2, 6)
        LocalDate.of(2007, 10, 5)  | LocalDate.of(2008, 4, 5)   | null                       | false    | 9493.68     | 9533.27         | 0.00                  | 2          | "pre-sessional" | false          || 19345.00  || 0.00       || 0            || LocalDate.of(2008, 6, 5)
        LocalDate.of(2029, 2, 16)  | LocalDate.of(2029, 6, 4)   | LocalDate.of(2028, 8, 18)  | false    | 3610.83     | 0.00            | 473.05                | 1          | "main"          | false          || 11277.78  || 0.00       || 0            || LocalDate.of(2029, 8, 4)
        LocalDate.of(2038, 6, 18)  | LocalDate.of(2039, 1, 24)  | LocalDate.of(2038, 5, 2)   | true     | 6460.72     | 4873.70         | 0.00                  | 1          | "main"          | true           || 7605.00   || 0.00       || 0            || LocalDate.of(2039, 3, 24)
    }

}
