package uk.gov.digital.ho.proving.financialstatus.api.test

import spock.lang.Specification
import uk.gov.digital.ho.proving.financialstatus.domain.MaintenanceThresholdCalculator
import static uk.gov.digital.ho.proving.financialstatus.api.test.TestUtils.*

class NonDoctorateMaintenanceThresholdCalculatorTest extends Specification {

    MaintenanceThresholdCalculator maintenanceThresholdCalculator =
        new MaintenanceThresholdCalculator(inLondonMaintenance, notInLondonMaintenance,
            maxMaintenanceAllowance, inLondonDependant, notInLondonDependant,
            nonDoctorateMinCourseLength, nonDoctorateMaxCourseLength, nonDoctorateMinCourseLengthWithDependants,
            pgddSsoMinCourseLength, pgddSsoMaxCourseLength, doctorateFixedCourseLength
        )

    def bd(a) { new scala.math.BigDecimal(a) }

    def "Tier 4 Non Doctorate - Check 'Non Inner London Borough'"() {

        expect:
        maintenanceThresholdCalculator.calculateNonDoctorate(inLondon, courseLengthInMonths, bd(tuitionFees), bd(tuitionFeesPaid), bd(accommodationFeesPaid), dependants, 2)._1 == bd(threshold)

        where:
        inLondon | courseLengthInMonths | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants || threshold
        false    | 7                    | 10000       | 0               | 0                     | 1          || 23225.00
        false    | 8                    | 7000        | 300             | 500                   | 2          || 26560.00
        false    | 9                    | 2000        | 0               | 300.5                 | 3          || 29194.50

    }

    def "Tier 4 Non Doctorate - Check 'Inner London Borough'"() {

        expect:
        maintenanceThresholdCalculator.calculateNonDoctorate(inLondon, courseLengthInMonths, bd(tuitionFees), bd(tuitionFeesPaid), bd(accommodationFeesPaid), dependants, 2)._1 == bd(threshold)

        where:
        inLondon | courseLengthInMonths | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants || threshold
        true     | 7                    | 10000       | 0               | 0                     | 1           | 26460.00
        true     | 8                    | 7000        | 300             | 500                   | 2           | 31530.00
        true     | 9                    | 2000        | 0               | 300.5                 | 3           | 35899.50

    }

    def "Tier 4 Non Doctorate - Check 'Tuition Fees paid'"() {

        expect:
        maintenanceThresholdCalculator.calculateNonDoctorate(inLondon, courseLengthInMonths, bd(tuitionFees), bd(tuitionFeesPaid), bd(accommodationFeesPaid), dependants, courseLengthInMonths >=12 ? 4 : 2)._1 == bd(threshold)

        where:
        inLondon | courseLengthInMonths | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants || threshold
        true     | 9                    | 13470       | 8734            | 0                     | 15         || 130196.00
        false    | 14                   | 11795       | 7663            | 0                     | 12         || 86707.00
        true     | 9                    | 892         | 104             | 0                     | 13         || 111038.00
        true     | 4                    | 14939       | 529             | 0                     | 0          || 19470.00
        true     | 14                   | 3914        | 4069            | 0                     | 9          || 79830.00
        true     | 1                    | 13060       | 6303            | 0                     | 0          || 8022.00
        false    | 1                    | 9689        | 2641            | 0                     | 0          || 8063.00
        false    | 7                    | 9514        | 9626            | 0                     | 10         || 68305.00
        false    | 12                   | 7551        | 4975            | 0                     | 14         || 97391.00
        false    | 0                    | 4821        | 516             | 0                     | 0          || 4305.00
        true     | 13                   | 6214        | 5191            | 0                     | 8          || 73248.00
        false    | 12                   | 9985        | 7062            | 0                     | 14         || 97738.00
        true     | 14                   | 7134        | 939             | 0                     | 10         || 93630.00
        true     | 0                    | 13602       | 13474           | 0                     | 0          || 128.00
        false    | 1                    | 14155       | 2719            | 0                     | 0          || 12451.00
        false    | 7                    | 3131        | 3801            | 0                     | 14         || 92785.00

    }

    def "Tier 4 Non Doctorate - Check 'Accommodation Fees paid'"() {

        expect:
        maintenanceThresholdCalculator.calculateNonDoctorate(inLondon, courseLengthInMonths, bd(tuitionFees), bd(tuitionFeesPaid), bd(accommodationFeesPaid), dependants, courseLengthInMonths >=12 ? 4 : 2)._1 == bd(threshold)

        where:
        inLondon | courseLengthInMonths | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants || threshold
        false    | 5                    | 13929       | 0               | 767                   | 0          || 18237.00
        true     | 4                    | 8719        | 0               | 892                   | 0          || 12887.00
        true     | 4                    | 5985        | 0               | 1888                  | 0          || 9780.00
        false    | 2                    | 10482       | 0               | 1072                  | 0          || 11440.00
        true     | 2                    | 8132        | 0               | 527                   | 0          || 10135.00
        false    | 9                    | 510         | 0               | 1175                  | 6          || 45190.00
        false    | 2                    | 1887        | 0               | 917                   | 0          || 3000.00
        false    | 1                    | 9294        | 0               | 1113                  | 0          || 9196.00
        true     | 15                   | 10782       | 0               | 503                   | 11         || 105319.00
        false    | 5                    | 5036        | 0               | 1794                  | 0          || 8846.00
        true     | 9                    | 2904        | 0               | 1072                  | 8          || 74057.00
        true     | 4                    | 14456       | 0               | 1800                  | 0          || 18251.00
        false    | 14                   | 811         | 0               | 987                   | 1          || 15079.00
        true     | 3                    | 12041       | 0               | 202                   | 0          || 15634.00
        false    | 4                    | 7100        | 0               | 1740                  | 0          || 9895.00
        false    | 0                    | 3935        | 0               | 1455                  | 0          || 2670.00

    }

    def "Tier 4 Non Doctorate - Check 'All variants'"() {

        def response = maintenanceThresholdCalculator.calculateNonDoctorate(inLondon, courseLengthInMonths, bd(tuitionFees), bd(tuitionFeesPaid), bd(accommodationFeesPaid), dependants, courseLengthInMonths >=12 ? 4 : 2)
        def thresholdValue = response._1
        def cappedValues = DataUtils.getCappedValues(response._2)
        def cappedAccommodation = cappedValues.accommodationFeesPaid()
        def cappedCourseLength = cappedValues.courseLength()

        assert thresholdValue == bd(threshold)
        assert DataUtils.compareAccommodationFees(bd(feesCapped), cappedAccommodation) == true
        assert DataUtils.compareCourseLength(courseLengthCapped, cappedCourseLength) == true

        where:
        // Due to groovy not liking Scala's 'None' object we represent this as the value zero
        inLondon | courseLengthInMonths | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants || threshold || feesCapped || courseLengthCapped
        false    | 14                   | 447         | 1348            | 1396                  | 12         || 81310.00  || 1265       || 9
        true     | 7                    | 305         | 112             | 1894                  | 1          || 15388.00  || 1265       || 0
        true     | 9                    | 236         | 71              | 987                   | 13         || 109428.00 || 0          || 0
        true     | 5                    | 283         | 340             | 1524                  | 0          || 5060.00   || 1265       || 0
        false    | 15                   | 842         | 709             | 302                   | 5          || 39566.00  || 0          || 9
        false    | 3                    | 467         | 613             | 1727                  | 0          || 1780.00   || 1265       || 0
        true     | 0                    | 65          | 238             | 1463                  | 0          || 0.00      || 1265       || 0
        false    | 3                    | 71          | 213             | 229                   | 0          || 2816.00   || 0          || 0
        true     | 15                   | 1446        | 1832            | 99                    | 5          || 49311.00  || 0          || 9
        false    | 15                   | 1441        | 2285            | 686                   | 5          || 39049.00  || 0          || 9
        true     | 11                   | 1491        | 301             | 1359                  | 4          || 41730.00  || 1265       || 9
        true     | 2                    | 781         | 418             | 70                    | 0          || 2823.00   || 0          || 0
        true     | 10                   | 437         | 605             | 204                   | 11         || 94836.00  || 0          || 9
        false    | 11                   | 932         | 734             | 1604                  | 7          || 50908.00  || 1265       || 9
        true     | 7                    | 1348        | 916             | 1062                  | 7          || 61460.00  || 0          || 0
        true     | 2                    | 1207        | 1404            | 610                   | 0          || 1920.00   || 0          || 0

    }

}
