package uk.gov.digital.ho.proving.financialstatus.api.test

import spock.lang.Specification
import uk.gov.digital.ho.proving.financialstatus.domain.MaintenanceThresholdCalculator

import java.time.LocalDate

import static uk.gov.digital.ho.proving.financialstatus.api.test.TestUtils.*
import static uk.gov.digital.ho.proving.financialstatus.api.test.DataUtils.buildScalaBigDecimal
import static uk.gov.digital.ho.proving.financialstatus.api.test.DataUtils.buildScalaOption


class NonDoctorateMaintenanceThresholdCalculatorTest extends Specification {

    MaintenanceThresholdCalculator maintenanceThresholdCalculator =
        new MaintenanceThresholdCalculator(inLondonMaintenance, notInLondonMaintenance,
            maxMaintenanceAllowance, inLondonDependant, notInLondonDependant,
            nonDoctorateMinCourseLength, nonDoctorateMaxCourseLength,
            pgddSsoMinCourseLength, pgddSsoMaxCourseLength, doctorateFixedCourseLength,
            susoMinCourseLength, susoMaxCourseLength
        )


    def "Tier 4 Non Doctorate - Check 'Non Inner London Borough'"() {

        expect:
        def response = maintenanceThresholdCalculator.calculateNonDoctorate(inLondon, buildScalaBigDecimal(tuitionFees), buildScalaBigDecimal(tuitionFeesPaid),
            buildScalaBigDecimal(accommodationFeesPaid), dependants, courseStartDate, courseEndDate, buildScalaOption(originalCourseStartDate),
            originalCourseStartDate != null, courseType == "pre-sessional")

        assert (response._1 == buildScalaBigDecimal(threshold))

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate   | inLondon | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants | courseType || threshold || feesCapped || courseCapped
        LocalDate.of(2022, 3, 8)   | LocalDate.of(2022, 11, 19) | LocalDate.of(2021, 10, 9) | true     | 3534.29     | 0.00            | 0.00                  | 6          | "main"     || 60549.29  || 0.00       || 0
        LocalDate.of(1984, 9, 21)  | LocalDate.of(1985, 4, 9)   | null                      | true     | 18.34       | 0.00            | 0.00                  | 14         | "main"     || 115343.34 || 0.00       || 0
        LocalDate.of(2034, 8, 16)  | LocalDate.of(2035, 2, 2)   | null                      | true     | 7075.04     | 0.00            | 0.00                  | 0          | "main"     || 14665.04  || 0.00       || 0
        LocalDate.of(1993, 7, 14)  | LocalDate.of(1994, 8, 8)   | null                      | true     | 5159.51     | 0.00            | 0.00                  | 11         | "main"     || 100199.51 || 0.00       || 9
        LocalDate.of(1982, 8, 14)  | LocalDate.of(1982, 11, 7)  | null                      | true     | 1999.72     | 0.00            | 0.00                  | 0          | "pre-sessional"      || 5794.72   || 0.00       || 0
        LocalDate.of(1988, 5, 31)  | LocalDate.of(1989, 1, 31)  | LocalDate.of(1988, 4, 30) | true     | 48.74       | 0.00            | 0.00                  | 4          | "main"     || 41853.74  || 0.00       || 0
        LocalDate.of(1975, 9, 28)  | LocalDate.of(1976, 8, 1)   | null                      | true     | 7489.93     | 0.00            | 0.00                  | 12         | "main"     || 110134.93 || 0.00       || 9
        LocalDate.of(1985, 2, 16)  | LocalDate.of(1985, 6, 9)   | LocalDate.of(1984, 8, 13) | true     | 9058.83     | 0.00            | 0.00                  | 9          | "main"     || 59748.83  || 0.00       || 0
        LocalDate.of(1977, 1, 24)  | LocalDate.of(1978, 2, 17)  | null                      | true     | 8003.34     | 0.00            | 0.00                  | 3          | "main"     || 42203.34  || 0.00       || 9
        LocalDate.of(1984, 10, 28) | LocalDate.of(1985, 3, 7)   | LocalDate.of(1984, 6, 9)  | true     | 7671.64     | 0.00            | 0.00                  | 1          | "main"     || 19911.64  || 0.00       || 0
    }

    def "Tier 4 Non Doctorate - Check 'Inner London Borough'"() {

        expect:
        def response = maintenanceThresholdCalculator.calculateNonDoctorate(inLondon, buildScalaBigDecimal(tuitionFees), buildScalaBigDecimal(tuitionFeesPaid),
            buildScalaBigDecimal(accommodationFeesPaid), dependants, courseStartDate, courseEndDate, buildScalaOption(originalCourseStartDate),
            originalCourseStartDate != null, courseType == "pre-sessional")

        assert (response._1 == buildScalaBigDecimal(threshold))

        where:
        courseStartDate           | courseEndDate              | originalCourseStartDate    | inLondon | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants | courseType || threshold || feesCapped || courseCapped
        LocalDate.of(2026, 6, 2)  | LocalDate.of(2027, 5, 30)  | LocalDate.of(2025, 7, 9)   | false    | 6850.70     | 0.00            | 0.00                  | 13         | "main"     || 95545.70  || 0.00       || 9
        LocalDate.of(1991, 11, 6) | LocalDate.of(1991, 12, 28) | LocalDate.of(1991, 10, 29) | false    | 909.72      | 0.00            | 0.00                  | 10         | "main"     || 16539.72  || 0.00       || 0
        LocalDate.of(2042, 1, 8)  | LocalDate.of(2042, 7, 8)   | LocalDate.of(2041, 2, 7)   | false    | 284.09      | 0.00            | 0.00                  | 4          | "main"     || 31869.09  || 0.00       || 0
        LocalDate.of(1977, 12, 4) | LocalDate.of(1978, 5, 6)   | null                       | false    | 212.90      | 0.00            | 0.00                  | 0          | "pre-sessional"      || 6302.90   || 0.00       || 0
        LocalDate.of(2015, 1, 21) | LocalDate.of(2015, 7, 25)  | null                       | false    | 3747.33     | 0.00            | 0.00                  | 9          | "main"     || 65932.33  || 0.00       || 0
        LocalDate.of(2003, 9, 21) | LocalDate.of(2003, 11, 25) | LocalDate.of(2003, 4, 12)  | false    | 5884.20     | 0.00            | 0.00                  | 8          | "main"     || 36129.20  || 0.00       || 0
        LocalDate.of(1975, 8, 1)  | LocalDate.of(1975, 10, 10) | LocalDate.of(1974, 11, 18) | false    | 9334.44     | 0.00            | 0.00                  | 10         | "main"     || 46379.44  || 0.00       || 0
        LocalDate.of(2012, 6, 17) | LocalDate.of(2013, 4, 24)  | LocalDate.of(2012, 5, 12)  | false    | 7764.50     | 0.00            | 0.00                  | 12         | "main"     || 90339.50  || 0.00       || 9
        LocalDate.of(1998, 1, 14) | LocalDate.of(1998, 10, 29) | LocalDate.of(1997, 8, 11)  | false    | 4399.63     | 0.00            | 0.00                  | 5          | "main"     || 44134.63  || 0.00       || 9
        LocalDate.of(2034, 6, 10) | LocalDate.of(2034, 12, 20) | null                       | false    | 2472.94     | 0.00            | 0.00                  | 5          | "pre-sessional"      || 40177.94  || 0.00       || 0
    }

    def "Tier 4 Non Doctorate - Check 'Tuition Fees paid'"() {

        expect:
        def response = maintenanceThresholdCalculator.calculateNonDoctorate(inLondon, buildScalaBigDecimal(tuitionFees), buildScalaBigDecimal(tuitionFeesPaid),
            buildScalaBigDecimal(accommodationFeesPaid), dependants, courseStartDate, courseEndDate, buildScalaOption(originalCourseStartDate),
            originalCourseStartDate != null, courseType == "pre-sessional")

        assert (response._1 == buildScalaBigDecimal(threshold))


        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate   | inLondon | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants | courseType || threshold || feesCapped || courseCapped
        LocalDate.of(1995, 5, 3)   | LocalDate.of(1995, 9, 13)  | null                      | true     | 8362.41     | 8091.47         | 0.00                  | 0          | "main"     || 6595.94   || 0.00       || 0
        LocalDate.of(2016, 10, 9)  | LocalDate.of(2017, 6, 29)  | LocalDate.of(2016, 9, 8)  | false    | 8011.48     | 9160.03         | 0.00                  | 12         | "main"     || 82575.00  || 0.00       || 0
        LocalDate.of(2027, 5, 25)  | LocalDate.of(2027, 11, 9)  | null                      | true     | 2200.54     | 3633.58         | 0.00                  | 0          | "main"     || 7590.00   || 0.00       || 0
        LocalDate.of(2032, 4, 3)   | LocalDate.of(2033, 4, 25)  | null                      | false    | 4119.20     | 3416.67         | 0.00                  | 13         | "main"     || 89397.53  || 0.00       || 9
        LocalDate.of(2018, 12, 9)  | LocalDate.of(2020, 1, 6)   | LocalDate.of(2018, 4, 17) | true     | 2149.20     | 7761.61         | 0.00                  | 3          | "main"     || 34200.00  || 0.00       || 9
        LocalDate.of(1997, 8, 17)  | LocalDate.of(1998, 1, 23)  | null                      | false    | 6684.65     | 2546.32         | 0.00                  | 0          | "pre-sessional"      || 10228.33  || 0.00       || 0
        LocalDate.of(2048, 12, 28) | LocalDate.of(2049, 8, 7)   | null                      | true     | 3065.53     | 8480.97         | 0.00                  | 6          | "pre-sessional"      || 55750.00  || 0.00       || 0
        LocalDate.of(2044, 8, 29)  | LocalDate.of(2045, 1, 19)  | LocalDate.of(2044, 3, 22) | true     | 6543.56     | 9868.04         | 0.00                  | 13         | "main"     || 83220.00  || 0.00       || 0
        LocalDate.of(2051, 8, 31)  | LocalDate.of(2051, 10, 10) | null                      | false    | 8874.93     | 4958.95         | 0.00                  | 0          | "main"     || 5945.98   || 0.00       || 0
        LocalDate.of(1974, 9, 6)   | LocalDate.of(1975, 2, 15)  | LocalDate.of(1974, 1, 12) | true     | 1923.70     | 4303.94         | 0.00                  | 6          | "main"     || 53220.00  || 0.00       || 0
    }

    def "Tier 4 Non Doctorate - Check 'Accommodation Fees paid'"() {

        expect:
        def response = maintenanceThresholdCalculator.calculateNonDoctorate(inLondon, buildScalaBigDecimal(tuitionFees), buildScalaBigDecimal(tuitionFeesPaid),
            buildScalaBigDecimal(accommodationFeesPaid), dependants, courseStartDate, courseEndDate, buildScalaOption(originalCourseStartDate),
            originalCourseStartDate != null, courseType == "pre-sessional")

        assert (response._1 == buildScalaBigDecimal(threshold))

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate    | inLondon | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants | courseType || threshold || feesCapped || courseCapped
        LocalDate.of(2004, 2, 12)  | LocalDate.of(2004, 12, 27) | null                       | true     | 6745.80     | 0.00            | 1225.73               | 14         | "main"     || 123375.07 || 0.00       || 9
        LocalDate.of(2032, 12, 5)  | LocalDate.of(2033, 3, 12)  | null                       | true     | 7187.50     | 0.00            | 1649.60               | 0          | "pre-sessional"      || 10982.50  || 1265.00    || 0
        LocalDate.of(2000, 7, 31)  | LocalDate.of(2001, 8, 27)  | null                       | false    | 7560.49     | 0.00            | 472.63                | 4          | "pre-sessional"      || 40702.86  || 0.00       || 9
        LocalDate.of(1997, 12, 16) | LocalDate.of(1998, 11, 9)  | null                       | true     | 7545.89     | 0.00            | 1507.92               | 12         | "main"     || 108925.89 || 1265.00    || 9
        LocalDate.of(2036, 1, 11)  | LocalDate.of(2036, 9, 29)  | null                       | true     | 2700.78     | 0.00            | 807.16                | 1          | "pre-sessional"      || 20883.62  || 0.00       || 0
        LocalDate.of(2041, 10, 24) | LocalDate.of(2042, 5, 8)   | null                       | false    | 5592.18     | 0.00            | 822.36                | 3          | "pre-sessional"      || 30234.82  || 0.00       || 0
        LocalDate.of(2048, 2, 11)  | LocalDate.of(2048, 3, 2)   | null                       | false    | 4947.54     | 0.00            | 1056.07               | 0          | "pre-sessional"      || 4906.47   || 0.00       || 0
        LocalDate.of(1974, 12, 3)  | LocalDate.of(1975, 8, 27)  | LocalDate.of(1973, 12, 31) | true     | 487.75      | 0.00            | 1354.61               | 12         | "main"     || 101867.75 || 1265.00    || 0
        LocalDate.of(1983, 5, 30)  | LocalDate.of(1984, 6, 12)  | null                       | true     | 1979.45     | 0.00            | 217.31                | 5          | "main"     || 51172.14  || 0.00       || 9
        LocalDate.of(2048, 4, 14)  | LocalDate.of(2049, 3, 4)   | LocalDate.of(2048, 3, 30)  | true     | 9821.61     | 0.00            | 293.85                | 6          | "main"     || 66542.76  || 0.00       || 9
    }

    def "Tier 4 Non Doctorate - Check 'extensions'"() {

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
        courseStartDate            | courseEndDate             | originalCourseStartDate    | inLondon | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants | courseType || threshold || feesCapped || courseCapped
        LocalDate.of(2010, 9, 6)   | LocalDate.of(2011, 1, 17) | LocalDate.of(2010, 6, 15)  | true     | 6531.99     | 0.00            | 0.00                  | 10         | "main"     || 72006.99  || 0.00       || 0
        LocalDate.of(2049, 11, 22) | LocalDate.of(2050, 4, 2)  | LocalDate.of(2048, 12, 11) | true     | 6819.83     | 0.00            | 744.37                | 11         | "main"     || 96055.46  || 0.00       || 0
        LocalDate.of(2019, 9, 4)   | LocalDate.of(2020, 4, 4)  | LocalDate.of(2018, 8, 19)  | false    | 9274.10     | 5847.77         | 1769.75               | 12         | "main"     || 83721.33  || 1265.00    || 0
        LocalDate.of(1993, 2, 18)  | LocalDate.of(1993, 7, 22) | LocalDate.of(1992, 2, 7)   | false    | 9263.63     | 0.00            | 0.00                  | 3          | "main"     || 33713.63  || 0.00       || 0
        LocalDate.of(2046, 2, 22)  | LocalDate.of(2046, 9, 21) | LocalDate.of(2045, 2, 8)   | true     | 4243.50     | 0.00            | 0.00                  | 7          | "main"     || 66333.50  || 0.00       || 0
        LocalDate.of(2027, 2, 14)  | LocalDate.of(2028, 2, 6)  | LocalDate.of(2026, 1, 14)  | true     | 1234.69     | 0.00            | 201.27                | 4          | "main"     || 42838.42  || 0.00       || 9
        LocalDate.of(2015, 9, 12)  | LocalDate.of(2015, 10, 6) | LocalDate.of(2014, 8, 30)  | true     | 8990.89     | 0.00            | 148.67                | 2          | "main"     || 18557.22  || 0.00       || 0
        LocalDate.of(1977, 6, 6)   | LocalDate.of(1978, 6, 24) | LocalDate.of(1977, 1, 22)  | false    | 3146.38     | 0.00            | 429.36                | 7          | "main"     || 54692.02  || 0.00       || 9
        LocalDate.of(2054, 4, 2)   | LocalDate.of(2054, 6, 26) | LocalDate.of(2054, 1, 22)  | false    | 9497.10     | 0.00            | 0.00                  | 5          | "main"     || 26142.10  || 0.00       || 0
        LocalDate.of(2051, 7, 24)  | LocalDate.of(2052, 1, 21) | LocalDate.of(2051, 5, 18)  | true     | 3904.87     | 0.00            | 1208.63               | 11         | "main"     || 84646.24  || 0.00       || 0
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
        courseStartDate            | courseEndDate              | originalCourseStartDate    | inLondon | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants | courseType || threshold || feesCapped || courseCapped
        LocalDate.of(2002, 4, 22)  | LocalDate.of(2003, 4, 8)   | LocalDate.of(2001, 11, 15) | false    | 3679.07     | 0.00            | 0.00                  | 6          | "main"     || 49534.07  || 0.00       || 9
        LocalDate.of(1991, 7, 10)  | LocalDate.of(1991, 9, 30)  | null                       | true     | 2045.78     | 0.00            | 0.00                  | 0          | "pre-sessional"      || 5840.78   || 0.00       || 0
        LocalDate.of(2007, 7, 28)  | LocalDate.of(2007, 11, 1)  | LocalDate.of(2006, 6, 28)  | false    | 9050.84     | 8926.53         | 0.00                  | 3          | "main"     || 20504.31  || 0.00       || 0
        LocalDate.of(2044, 6, 9)   | LocalDate.of(2044, 6, 14)  | null                       | true     | 8710.84     | 0.00            | 1466.67               | 0          | "main"     || 8710.84   || 1265.00    || 0
        LocalDate.of(1977, 6, 2)   | LocalDate.of(1977, 11, 29) | LocalDate.of(1977, 3, 20)  | true     | 3023.43     | 0.00            | 73.24                 | 10         | "main"     || 78140.19  || 0.00       || 0
        LocalDate.of(2043, 8, 3)   | LocalDate.of(2044, 4, 28)  | null                       | false    | 2249.44     | 0.00            | 0.00                  | 4          | "pre-sessional"      || 35864.44  || 0.00       || 0
        LocalDate.of(1990, 8, 16)  | LocalDate.of(1991, 2, 4)   | null                       | true     | 6681.48     | 0.00            | 0.00                  | 0          | "main"     || 14271.48  || 0.00       || 0
        LocalDate.of(2008, 4, 22)  | LocalDate.of(2008, 12, 14) | LocalDate.of(2007, 5, 15)  | true     | 8592.24     | 0.00            | 0.00                  | 2          | "main"     || 33922.24  || 0.00       || 0
        LocalDate.of(2036, 9, 24)  | LocalDate.of(2037, 4, 14)  | LocalDate.of(2036, 2, 1)   | false    | 3819.01     | 9262.56         | 0.00                  | 13         | "main"     || 86665.00  || 0.00       || 0
        LocalDate.of(2040, 9, 7)   | LocalDate.of(2040, 10, 21) | LocalDate.of(2040, 6, 6)   | false    | 790.21      | 0.00            | 0.00                  | 8          | "main"     || 13700.21  || 0.00       || 0
        LocalDate.of(2010, 4, 5)   | LocalDate.of(2010, 5, 15)  | null                       | true     | 5574.53     | 9920.11         | 1047.73               | 0          | "main"     || 1482.27   || 0.00       || 0
        LocalDate.of(2038, 10, 25) | LocalDate.of(2039, 5, 18)  | null                       | true     | 1372.86     | 2518.09         | 0.00                  | 0          | "pre-sessional"      || 8855.00   || 0.00       || 0
        LocalDate.of(2043, 1, 14)  | LocalDate.of(2043, 7, 19)  | LocalDate.of(2042, 2, 8)   | false    | 2359.25     | 0.00            | 411.08                | 3          | "main"     || 27413.17  || 0.00       || 0
        LocalDate.of(1976, 5, 4)   | LocalDate.of(1977, 4, 10)  | null                       | false    | 1349.29     | 0.00            | 0.00                  | 10         | "pre-sessional"      || 71684.29  || 0.00       || 9
        LocalDate.of(1985, 9, 24)  | LocalDate.of(1985, 10, 16) | null                       | false    | 8441.77     | 0.00            | 0.00                  | 0          | "main"     || 9456.77   || 0.00       || 0
        LocalDate.of(2046, 6, 5)   | LocalDate.of(2047, 2, 15)  | LocalDate.of(2045, 8, 8)   | true     | 2740.55     | 4907.16         | 0.00                  | 6          | "main"     || 57015.00  || 0.00       || 0
        LocalDate.of(1977, 11, 16) | LocalDate.of(1978, 10, 29) | LocalDate.of(1977, 3, 6)   | false    | 869.33      | 1502.96         | 513.92                | 13         | "main"     || 88181.08  || 0.00       || 9
        LocalDate.of(2002, 3, 13)  | LocalDate.of(2002, 9, 15)  | null                       | true     | 8380.55     | 0.00            | 1935.86               | 13         | "main"     || 114835.55 || 1265.00    || 0
        LocalDate.of(2047, 7, 16)  | LocalDate.of(2047, 10, 15) | null                       | true     | 6342.50     | 4344.58         | 0.00                  | 0          | "main"     || 5792.92   || 0.00       || 0
        LocalDate.of(1989, 6, 2)   | LocalDate.of(1989, 7, 7)   | null                       | false    | 7068.35     | 533.65          | 0.00                  | 0          | "main"     || 8564.70   || 0.00       || 0
    }


}
