package uk.gov.digital.ho.proving.financialstatus.api.test.tier4

import spock.lang.Specification
import uk.gov.digital.ho.proving.financialstatus.api.test.DataUtils
import uk.gov.digital.ho.proving.financialstatus.domain.MaintenanceThresholdCalculator

import java.time.LocalDate

import static uk.gov.digital.ho.proving.financialstatus.api.test.DataUtils.buildScalaBigDecimal
import static uk.gov.digital.ho.proving.financialstatus.api.test.DataUtils.buildScalaOption


class NonDoctorateMaintenanceThresholdCalculatorTest extends Specification {

    MaintenanceThresholdCalculator maintenanceThresholdCalculator = TestUtilsTier4.maintenanceThresholdServiceBuilder()


    def "Tier 4 Non Doctorate - Check 'Non Inner London Borough'"() {

        expect:
        def response = maintenanceThresholdCalculator.calculateNonDoctorate(inLondon, buildScalaBigDecimal(tuitionFees), buildScalaBigDecimal(tuitionFeesPaid),
            buildScalaBigDecimal(accommodationFeesPaid), dependants, courseStartDate, courseEndDate, buildScalaOption(originalCourseStartDate),
            originalCourseStartDate != null, courseType == "pre-sessional")

        assert (response._1 == buildScalaBigDecimal(threshold))

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate    | inLondon | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants | courseType      || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(2025, 4, 15)  | LocalDate.of(2026, 4, 27)  | LocalDate.of(2024, 6, 17)  | false    | 8888.29     | 0.00            | 0.00                  | 0          | "main"          || 18023.29  || 0.00       || 9            || LocalDate.of(2026, 8, 27)
        LocalDate.of(2009, 2, 19)  | LocalDate.of(2010, 1, 6)   | LocalDate.of(2008, 6, 5)   | false    | 2197.60     | 0.00            | 0.00                  | 4          | "main"          || 35812.60  || 0.00       || 9            || LocalDate.of(2010, 5, 6)
        LocalDate.of(2044, 9, 28)  | LocalDate.of(2045, 2, 27)  | LocalDate.of(2044, 3, 29)  | false    | 6661.13     | 7865.70         | 666.49                | 14         | "main"          || 71048.51  || 0.00       || 0            || LocalDate.of(2045, 4, 27)
        LocalDate.of(1973, 8, 8)   | LocalDate.of(1973, 9, 19)  | null                       | false    | 5765.01     | 2991.83         | 565.41                | 0          | "main"          || 4237.77   || 0.00       || 0            || LocalDate.of(1973, 9, 26)
        LocalDate.of(1977, 2, 18)  | LocalDate.of(1978, 1, 4)   | LocalDate.of(1976, 12, 4)  | false    | 8944.51     | 5331.02         | 1680.00               | 14         | "main"          || 97163.49  || 1265.00    || 9            || LocalDate.of(1978, 5, 4)
        LocalDate.of(1975, 3, 18)  | LocalDate.of(1975, 4, 30)  | null                       | false    | 4435.51     | 7243.73         | 614.42                | 0          | "main"          || 1415.58   || 0.00       || 0            || LocalDate.of(1975, 5, 7)
        LocalDate.of(2022, 10, 1)  | LocalDate.of(2022, 10, 27) | null                       | false    | 5952.66     | 5343.58         | 0.00                  | 0          | "main"          || 1624.08   || 0.00       || 0            || LocalDate.of(2022, 11, 3)
        LocalDate.of(1978, 7, 15)  | LocalDate.of(1979, 5, 19)  | LocalDate.of(1978, 2, 22)  | false    | 5723.14     | 5526.09         | 1762.15               | 12         | "main"          || 81507.05  || 1265.00    || 9            || LocalDate.of(1979, 9, 19)
        LocalDate.of(2000, 11, 17) | LocalDate.of(2001, 6, 28)  | LocalDate.of(2000, 9, 5)   | false    | 3582.49     | 0.00            | 0.00                  | 11         | "main"          || 79022.49  || 0.00       || 0            || LocalDate.of(2001, 8, 28)
        LocalDate.of(2014, 4, 12)  | LocalDate.of(2014, 9, 8)   | null                       | false    | 3961.20     | 0.00            | 1156.81               | 0          | "pre-sessional" || 7879.39   || 0.00       || 0            || LocalDate.of(2014, 10, 8)
        LocalDate.of(2022, 4, 21)  | LocalDate.of(2023, 4, 12)  | LocalDate.of(2021, 5, 23)  | false    | 9688.72     | 0.00            | 183.82                | 13         | "main"          || 98199.90  || 0.00       || 9            || LocalDate.of(2023, 8, 12)
        LocalDate.of(2052, 3, 10)  | LocalDate.of(2053, 2, 15)  | LocalDate.of(2051, 11, 16) | false    | 6999.51     | 9019.06         | 690.80                | 13         | "main"          || 88004.20  || 0.00       || 9            || LocalDate.of(2053, 6, 15)
        LocalDate.of(1992, 11, 20) | LocalDate.of(1993, 1, 3)   | null                       | false    | 3362.99     | 0.00            | 0.00                  | 0          | "pre-sessional" || 5392.99   || 0.00       || 0            || LocalDate.of(1993, 2, 3)
        LocalDate.of(2012, 2, 3)   | LocalDate.of(2012, 7, 2)   | null                       | false    | 8559.94     | 0.00            | 32.58                 | 0          | "main"          || 13602.36  || 0.00       || 0            || LocalDate.of(2012, 7, 9)
        LocalDate.of(2022, 5, 31)  | LocalDate.of(2022, 8, 21)  | LocalDate.of(2022, 4, 13)  | false    | 7391.54     | 0.00            | 635.74                | 6          | "main"          || 22040.80  || 0.00       || 0            || LocalDate.of(2022, 8, 28)
        LocalDate.of(1979, 3, 5)   | LocalDate.of(1979, 5, 12)  | null                       | false    | 7631.97     | 2606.96         | 0.00                  | 0          | "main"          || 8070.01   || 0.00       || 0            || LocalDate.of(1979, 5, 19)
        LocalDate.of(2039, 5, 17)  | LocalDate.of(2039, 9, 2)   | LocalDate.of(2039, 2, 17)  | false    | 2623.07     | 0.00            | 1217.45               | 14         | "main"          || 62585.62  || 0.00       || 0            || LocalDate.of(2039, 11, 2)
        LocalDate.of(2041, 2, 15)  | LocalDate.of(2041, 4, 18)  | null                       | false    | 8960.94     | 2772.66         | 0.00                  | 0          | "pre-sessional" || 9233.28   || 0.00       || 0            || LocalDate.of(2041, 5, 18)
        LocalDate.of(2024, 10, 15) | LocalDate.of(2025, 10, 10) | null                       | false    | 9837.36     | 0.00            | 637.51                | 10         | "pre-sessional" || 79534.85  || 0.00       || 9            || LocalDate.of(2025, 12, 10)
        LocalDate.of(1988, 1, 17)  | LocalDate.of(1988, 10, 25) | LocalDate.of(1987, 3, 1)   | false    | 3439.64     | 0.00            | 1565.74               | 7          | "main"          || 54149.64  || 1265.00    || 9            || LocalDate.of(1989, 2, 25)
        LocalDate.of(1989, 2, 3)   | LocalDate.of(1989, 8, 19)  | null                       | false    | 3626.97     | 0.00            | 203.67                | 9          | "pre-sessional" || 65608.30  || 0.00       || 0            || LocalDate.of(1989, 10, 19)
        LocalDate.of(2048, 4, 14)  | LocalDate.of(2048, 12, 14) | null                       | false    | 5131.71     | 0.00            | 0.00                  | 11         | "main"          || 81586.71  || 0.00       || 0            || LocalDate.of(2049, 2, 14)
        LocalDate.of(2021, 12, 23) | LocalDate.of(2022, 4, 19)  | LocalDate.of(2020, 12, 21) | false    | 8527.77     | 8417.55         | 0.00                  | 0          | "main"          || 4170.22   || 0.00       || 0            || LocalDate.of(2022, 8, 19)
        LocalDate.of(2003, 4, 3)   | LocalDate.of(2003, 12, 27) | null                       | false    | 888.77      | 5153.26         | 0.00                  | 4          | "pre-sessional" || 33615.00  || 0.00       || 0            || LocalDate.of(2004, 2, 27)
        LocalDate.of(2026, 7, 12)  | LocalDate.of(2027, 5, 27)  | null                       | false    | 6402.22     | 0.00            | 0.00                  | 4          | "pre-sessional" || 40017.22  || 0.00       || 9            || LocalDate.of(2027, 7, 27)
    }

    def "Tier 4 Non Doctorate - Check 'Inner London Borough'"() {

        expect:
        def response = maintenanceThresholdCalculator.calculateNonDoctorate(inLondon, buildScalaBigDecimal(tuitionFees), buildScalaBigDecimal(tuitionFeesPaid),
            buildScalaBigDecimal(accommodationFeesPaid), dependants, courseStartDate, courseEndDate, buildScalaOption(originalCourseStartDate),
            originalCourseStartDate != null, courseType == "pre-sessional")

        assert (response._1 == buildScalaBigDecimal(threshold))

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate    | inLondon | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants | courseType      || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(2045, 3, 2)   | LocalDate.of(2046, 1, 13)  | LocalDate.of(2045, 1, 13)  | true     | 1275.29     | 8156.45         | 0.00                  | 10         | "main"          || 87435.00  || 0.00       || 9            || LocalDate.of(2046, 5, 13)
        LocalDate.of(1987, 8, 26)  | LocalDate.of(1987, 10, 29) | null                       | true     | 6814.09     | 0.00            | 0.00                  | 0          | "main"          || 10609.09  || 0.00       || 0            || LocalDate.of(1987, 11, 5)
        LocalDate.of(2016, 3, 3)   | LocalDate.of(2016, 4, 2)   | null                       | true     | 4669.56     | 0.00            | 953.81                | 0          | "main"          || 4980.75   || 0.00       || 0            || LocalDate.of(2016, 4, 9)
        LocalDate.of(2012, 11, 17) | LocalDate.of(2013, 4, 1)   | LocalDate.of(2012, 3, 17)  | true     | 4551.46     | 0.00            | 1792.33               | 14         | "main"          || 116081.46 || 1265.00    || 0            || LocalDate.of(2013, 8, 1)
        LocalDate.of(1998, 3, 20)  | LocalDate.of(1998, 5, 16)  | null                       | true     | 3696.21     | 192.09          | 1800.41               | 0          | "main"          || 4769.12   || 1265.00    || 0            || LocalDate.of(1998, 5, 23)
        LocalDate.of(1980, 12, 24) | LocalDate.of(1981, 8, 14)  | null                       | true     | 4064.82     | 4965.01         | 0.00                  | 8          | "main"          || 70960.00  || 0.00       || 0            || LocalDate.of(1981, 10, 14)
        LocalDate.of(1993, 2, 1)   | LocalDate.of(1993, 3, 28)  | null                       | true     | 2523.95     | 0.00            | 1795.37               | 0          | "pre-sessional" || 3788.95   || 1265.00    || 0            || LocalDate.of(1993, 4, 28)
        LocalDate.of(1996, 5, 28)  | LocalDate.of(1997, 3, 24)  | null                       | true     | 3767.67     | 0.00            | 1621.38               | 13         | "main"          || 112752.67 || 1265.00    || 9            || LocalDate.of(1997, 5, 24)
        LocalDate.of(2042, 5, 12)  | LocalDate.of(2042, 7, 2)   | null                       | true     | 1601.96     | 4128.51         | 0.00                  | 0          | "pre-sessional" || 2530.00   || 0.00       || 0            || LocalDate.of(2042, 8, 2)
        LocalDate.of(2019, 8, 25)  | LocalDate.of(2019, 9, 19)  | null                       | true     | 2124.80     | 0.00            | 0.00                  | 0          | "pre-sessional" || 3389.80   || 0.00       || 0            || LocalDate.of(2019, 10, 19)
        LocalDate.of(1991, 5, 25)  | LocalDate.of(1991, 10, 24) | LocalDate.of(1990, 4, 22)  | true     | 9382.08     | 3976.90         | 572.20                | 2          | "main"          || 26367.98  || 0.00       || 0            || LocalDate.of(1992, 2, 24)
        LocalDate.of(2046, 12, 28) | LocalDate.of(2047, 9, 27)  | null                       | true     | 3363.32     | 1963.25         | 0.00                  | 8          | "pre-sessional" || 73625.07  || 0.00       || 0            || LocalDate.of(2047, 11, 27)
        LocalDate.of(1998, 10, 9)  | LocalDate.of(1998, 12, 6)  | LocalDate.of(1998, 7, 8)   | true     | 6127.97     | 0.00            | 1213.00               | 12         | "main"          || 37864.97  || 0.00       || 0            || LocalDate.of(1998, 12, 13)
        LocalDate.of(2032, 9, 27)  | LocalDate.of(2033, 8, 15)  | null                       | true     | 4645.12     | 0.00            | 0.00                  | 6          | "main"          || 61660.12  || 0.00       || 9            || LocalDate.of(2033, 10, 15)
        LocalDate.of(1999, 8, 22)  | LocalDate.of(2000, 3, 16)  | null                       | true     | 9859.46     | 8284.70         | 71.22                 | 0          | "main"          || 10358.54  || 0.00       || 0            || LocalDate.of(2000, 5, 16)
        LocalDate.of(2021, 3, 15)  | LocalDate.of(2021, 6, 10)  | null                       | true     | 8535.06     | 0.00            | 1159.08               | 0          | "pre-sessional" || 11170.98  || 0.00       || 0            || LocalDate.of(2021, 7, 10)
        LocalDate.of(2031, 12, 24) | LocalDate.of(2033, 1, 23)  | null                       | true     | 332.49      | 6598.68         | 1051.86               | 8          | "pre-sessional" || 71173.14  || 0.00       || 9            || LocalDate.of(2033, 5, 23)
        LocalDate.of(1980, 1, 13)  | LocalDate.of(1980, 2, 14)  | null                       | true     | 2598.26     | 2203.32         | 1242.82               | 0          | "main"          || 1682.12   || 0.00       || 0            || LocalDate.of(1980, 2, 21)
        LocalDate.of(1992, 10, 19) | LocalDate.of(1993, 8, 30)  | LocalDate.of(1992, 5, 25)  | true     | 8800.57     | 0.00            | 1414.16               | 7          | "main"          || 72155.57  || 1265.00    || 9            || LocalDate.of(1993, 12, 30)
        LocalDate.of(1991, 12, 13) | LocalDate.of(1992, 5, 30)  | null                       | true     | 2598.50     | 0.00            | 0.00                  | 0          | "main"          || 10188.50  || 0.00       || 0            || LocalDate.of(1992, 6, 6)
        LocalDate.of(1982, 3, 22)  | LocalDate.of(1982, 5, 19)  | null                       | true     | 5513.51     | 0.00            | 0.00                  | 0          | "main"          || 8043.51   || 0.00       || 0            || LocalDate.of(1982, 5, 26)
        LocalDate.of(2019, 11, 10) | LocalDate.of(2020, 9, 18)  | null                       | true     | 1089.82     | 5648.26         | 0.00                  | 12         | "pre-sessional" || 102645.00 || 0.00       || 9            || LocalDate.of(2020, 11, 18)
        LocalDate.of(2031, 2, 3)   | LocalDate.of(2031, 7, 23)  | LocalDate.of(2030, 12, 22) | true     | 8336.70     | 0.00            | 182.22                | 6          | "main"          || 56304.48  || 0.00       || 0            || LocalDate.of(2031, 9, 23)
        LocalDate.of(2026, 2, 11)  | LocalDate.of(2026, 4, 16)  | LocalDate.of(2025, 2, 24)  | true     | 7920.98     | 9228.33         | 0.00                  | 1          | "main"          || 9710.00   || 0.00       || 0            || LocalDate.of(2026, 8, 16)
        LocalDate.of(1985, 8, 27)  | LocalDate.of(1985, 9, 29)  | null                       | true     | 8556.72     | 6983.59         | 0.00                  | 0          | "main"          || 4103.13   || 0.00       || 0            || LocalDate.of(1985, 10, 6)
    }

    def "Tier 4 Non Doctorate - Check 'Tuition Fees paid'"() {

        expect:
        def response = maintenanceThresholdCalculator.calculateNonDoctorate(inLondon, buildScalaBigDecimal(tuitionFees), buildScalaBigDecimal(tuitionFeesPaid),
            buildScalaBigDecimal(accommodationFeesPaid), dependants, courseStartDate, courseEndDate, buildScalaOption(originalCourseStartDate),
            originalCourseStartDate != null, courseType == "pre-sessional")

        assert (response._1 == buildScalaBigDecimal(threshold))


        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate    | inLondon | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants | courseType      || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(2049, 7, 23)  | LocalDate.of(2049, 11, 25) | LocalDate.of(2049, 7, 22)  | true     | 6299.21     | 7097.34         | 1503.33               | 10         | "main"          || 47310.00  || 1265.00    || 0            || LocalDate.of(2049, 12, 2)
        LocalDate.of(2023, 1, 25)  | LocalDate.of(2023, 4, 13)  | LocalDate.of(2023, 1, 12)  | false    | 9837.59     | 3001.53         | 713.43                | 12         | "main"          || 33647.63  || 0.00       || 0            || LocalDate.of(2023, 4, 20)
        LocalDate.of(1998, 8, 2)   | LocalDate.of(1998, 12, 25) | null                       | true     | 2914.70     | 8627.16         | 1584.83               | 0          | "main"          || 5060.00   || 1265.00    || 0            || LocalDate.of(1999, 1, 1)
        LocalDate.of(1977, 11, 10) | LocalDate.of(1978, 7, 11)  | null                       | true     | 308.18      | 6974.82         | 316.77                | 5          | "main"          || 49093.23  || 0.00       || 0            || LocalDate.of(1978, 9, 11)
        LocalDate.of(1974, 3, 5)   | LocalDate.of(1974, 11, 17) | LocalDate.of(1973, 6, 25)  | false    | 8858.71     | 4193.35         | 0.00                  | 11         | "main"          || 81120.36  || 0.00       || 0            || LocalDate.of(1975, 3, 17)
        LocalDate.of(2011, 7, 26)  | LocalDate.of(2012, 5, 13)  | null                       | true     | 4744.19     | 3287.26         | 0.00                  | 8          | "main"          || 73681.93  || 0.00       || 9            || LocalDate.of(2012, 7, 13)
        LocalDate.of(2008, 8, 29)  | LocalDate.of(2009, 8, 28)  | LocalDate.of(2008, 8, 8)   | false    | 4418.40     | 2512.44         | 0.00                  | 7          | "main"          || 53880.96  || 0.00       || 9            || LocalDate.of(2009, 12, 28)
        LocalDate.of(2052, 1, 5)   | LocalDate.of(2052, 10, 2)  | null                       | false    | 1682.77     | 6031.92         | 0.00                  | 4          | "pre-sessional" || 33615.00  || 0.00       || 0            || LocalDate.of(2052, 12, 2)
        LocalDate.of(2031, 6, 19)  | LocalDate.of(2031, 8, 22)  | LocalDate.of(2030, 5, 31)  | true     | 1328.19     | 1177.23         | 1533.16               | 10         | "main"          || 61830.96  || 1265.00    || 0            || LocalDate.of(2031, 12, 22)
        LocalDate.of(2034, 9, 21)  | LocalDate.of(2035, 3, 12)  | LocalDate.of(2034, 3, 6)   | false    | 6447.06     | 7071.05         | 826.71                | 10         | "main"          || 66463.29  || 0.00       || 0            || LocalDate.of(2035, 7, 12)
        LocalDate.of(2033, 9, 17)  | LocalDate.of(2034, 10, 12) | null                       | false    | 8992.38     | 6668.33         | 719.60                | 14         | "main"          || 96419.45  || 0.00       || 9            || LocalDate.of(2035, 2, 12)
        LocalDate.of(2045, 7, 3)   | LocalDate.of(2046, 5, 2)   | LocalDate.of(2044, 7, 27)  | false    | 8614.09     | 1310.34         | 0.00                  | 10         | "main"          || 77638.75  || 0.00       || 9            || LocalDate.of(2046, 9, 2)
        LocalDate.of(2051, 3, 29)  | LocalDate.of(2051, 5, 2)   | null                       | false    | 1471.41     | 2631.15         | 693.71                | 0          | "pre-sessional" || 1336.29   || 0.00       || 0            || LocalDate.of(2051, 6, 2)
        LocalDate.of(2023, 9, 13)  | LocalDate.of(2023, 11, 21) | LocalDate.of(2022, 10, 21) | false    | 5718.26     | 2187.73         | 0.00                  | 7          | "main"          || 39895.53  || 0.00       || 0            || LocalDate.of(2024, 3, 21)
        LocalDate.of(1995, 10, 21) | LocalDate.of(1996, 1, 24)  | null                       | false    | 8924.06     | 39.51           | 564.66                | 0          | "pre-sessional" || 12379.89  || 0.00       || 0            || LocalDate.of(1996, 2, 24)
        LocalDate.of(2003, 2, 23)  | LocalDate.of(2003, 6, 11)  | null                       | false    | 6570.40     | 7968.18         | 906.97                | 0          | "main"          || 3153.03   || 0.00       || 0            || LocalDate.of(2003, 6, 18)
        LocalDate.of(1991, 9, 22)  | LocalDate.of(1992, 5, 22)  | LocalDate.of(1991, 5, 2)   | false    | 2817.49     | 9013.45         | 1923.93               | 13         | "main"          || 87430.00  || 1265.00    || 0            || LocalDate.of(1992, 9, 22)
        LocalDate.of(2041, 1, 31)  | LocalDate.of(2041, 2, 13)  | null                       | false    | 393.09      | 9069.93         | 0.00                  | 0          | "pre-sessional" || 1015.00   || 0.00       || 0            || LocalDate.of(2041, 3, 13)
        LocalDate.of(2027, 3, 21)  | LocalDate.of(2027, 8, 26)  | LocalDate.of(2026, 6, 19)  | true     | 5863.34     | 1273.12         | 560.94                | 7          | "main"          || 64854.28  || 0.00       || 0            || LocalDate.of(2027, 12, 26)
        LocalDate.of(2021, 9, 19)  | LocalDate.of(2021, 10, 3)  | LocalDate.of(2021, 3, 14)  | false    | 6182.20     | 5194.10         | 0.00                  | 13         | "main"          || 28523.10  || 0.00       || 0            || LocalDate.of(2021, 12, 3)
        LocalDate.of(1993, 12, 14) | LocalDate.of(1994, 8, 22)  | null                       | false    | 4037.43     | 2777.03         | 1417.02               | 10         | "main"          || 70330.40  || 1265.00    || 0            || LocalDate.of(1994, 10, 22)
        LocalDate.of(1989, 8, 1)   | LocalDate.of(1990, 1, 27)  | LocalDate.of(1988, 11, 13) | false    | 5520.92     | 3159.68         | 1249.98               | 13         | "main"          || 86761.26  || 0.00       || 0            || LocalDate.of(1990, 5, 27)
        LocalDate.of(2024, 9, 3)   | LocalDate.of(2025, 5, 31)  | LocalDate.of(2024, 5, 17)  | false    | 9129.45     | 3555.08         | 1217.13               | 0          | "main"          || 13492.24  || 0.00       || 0            || LocalDate.of(2025, 9, 30)
        LocalDate.of(2042, 10, 23) | LocalDate.of(2043, 2, 19)  | null                       | true     | 2131.62     | 2574.24         | 1840.28               | 0          | "main"          || 3795.00   || 1265.00    || 0            || LocalDate.of(2043, 2, 26)
        LocalDate.of(1973, 10, 29) | LocalDate.of(1974, 2, 25)  | null                       | false    | 3807.11     | 7595.73         | 1464.67               | 0          | "main"          || 2795.00   || 1265.00    || 0            || LocalDate.of(1974, 3, 4)
    }

    def "Tier 4 Non Doctorate - Check 'Accommodation Fees paid'"() {

        expect:
        def response = maintenanceThresholdCalculator.calculateNonDoctorate(inLondon, buildScalaBigDecimal(tuitionFees), buildScalaBigDecimal(tuitionFeesPaid),
            buildScalaBigDecimal(accommodationFeesPaid), dependants, courseStartDate, courseEndDate, buildScalaOption(originalCourseStartDate),
            originalCourseStartDate != null, courseType == "pre-sessional")

        assert (response._1 == buildScalaBigDecimal(threshold))

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate    | inLondon | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants | courseType      || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(2006, 9, 12)  | LocalDate.of(2006, 11, 4)  | LocalDate.of(2006, 1, 18)  | false    | 4787.04     | 7676.13         | 962.90                | 7          | "main"          || 20107.10  || 0.00       || 0            || LocalDate.of(2007, 1, 4)
        LocalDate.of(1999, 9, 9)   | LocalDate.of(2000, 7, 16)  | null                       | false    | 3252.61     | 0.00            | 1975.70               | 13         | "main"          || 90682.61  || 1265.00    || 9            || LocalDate.of(2000, 9, 16)
        LocalDate.of(1983, 2, 2)   | LocalDate.of(1983, 10, 11) | null                       | true     | 2361.47     | 0.00            | 1073.39               | 0          | "main"          || 12673.08  || 0.00       || 0            || LocalDate.of(1983, 12, 11)
        LocalDate.of(1974, 11, 12) | LocalDate.of(1975, 10, 13) | null                       | false    | 9933.79     | 8212.23         | 1133.28               | 5          | "main"          || 40323.28  || 0.00       || 9            || LocalDate.of(1975, 12, 13)
        LocalDate.of(2027, 5, 4)   | LocalDate.of(2027, 10, 7)  | null                       | false    | 596.11      | 0.00            | 442.86                | 0          | "main"          || 6243.25   || 0.00       || 0            || LocalDate.of(2027, 10, 14)
        LocalDate.of(2044, 4, 2)   | LocalDate.of(2044, 6, 14)  | null                       | true     | 587.32      | 0.00            | 75.42                 | 0          | "pre-sessional" || 4306.90   || 0.00       || 0            || LocalDate.of(2044, 7, 14)
        LocalDate.of(1997, 5, 29)  | LocalDate.of(1998, 1, 20)  | LocalDate.of(1997, 5, 6)   | false    | 3779.51     | 7013.36         | 24.21                 | 12         | "main"          || 81535.79  || 0.00       || 0            || LocalDate.of(1998, 3, 20)
        LocalDate.of(2046, 8, 13)  | LocalDate.of(2046, 11, 28) | null                       | false    | 1627.96     | 0.00            | 1502.54               | 0          | "pre-sessional" || 4422.96   || 1265.00    || 0            || LocalDate.of(2046, 12, 28)
        LocalDate.of(1992, 8, 16)  | LocalDate.of(1993, 7, 26)  | LocalDate.of(1991, 11, 7)  | true     | 9645.51     | 3754.14         | 1496.97               | 2          | "main"          || 31221.37  || 1265.00    || 9            || LocalDate.of(1993, 11, 26)
        LocalDate.of(1983, 7, 2)   | LocalDate.of(1984, 4, 9)   | LocalDate.of(1982, 11, 26) | false    | 11.59       | 177.13          | 186.77                | 3          | "main"          || 27308.23  || 0.00       || 9            || LocalDate.of(1984, 8, 9)
        LocalDate.of(1986, 6, 11)  | LocalDate.of(1986, 6, 30)  | null                       | true     | 1261.58     | 0.00            | 82.32                 | 0          | "pre-sessional" || 2444.26   || 0.00       || 0            || LocalDate.of(1986, 7, 30)
        LocalDate.of(1991, 11, 1)  | LocalDate.of(1991, 11, 5)  | LocalDate.of(1990, 10, 7)  | false    | 7946.44     | 8437.71         | 1849.65               | 8          | "main"          || 26950.00  || 1265.00    || 0            || LocalDate.of(1992, 3, 5)
        LocalDate.of(2031, 4, 24)  | LocalDate.of(2032, 2, 13)  | LocalDate.of(2030, 10, 24) | false    | 9623.37     | 3117.79         | 1665.05               | 3          | "main"          || 32735.58  || 1265.00    || 9            || LocalDate.of(2032, 6, 13)
        LocalDate.of(2006, 6, 11)  | LocalDate.of(2006, 6, 12)  | null                       | false    | 9513.00     | 0.00            | 1151.79               | 0          | "main"          || 9376.21   || 0.00       || 0            || LocalDate.of(2006, 6, 19)
        LocalDate.of(2018, 10, 3)  | LocalDate.of(2019, 10, 15) | LocalDate.of(2018, 7, 28)  | false    | 667.33      | 5306.83         | 1580.33               | 14         | "main"          || 93550.00  || 1265.00    || 9            || LocalDate.of(2020, 2, 15)
        LocalDate.of(1974, 12, 15) | LocalDate.of(1976, 1, 13)  | null                       | false    | 4513.62     | 0.00            | 1881.44               | 4          | "main"          || 36863.62  || 1265.00    || 9            || LocalDate.of(1976, 5, 13)
        LocalDate.of(1998, 10, 9)  | LocalDate.of(1998, 12, 24) | null                       | false    | 2003.26     | 3871.48         | 1574.64               | 0          | "main"          || 1780.00   || 1265.00    || 0            || LocalDate.of(1998, 12, 31)
        LocalDate.of(2003, 3, 26)  | LocalDate.of(2003, 7, 15)  | null                       | true     | 7305.24     | 7087.06         | 854.28                | 0          | "pre-sessional" || 4423.90   || 0.00       || 0            || LocalDate.of(2003, 8, 15)
        LocalDate.of(1985, 2, 7)   | LocalDate.of(1985, 6, 25)  | null                       | true     | 8926.65     | 8498.91         | 1270.58               | 0          | "main"          || 5487.74   || 1265.00    || 0            || LocalDate.of(1985, 7, 2)
        LocalDate.of(2005, 7, 9)   | LocalDate.of(2005, 8, 9)   | LocalDate.of(2005, 3, 8)   | false    | 3169.79     | 3356.08         | 1353.29               | 6          | "main"          || 8925.00   || 1265.00    || 0            || LocalDate.of(2005, 8, 16)
        LocalDate.of(2008, 3, 29)  | LocalDate.of(2008, 9, 25)  | LocalDate.of(2008, 1, 22)  | false    | 4644.75     | 0.00            | 893.12                | 9          | "main"          || 58801.63  || 0.00       || 0            || LocalDate.of(2008, 11, 25)
        LocalDate.of(2035, 4, 25)  | LocalDate.of(2035, 9, 26)  | LocalDate.of(2035, 4, 8)   | false    | 1213.12     | 0.00            | 1344.86               | 9          | "main"          || 42758.12  || 1265.00    || 0            || LocalDate.of(2035, 10, 3)
        LocalDate.of(2035, 7, 15)  | LocalDate.of(2036, 6, 3)   | LocalDate.of(2035, 7, 3)   | false    | 7331.62     | 3461.08         | 373.97                | 6          | "main"          || 49351.57  || 0.00       || 9            || LocalDate.of(2036, 8, 3)
        LocalDate.of(2041, 4, 10)  | LocalDate.of(2042, 5, 11)  | LocalDate.of(2040, 5, 27)  | false    | 150.73      | 0.00            | 651.16                | 13         | "main"          || 88194.57  || 0.00       || 9            || LocalDate.of(2042, 9, 11)
        LocalDate.of(2011, 6, 18)  | LocalDate.of(2011, 10, 22) | null                       | true     | 3959.37     | 7133.52         | 1123.53               | 0          | "main"          || 5201.47   || 0.00       || 0            || LocalDate.of(2011, 10, 29)
    }

    def "Tier 4 Non Doctorate - Check 'continuations'"() {

        expect:
        def response = maintenanceThresholdCalculator.calculateNonDoctorate(inLondon, buildScalaBigDecimal(tuitionFees), buildScalaBigDecimal(tuitionFeesPaid),
            buildScalaBigDecimal(accommodationFeesPaid), dependants, courseStartDate, courseEndDate, buildScalaOption(originalCourseStartDate),
            originalCourseStartDate != null, courseType == "pre-sessional")

        def thresholdValue = response._1
        def cappedValues = DataUtils.getCappedValues(response._2)
        def cappedAccommodation = cappedValues.accommodationFeesPaid()
        def cappedCourseLength = cappedValues.courseLength()

        assert thresholdValue == buildScalaBigDecimal(threshold)
        assert DataUtils.compareAccommodationFees(buildScalaBigDecimal(feesCapped), cappedAccommodation) == true
        assert DataUtils.compareCourseLength(courseCapped, cappedCourseLength) == true

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate    | inLondon | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants | courseType || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(2012, 9, 1)   | LocalDate.of(2012, 10, 5)  | LocalDate.of(2011, 8, 16)  | true     | 9518.13     | 0.00            | 0.00                  | 13         | "main"     || 77958.13  || 0.00       || 0            || LocalDate.of(2013, 2, 5)
        LocalDate.of(2048, 4, 29)  | LocalDate.of(2049, 2, 5)   | LocalDate.of(2047, 5, 30)  | false    | 8497.38     | 4389.09         | 552.40                | 12         | "main"     || 86130.89  || 0.00       || 9            || LocalDate.of(2049, 6, 5)
        LocalDate.of(1983, 1, 1)   | LocalDate.of(1983, 5, 13)  | LocalDate.of(1982, 7, 28)  | true     | 8855.36     | 440.57          | 494.41                | 4          | "main"     || 37905.38  || 0.00       || 0            || LocalDate.of(1983, 7, 13)
        LocalDate.of(2012, 10, 4)  | LocalDate.of(2013, 7, 23)  | LocalDate.of(2012, 6, 8)   | true     | 975.47      | 0.00            | 0.00                  | 10         | "main"     || 88410.47  || 0.00       || 9            || LocalDate.of(2013, 11, 23)
        LocalDate.of(1993, 10, 24) | LocalDate.of(1994, 2, 19)  | LocalDate.of(1993, 9, 11)  | true     | 2328.11     | 0.00            | 1143.60               | 11         | "main"     || 52719.51  || 0.00       || 0            || LocalDate.of(1994, 2, 26)
        LocalDate.of(1976, 10, 2)  | LocalDate.of(1976, 11, 10) | LocalDate.of(1976, 8, 18)  | false    | 7190.51     | 6739.80         | 0.00                  | 13         | "main"     || 20160.71  || 0.00       || 0            || LocalDate.of(1976, 11, 17)
        LocalDate.of(1997, 7, 3)   | LocalDate.of(1998, 5, 6)   | LocalDate.of(1997, 4, 25)  | true     | 1013.36     | 6786.90         | 1192.96               | 7          | "main"     || 63427.04  || 0.00       || 9            || LocalDate.of(1998, 9, 6)
        LocalDate.of(1987, 6, 19)  | LocalDate.of(1988, 4, 19)  | LocalDate.of(1986, 7, 3)   | false    | 4871.82     | 4210.72         | 1997.63               | 13         | "main"     || 88091.10  || 1265.00    || 9            || LocalDate.of(1988, 8, 19)
        LocalDate.of(1979, 11, 11) | LocalDate.of(1980, 5, 4)   | LocalDate.of(1978, 12, 2)  | false    | 7387.46     | 6001.97         | 0.00                  | 10         | "main"     || 68675.49  || 0.00       || 0            || LocalDate.of(1980, 9, 4)
        LocalDate.of(2011, 11, 13) | LocalDate.of(2011, 12, 21) | LocalDate.of(2010, 11, 12) | false    | 8314.11     | 3279.49         | 0.00                  | 6          | "main"     || 31544.62  || 0.00       || 0            || LocalDate.of(2012, 4, 21)
        LocalDate.of(2001, 8, 4)   | LocalDate.of(2002, 3, 1)   | LocalDate.of(2000, 8, 3)   | false    | 2889.23     | 7313.87         | 1118.60               | 4          | "main"     || 30466.40  || 0.00       || 0            || LocalDate.of(2002, 7, 1)
        LocalDate.of(2039, 5, 3)   | LocalDate.of(2039, 9, 8)   | LocalDate.of(2039, 1, 30)  | false    | 9837.77     | 2075.81         | 58.92                 | 7          | "main"     || 46098.04  || 0.00       || 0            || LocalDate.of(2039, 11, 8)
        LocalDate.of(2047, 2, 16)  | LocalDate.of(2047, 5, 19)  | LocalDate.of(2046, 9, 27)  | false    | 3726.58     | 0.00            | 1816.95               | 0          | "main"     || 6521.58   || 1265.00    || 0            || LocalDate.of(2047, 7, 19)
        LocalDate.of(2013, 1, 26)  | LocalDate.of(2014, 2, 9)   | LocalDate.of(2012, 12, 10) | true     | 5100.50     | 8355.59         | 0.00                  | 5          | "main"     || 49410.00  || 0.00       || 9            || LocalDate.of(2014, 6, 9)
        LocalDate.of(2036, 10, 29) | LocalDate.of(2037, 2, 18)  | LocalDate.of(2036, 2, 24)  | true     | 7707.63     | 0.00            | 250.41                | 5          | "main"     || 37867.22  || 0.00       || 0            || LocalDate.of(2037, 4, 18)
        LocalDate.of(2046, 6, 4)   | LocalDate.of(2046, 9, 8)   | LocalDate.of(2045, 6, 30)  | false    | 4046.68     | 1670.00         | 1292.23               | 5          | "main"     || 32371.68  || 1265.00    || 0            || LocalDate.of(2047, 1, 8)
        LocalDate.of(2022, 6, 12)  | LocalDate.of(2022, 9, 13)  | LocalDate.of(2022, 2, 25)  | true     | 223.82      | 0.00            | 0.00                  | 3          | "main"     || 20493.82  || 0.00       || 0            || LocalDate.of(2022, 11, 13)
        LocalDate.of(1994, 11, 11) | LocalDate.of(1995, 6, 24)  | LocalDate.of(1994, 2, 14)  | true     | 4572.25     | 2618.11         | 0.00                  | 2          | "main"     || 27284.14  || 0.00       || 0            || LocalDate.of(1995, 10, 24)
        LocalDate.of(1994, 4, 19)  | LocalDate.of(1995, 2, 19)  | LocalDate.of(1993, 12, 14) | false    | 4187.24     | 4545.76         | 0.00                  | 0          | "main"     || 9135.00   || 0.00       || 9            || LocalDate.of(1995, 6, 19)
        LocalDate.of(1977, 1, 8)   | LocalDate.of(1977, 10, 13) | LocalDate.of(1976, 12, 5)  | false    | 9305.97     | 4931.97         | 0.00                  | 10         | "main"     || 74709.00  || 0.00       || 9            || LocalDate.of(1977, 12, 13)
        LocalDate.of(2039, 10, 28) | LocalDate.of(2040, 9, 28)  | LocalDate.of(2039, 2, 17)  | false    | 4119.66     | 6089.15         | 271.61                | 6          | "main"     || 45583.39  || 0.00       || 9            || LocalDate.of(2041, 1, 28)
        LocalDate.of(2053, 1, 5)   | LocalDate.of(2053, 3, 31)  | LocalDate.of(2051, 12, 23) | true     | 8527.11     | 0.00            | 1702.38               | 6          | "main"     || 46547.11  || 1265.00    || 0            || LocalDate.of(2053, 7, 31)
        LocalDate.of(1988, 7, 21)  | LocalDate.of(1989, 1, 9)   | LocalDate.of(1987, 12, 21) | false    | 6600.37     | 0.00            | 1614.86               | 9          | "main"     || 66505.37  || 1265.00    || 0            || LocalDate.of(1989, 5, 9)
        LocalDate.of(1990, 8, 10)  | LocalDate.of(1991, 2, 13)  | LocalDate.of(1990, 3, 6)   | true     | 6175.57     | 3326.77         | 1928.42               | 6          | "main"     || 56068.80  || 1265.00    || 0            || LocalDate.of(1991, 4, 13)
        LocalDate.of(2043, 11, 9)  | LocalDate.of(2044, 10, 18) | LocalDate.of(2043, 10, 30) | false    | 9133.65     | 8621.09         | 190.67                | 6          | "main"     || 46176.89  || 0.00       || 9            || LocalDate.of(2044, 12, 18)
    }


    def "Tier 4 Non Doctorate - Check 'All variants'"() {

        def response = maintenanceThresholdCalculator.calculateNonDoctorate(inLondon, buildScalaBigDecimal(tuitionFees), buildScalaBigDecimal(tuitionFeesPaid),
            buildScalaBigDecimal(accommodationFeesPaid), dependants, courseStartDate, courseEndDate, buildScalaOption(originalCourseStartDate),
            originalCourseStartDate != null, courseType == "pre-sessional")
        def thresholdValue = response._1
        def cappedValues = DataUtils.getCappedValues(response._2)
        def cappedAccommodation = cappedValues.accommodationFeesPaid()
        def cappedCourseLength = cappedValues.courseLength()

        assert thresholdValue == buildScalaBigDecimal(threshold)
        assert DataUtils.compareAccommodationFees(buildScalaBigDecimal(feesCapped), cappedAccommodation) == true
        assert DataUtils.compareCourseLength(courseCapped, cappedCourseLength) == true

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate    | inLondon | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants | courseType      || threshold || feesCapped || courseCapped
        LocalDate.of(1998, 11, 20) | LocalDate.of(1999, 3, 30)  | LocalDate.of(1998, 4, 8)   | false    | 571.76      | 8871.94         | 446.12                | 3          | "main"          || 18908.88  || 0.00       || 0
        LocalDate.of(2004, 4, 3)   | LocalDate.of(2004, 4, 20)  | LocalDate.of(2003, 6, 13)  | true     | 871.97      | 1004.07         | 0.00                  | 6          | "main"          || 16475.00  || 0.00       || 0
        LocalDate.of(2051, 3, 13)  | LocalDate.of(2051, 4, 7)   | null                       | false    | 459.46      | 6269.22         | 1077.57               | 0          | "pre-sessional" || 0.00      || 0.00       || 0
        LocalDate.of(1990, 6, 11)  | LocalDate.of(1990, 7, 21)  | null                       | false    | 7226.30     | 476.88          | 0.00                  | 0          | "pre-sessional" || 8779.42   || 0.00       || 0
        LocalDate.of(2008, 4, 10)  | LocalDate.of(2008, 10, 23) | LocalDate.of(2008, 1, 19)  | false    | 4847.45     | 0.00            | 0.00                  | 9          | "main"          || 67032.45  || 0.00       || 0
        LocalDate.of(2035, 6, 25)  | LocalDate.of(2035, 9, 24)  | LocalDate.of(2035, 3, 10)  | true     | 4806.40     | 8125.81         | 1254.12               | 11         | "main"          || 49015.88  || 0.00       || 0
        LocalDate.of(2015, 7, 20)  | LocalDate.of(2016, 1, 21)  | null                       | true     | 6742.95     | 8838.15         | 0.00                  | 14         | "pre-sessional" || 115325.00 || 0.00       || 0
        LocalDate.of(2010, 3, 1)   | LocalDate.of(2011, 2, 10)  | LocalDate.of(2009, 3, 8)   | true     | 7445.39     | 1286.26         | 0.00                  | 9          | "main"          || 85989.13  || 0.00       || 9
        LocalDate.of(1974, 3, 18)  | LocalDate.of(1974, 7, 18)  | null                       | false    | 4868.60     | 0.00            | 0.00                  | 0          | "main"          || 9943.60   || 0.00       || 0
        LocalDate.of(2003, 7, 29)  | LocalDate.of(2004, 4, 28)  | null                       | true     | 5374.46     | 0.00            | 732.18                | 4          | "main"          || 46447.28  || 0.00       || 0
        LocalDate.of(1989, 12, 6)  | LocalDate.of(1990, 10, 17) | LocalDate.of(1989, 2, 14)  | true     | 9131.62     | 0.00            | 0.00                  | 14         | "main"          || 126986.62 || 0.00       || 9
        LocalDate.of(2000, 6, 22)  | LocalDate.of(2001, 3, 29)  | null                       | true     | 7171.75     | 0.00            | 820.33                | 1          | "pre-sessional" || 25341.42  || 0.00       || 9
        LocalDate.of(2001, 6, 18)  | LocalDate.of(2002, 5, 24)  | LocalDate.of(2000, 11, 14) | false    | 7241.60     | 772.62          | 0.00                  | 13         | "main"          || 95163.98  || 0.00       || 9
        LocalDate.of(2033, 2, 16)  | LocalDate.of(2033, 8, 4)   | LocalDate.of(2032, 12, 24) | true     | 8171.87     | 6593.64         | 1765.07               | 14         | "main"          || 102543.23 || 1265.00    || 0
        LocalDate.of(1986, 3, 14)  | LocalDate.of(1986, 5, 24)  | null                       | true     | 4032.92     | 0.00            | 0.00                  | 0          | "main"          || 7827.92   || 0.00       || 0
        LocalDate.of(2015, 4, 25)  | LocalDate.of(2015, 12, 31) | null                       | false    | 4032.93     | 0.00            | 0.00                  | 7          | "pre-sessional" || 56007.93  || 0.00       || 0
        LocalDate.of(2010, 8, 14)  | LocalDate.of(2011, 4, 17)  | LocalDate.of(2009, 9, 16)  | false    | 6562.58     | 0.00            | 0.00                  | 13         | "main"          || 95257.58  || 0.00       || 0
        LocalDate.of(2027, 10, 23) | LocalDate.of(2028, 2, 28)  | null                       | true     | 3532.82     | 2707.18         | 1597.47               | 0          | "pre-sessional" || 5885.64   || 1265.00    || 0
        LocalDate.of(2001, 5, 12)  | LocalDate.of(2001, 12, 6)  | null                       | false    | 9617.60     | 0.00            | 662.45                | 10         | "pre-sessional" || 77260.15  || 0.00       || 0
        LocalDate.of(2031, 1, 29)  | LocalDate.of(2031, 7, 26)  | null                       | true     | 2179.58     | 0.00            | 1141.27               | 0          | "main"          || 8628.31   || 0.00       || 0
        LocalDate.of(2019, 4, 3)   | LocalDate.of(2020, 4, 26)  | LocalDate.of(2019, 1, 16)  | false    | 2370.07     | 6230.74         | 883.69                | 9          | "main"          || 63331.31  || 0.00       || 9
        LocalDate.of(2027, 2, 16)  | LocalDate.of(2027, 2, 21)  | LocalDate.of(2027, 1, 27)  | true     | 7326.36     | 0.00            | 88.11                 | 11         | "main"          || 17798.25  || 0.00       || 0
        LocalDate.of(2041, 12, 9)  | LocalDate.of(2042, 7, 24)  | null                       | false    | 7388.53     | 4927.61         | 0.00                  | 3          | "pre-sessional" || 28940.92  || 0.00       || 0
        LocalDate.of(1994, 3, 29)  | LocalDate.of(1994, 7, 29)  | null                       | true     | 9366.92     | 8864.58         | 1836.16               | 0          | "pre-sessional" || 5562.34   || 1265.00    || 0
        LocalDate.of(1981, 6, 5)   | LocalDate.of(1982, 7, 5)   | LocalDate.of(1980, 8, 6)   | true     | 9247.00     | 1957.02         | 1066.01               | 5          | "main"          || 55633.97  || 0.00       || 9
    }


}
