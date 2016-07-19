package uk.gov.digital.ho.proving.financialstatus.api.test

import spock.lang.Specification
import uk.gov.digital.ho.proving.financialstatus.domain.MaintenanceThresholdCalculator

class DoctorateMaintenanceThresholdCalculatorTest extends Specification {

    MaintenanceThresholdCalculator maintenanceThresholdCalculator =
        new MaintenanceThresholdCalculator(TestUtils.innerLondonMaintenance, TestUtils.nonInnerLondonMaintenance,
            TestUtils.maxMaintenanceAllowance, TestUtils.innerLondonDependant, TestUtils.nonInnerLondonDependant,
            TestUtils.nonDoctorateMinCourseLength, TestUtils.nonDoctorateMaxCourseLength,
            TestUtils.doctorateMinCourseLength, TestUtils.doctorateMaxCourseLength
        )

    def bd(a) { new scala.math.BigDecimal(a) }

    def "Tier 4 Doctorate - Check 'Non Inner London Borough'"() {

        expect:
        maintenanceThresholdCalculator.calculateDoctorate(innerLondon, courseLengthInMonths, bd(accommodationFeesPaid), dependants) == bd(threshold)

        where:
        innerLondon | courseLengthInMonths | accommodationFeesPaid | dependants || threshold
        false       | 1                    | 0.00                  | 5          || 4415.00
        false       | 2                    | 0.00                  | 7          || 11550.00
        false       | 3                    | 0.00                  | 0          || 2030.00
        false       | 4                    | 0.00                  | 6          || 18350.00
        false       | 5                    | 0.00                  | 5          || 19030.00
        false       | 6                    | 0.00                  | 1          || 6110.00
        false       | 7                    | 0.00                  | 14         || 68670.00
        false       | 8                    | 0.00                  | 0          || 2030.00
        false       | 9                    | 0.00                  | 11         || 69350.00

    }

    def "Tier 4 Doctorate - Check 'Inner London Borough'"() {

        expect:
        maintenanceThresholdCalculator.calculateDoctorate(innerLondon, courseLengthInMonths, bd(accommodationFeesPaid), dependants) == bd(threshold)

        where:
        innerLondon | courseLengthInMonths | accommodationFeesPaid | dependants || threshold
        true        | 1                    | 0.00                  | 4          || 4645.00
        true        | 2                    | 0.00                  | 15         || 27880.00
        true        | 3                    | 0.00                  | 4          || 12670.00
        true        | 4                    | 0.00                  | 7          || 26190.00
        true        | 5                    | 0.00                  | 0          || 2530.00
        true        | 6                    | 0.00                  | 11         || 58300.00
        true        | 7                    | 0.00                  | 9          || 55765.00
        true        | 8                    | 0.00                  | 15         || 103930.00
        true        | 9                    | 0.00                  | 15         || 116605.00

    }

    def "Tier 4 Doctorate - Check 'Accommodation Fees paid'"() {

        expect:
        maintenanceThresholdCalculator.calculateDoctorate(innerLondon, courseLengthInMonths, bd(accommodationFeesPaid), dependants) == bd(threshold)

        where:
        innerLondon | courseLengthInMonths | accommodationFeesPaid | dependants || threshold
        true        | 1                    | 1039.00               | 14         || 12056.00
        true        | 2                    | 692.00                | 11         || 20428.00
        true        | 1                    | 622.00                | 3          || 3178.00
        true        | 2                    | 154.00                | 9          || 17586.00
        true        | 1                    | 869.00                | 10         || 8846.00
        false       | 2                    | 860.00                | 12         || 17490.00
        false       | 1                    | 206.00                | 9          || 6929.00
        false       | 2                    | 106.00                | 11         || 16884.00
        false       | 1                    | 1245.00               | 0          || 0.00

    }

    def "Tier 4 Doctorate - Check 'All variants'"() {

        expect:
        maintenanceThresholdCalculator.calculateDoctorate(innerLondon, courseLengthInMonths, bd(accommodationFeesPaid), dependants) == bd(threshold)

        where:
        innerLondon | courseLengthInMonths | accommodationFeesPaid | dependants || threshold
        false       | 2                    | 627.00                | 15         || 21803.00
        false       | 1                    | 270.00                | 10         || 7545.00
        true        | 2                    | 22.00                 | 1          || 4198.00
        true        | 2                    | 636.00                | 9          || 17104.00
        false       | 1                    | 1018.00               | 3          || 2037.00
        true        | 2                    | 446.00                | 6          || 12224.00
        false       | 1                    | 372.00                | 6          || 4723.00
        true        | 2                    | 657.00                | 13         || 23843.00
        true        | 2                    | 953.00                | 6          || 11717.00
        true        | 2                    | 229.00                | 12         || 22581.00
        true        | 2                    | 23.00                 | 12         || 22787.00
        false       | 2                    | 182.00                | 14         || 20888.00
        false       | 1                    | 738.00                | 12         || 8437.00
        true        | 2                    | 73.00                 | 9          || 17667.00
        false       | 1                    | 970.00                | 6          || 4125.00
        true        | 2                    | 934.00                | 5          || 10046.00
        true        | 1                    | 223.00                | 4          || 4422.00
        true        | 2                    | 1078.00               | 14         || 25112.00

    }

}
