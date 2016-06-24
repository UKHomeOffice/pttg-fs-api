package uk.gov.digital.ho.proving.financialstatus.api.test

import spock.lang.Specification
import uk.gov.digital.ho.proving.financialstatus.domain.MaintenanceThresholdCalculator

class MaintenanceThresholdCalculatorTest extends Specification {

    def bd(a) { new scala.math.BigDecimal(a) }

    def "Check 'Non Inner London Borough'"() {

        expect:
        MaintenanceThresholdCalculator.calculate(innerLondon, courseLengthInMonths, bd(tuitionFees), bd(tuitionFeesPaid), bd(accommodationFeesPaid)) == bd(threshold)

        where:
        innerLondon | courseLengthInMonths | tuitionFees | tuitionFeesPaid | accommodationFeesPaid || threshold
        false       | 1                    | 11971.00    | 0.00            | 0.00                  || 12986.00
        false       | 2                    | 13510.00    | 0.00            | 0.00                  || 15540.00
        false       | 3                    | 11419.00    | 0.00            | 0.00                  || 14464.00
        false       | 4                    | 10627.00    | 0.00            | 0.00                  || 14687.00
        false       | 5                    | 8788.00     | 0.00            | 0.00                  || 13863.00
        false       | 6                    | 8106.00     | 0.00            | 0.00                  || 14196.00
        false       | 7                    | 10393.00    | 0.00            | 0.00                  || 17498.00
        false       | 8                    | 11463.00    | 0.00            | 0.00                  || 19583.00
        false       | 9                    | 9882.00     | 0.00            | 0.00                  || 19017.00

    }

    def "Check 'Inner London Borough'"() {

        expect:
        MaintenanceThresholdCalculator.calculate(innerLondon, courseLengthInMonths, bd(tuitionFees), bd(tuitionFeesPaid), bd(accommodationFeesPaid)) == bd(threshold)

        where:
        innerLondon | courseLengthInMonths | tuitionFees | tuitionFeesPaid | accommodationFeesPaid || threshold
        true        | 1                    | 11709.00    | 0.00            | 0.00                  || 12974.00
        true        | 2                    | 6296.00     | 0.00            | 0.00                  || 8826.00
        true        | 3                    | 7468.00     | 0.00            | 0.00                  || 11263.00
        true        | 4                    | 8022.00     | 0.00            | 0.00                  || 13082.00
        true        | 5                    | 10572.00    | 0.00            | 0.00                  || 16897.00
        true        | 6                    | 6511.00     | 0.00            | 0.00                  || 14101.00
        true        | 7                    | 11721.00    | 0.00            | 0.00                  || 20576.00
        true        | 8                    | 5010.00     | 0.00            | 0.00                  || 15130.00
        true        | 9                    | 14835.00    | 0.00            | 0.00                  || 26220.00

    }

    def "Check 'Tuition Fees paid'"() {

        expect:
        MaintenanceThresholdCalculator.calculate(innerLondon, courseLengthInMonths, bd(tuitionFees), bd(tuitionFeesPaid), bd(accommodationFeesPaid)) == bd(threshold)

        where:
        innerLondon | courseLengthInMonths | tuitionFees | tuitionFeesPaid | accommodationFeesPaid || threshold
        true        | 1                    | 12519.00    | 3723            | 0.00                  || 10061.00
        true        | 2                    | 7654.00     | 1628            | 0.00                  || 8556.00
        true        | 3                    | 14593.00    | 2543            | 0.00                  || 15845.00
        true        | 4                    | 10560.00    | 2129            | 0.00                  || 13491.00
        true        | 5                    | 7966.00     | 1732            | 0.00                  || 12559.00
        false       | 6                    | 13679.00    | 4589            | 0.00                  || 15180.00
        false       | 7                    | 5556.00     | 2552            | 0.00                  || 10109.00
        false       | 8                    | 6719.00     | 4566            | 0.00                  || 10273.00
        false       | 9                    | 5204.00     | 3370            | 0.00                  || 10969.00

    }

    def "Check 'Accommodation Fees paid'"() {

        expect:
        MaintenanceThresholdCalculator.calculate(innerLondon, courseLengthInMonths, bd(tuitionFees), bd(tuitionFeesPaid), bd(accommodationFeesPaid)) == bd(threshold)

        where:
        innerLondon | courseLengthInMonths | tuitionFees | tuitionFeesPaid | accommodationFeesPaid || threshold
        true        | 1                    | 14325.00    | 0.00            | 612.00                || 14978.00
        true        | 2                    | 8174.00     | 0.00            | 419.00                || 10285.00
        true        | 3                    | 8816.00     | 0.00            | 1281.00               || 11346.00  // Trigger max accommodation cap
        true        | 4                    | 11674.00    | 0.00            | 252.00                || 16482.00
        true        | 5                    | 13873.00    | 0.00            | 464.00                || 19734.00
        false       | 6                    | 7280.00     | 0.00            | 1485.00               || 12105.00  // Trigger max accommodation cap
        false       | 7                    | 14322.00    | 0.00            | 1460.00               || 20162.00  // Trigger max accommodation cap
        false       | 8                    | 13170.00    | 0.00            | 850.00                || 20440.00
        false       | 9                    | 9301.00     | 0.00            | 1086.00               || 17350.00

    }

    def "Check 'All variants'"() {

        expect:
        MaintenanceThresholdCalculator.calculate(innerLondon, courseLengthInMonths, bd(tuitionFees), bd(tuitionFeesPaid), bd(accommodationFeesPaid)) == bd(threshold)

        where:
        innerLondon | courseLengthInMonths | tuitionFees | tuitionFeesPaid | accommodationFeesPaid || threshold
        false       | 1                    | 5266.00     | 4658.00         | 398.00                || 1225.00
        true        | 8                    | 9729.00     | 2271.00         | 810.00                || 16768.00
        false       | 9                    | 7104.00     | 1073.00         | 1046.00               || 14120.00
        true        | 8                    | 8437.00     | 2399.00         | 977.00                || 15181.00
        false       | 5                    | 10122.00    | 3747.00         | 1367.00               || 10185.00  // Trigger max accommodation cap
        false       | 2                    | 6347.00     | 4108.00         | 561.00                || 3708.00
        false       | 9                    | 7187.00     | 1989.00         | 1497.00               || 13068.00  // Trigger max accommodation cap
        false       | 9                    | 12789.00    | 1506.00         | 1713.00               || 19153.00  // Trigger max accommodation cap
        true        | 7                    | 12636.00    | 1716.00         | 726.00                || 19049.00
        false       | 4                    | 6817.00     | 2646.00         | 1349.00               || 6966.00   // Trigger max accommodation cap
        false       | 3                    | 5673.00     | 2605.00         | 1508.00               || 4848.00   // Trigger max accommodation cap
        false       | 8                    | 12500.00    | 2490.00         | 273.00                || 17857.00
        false       | 6                    | 5186.00     | 4203.00         | 452.00                || 6621.00
        true        | 9                    | 12578.00    | 1223.00         | 628.00                || 22112.00
        false       | 7                    | 9685.00     | 578.00          | 1088.00               || 15124.00
        true        | 7                    | 5317.00     | 2959.00         | 1768.00               || 9948.00   // Trigger max accommodation cap
        true        | 2                    | 6004.00     | 563.00          | 834.00                || 7137.00
        true        | 7                    | 10967.00    | 2381.00         | 616.00                || 16825.00

    }

}
