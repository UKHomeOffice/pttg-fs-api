package uk.gov.digital.ho.proving.financialstatus.api.test

import spock.lang.Specification
import uk.gov.digital.ho.proving.financialstatus.domain.MaintenanceThresholdCalculator

class NonDoctorateMaintenanceThresholdCalculatorTest extends Specification {

    MaintenanceThresholdCalculator maintenanceThresholdCalculator =
        new MaintenanceThresholdCalculator(TestUtils.innerLondonMaintenance, TestUtils.nonInnerLondonMaintenance,
            TestUtils.maxMaintenanceAllowance,TestUtils.innerLondonDependant, TestUtils.nonInnerLondonDependant,
            TestUtils.nonDoctorateMinCourseLength, TestUtils.nonDoctorateMaxCourseLength,
            TestUtils.doctorateMinCourseLength, TestUtils.doctorateMaxCourseLength
        )

    def bd(a) { new scala.math.BigDecimal(a) }

    def "Tier 4 Non Doctorate - Check 'Non Inner London Borough'"() {

        expect:
        maintenanceThresholdCalculator.calculateNonDoctorate(innerLondon, courseLengthInMonths, bd(tuitionFees), bd(tuitionFeesPaid), bd(accommodationFeesPaid), dependants) == bd(threshold)

        where:
        innerLondon | courseLengthInMonths | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants || threshold
        false       | 1                    | 10047.00    | 0.00            | 0.00                  | 1          || 11742.00
        false       | 2                    | 11682.00    | 0.00            | 0.00                  | 14         || 32752.00
        false       | 3                    | 6160.00     | 0.00            | 0.00                  | 2          || 13285.00
        false       | 4                    | 7560.00     | 0.00            | 0.00                  | 6          || 27940.00
        false       | 5                    | 7482.00     | 0.00            | 0.00                  | 10         || 46557.00
        false       | 6                    | 8713.00     | 0.00            | 0.00                  | 9          || 51523.00
        false       | 7                    | 7307.00     | 0.00            | 0.00                  | 2          || 23932.00
        false       | 8                    | 5878.00     | 0.00            | 0.00                  | 4          || 35758.00
        false       | 9                    | 9180.00     | 0.00            | 0.00                  | 10         || 79515.00

    }

    def "Tier 4 Non Doctorate - Check 'Inner London Borough'"() {

        expect:
        maintenanceThresholdCalculator.calculateNonDoctorate(innerLondon, courseLengthInMonths, bd(tuitionFees), bd(tuitionFeesPaid), bd(accommodationFeesPaid), dependants) == bd(threshold)

        where:
        innerLondon | courseLengthInMonths | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants || threshold
        true        | 1                    | 6305.00     | 0.00            | 0.00                  | 14         || 19400.00
        true        | 2                    | 13788.00    | 0.00            | 0.00                  | 7          || 28148.00
        true        | 3                    | 13890.00    | 0.00            | 0.00                  | 2          || 22755.00
        true        | 4                    | 9781.00     | 0.00            | 0.00                  | 9          || 45261.00
        true        | 5                    | 9601.00     | 0.00            | 0.00                  | 12         || 66626.00
        true        | 6                    | 14502.00    | 0.00            | 0.00                  | 0          || 22092.00
        true        | 7                    | 12672.00    | 0.00            | 0.00                  | 13         || 98422.00
        true        | 8                    | 14618.00    | 0.00            | 0.00                  | 10         || 92338.00
        true        | 9                    | 11896.00    | 0.00            | 0.00                  | 3          || 46096.00

    }

    def "Tier 4 Non Doctorate - Check 'Tuition Fees paid'"() {

        expect:
        maintenanceThresholdCalculator.calculateNonDoctorate(innerLondon, courseLengthInMonths, bd(tuitionFees), bd(tuitionFeesPaid), bd(accommodationFeesPaid), dependants) == bd(threshold)

        where:
        innerLondon | courseLengthInMonths | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants || threshold
        true        | 1                    | 13693.00    | 2214.00         | 0.00                  | 13         || 23729.00
        true        | 2                    | 12710.00    | 2065.00         | 0.00                  | 4          || 19935.00
        true        | 3                    | 10591.00    | 2435.00         | 0.00                  | 4          || 22091.00
        true        | 4                    | 14897.00    | 1287.00         | 0.00                  | 3          || 28810.00
        true        | 5                    | 13362.00    | 626.00          | 0.00                  | 12         || 69761.00
        false       | 6                    | 9905.00     | 4601.00         | 0.00                  | 0          || 11394.00
        false       | 7                    | 9870.00     | 4713.00         | 0.00                  | 12         || 69382.00
        false       | 8                    | 11031.00    | 395.00          | 0.00                  | 12         || 84036.00
        false       | 9                    | 12972.00    | 4774.00         | 0.00                  | 5          || 47933.00

    }

    def "Tier 4 Non Doctorate - Check 'Accommodation Fees paid'"() {

        expect:
        maintenanceThresholdCalculator.calculateNonDoctorate(innerLondon, courseLengthInMonths, bd(tuitionFees), bd(tuitionFeesPaid), bd(accommodationFeesPaid), dependants) == bd(threshold)

        where:
        innerLondon | courseLengthInMonths | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants || threshold
        true        | 1                    | 8072.00     | 0.00            | 876.00                | 11         || 17756.00
        true        | 2                    | 9714.00     | 0.00            | 201.00                | 0          || 12043.00
        true        | 3                    | 10426.00    | 0.00            | 903.00                | 11         || 41203.00
        true        | 4                    | 5266.00     | 0.00            | 566.00                | 15         || 60460.00
        true        | 5                    | 13156.00    | 0.00            | 560.00                | 8          || 52721.00
        false       | 6                    | 5693.00     | 0.00            | 1136.00               | 7          || 39207.00
        false       | 7                    | 10337.00    | 0.00            | 620.00                | 7          || 50142.00
        false       | 8                    | 6749.00     | 0.00            | 485.00                | 6          || 47024.00
        false       | 9                    | 6242.00     | 0.00            | 917.00                | 2          || 26700.00

    }

    def "Tier 4 Non Doctorate - Check 'All variants'"() {

        expect:
        maintenanceThresholdCalculator.calculateNonDoctorate(innerLondon, courseLengthInMonths, bd(tuitionFees), bd(tuitionFeesPaid), bd(accommodationFeesPaid), dependants) == bd(threshold)

        where:
        innerLondon | courseLengthInMonths | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants || threshold
        true        | 3                    | 6751.00     | 1508.00         | 325.00                | 13         || 41668.00
        true        | 4                    | 8087.00     | 506.00          | 598.00                | 2          || 18803.00
        false       | 4                    | 6546.00     | 3997.00         | 948.00                | 0          || 5661.00
        false       | 8                    | 10265.00    | 4912.00         | 95.00                 | 14         || 89538.00
        true        | 2                    | 6624.00     | 3054.00         | 3.00                  | 8          || 19617.00
        false       | 4                    | 8476.00     | 1758.00         | 652.00                | 11         || 40046.00
        false       | 4                    | 11555.00    | 4773.00         | 602.00                | 1          || 12960.00
        false       | 9                    | 14248.00    | 2354.00         | 1260.00               | 1          || 25889.00
        true        | 6                    | 12883.00    | 1081.00         | 547.00                | 6          || 49265.00
        false       | 7                    | 9428.00     | 1688.00         | 126.00                | 10         || 62319.00
        false       | 6                    | 12320.00    | 4379.00         | 1011.00               | 12         || 61980.00
        true        | 19                   | 9202.00     | 487.00          | 618.00                | 13         || 118347.00
        false       | 7                    | 13171.00    | 403.00          | 77.00                 | 13         || 81676.00
        false       | 5                    | 5669.00     | 3209.00         | 999.00                | 3          || 16736.00
        false       | 7                    | 10095.00    | 2564.00         | 236.00                | 4          || 33440.00
        false       | 6                    | 13104.00    | 2056.00         | 977.00                | 5          || 36561.00
        true        | 7                    | 8187.00     | 3318.00         | 805.00                | 14         || 95729.00
        true        | 8                    | 10169.00    | 2731.00         | 1204.00               | 8          || 70434.00

    }

}
