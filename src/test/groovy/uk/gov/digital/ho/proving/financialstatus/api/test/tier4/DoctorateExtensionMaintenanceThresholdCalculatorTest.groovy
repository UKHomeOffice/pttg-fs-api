package uk.gov.digital.ho.proving.financialstatus.api.test.tier4

import spock.lang.Specification
import uk.gov.digital.ho.proving.financialstatus.api.test.DataUtils
import uk.gov.digital.ho.proving.financialstatus.domain.MaintenanceThresholdCalculatorT4

class DoctorateExtensionMaintenanceThresholdCalculatorTest extends Specification {

    MaintenanceThresholdCalculatorT4 maintenanceThresholdCalculator = TestUtilsTier4.maintenanceThresholdServiceBuilder()

    def bd(a) { new scala.math.BigDecimal(a) }

    def "Tier 4 Doctorate - Check 'Non Inner London Borough'"() {

        expect:
        maintenanceThresholdCalculator.calculateDoctorateExtensionScheme(inLondon, bd(accommodationFeesPaid), dependants, dependantsOnly)._1 == bd(threshold)

        where:
        inLondon | accommodationFeesPaid | dependants | dependantsOnly || threshold || feesCapped
        false    | 0.00                  | 2          | false          || 4750.00   || 0.00
        false    | 0.00                  | 6          | false          || 10190.00  || 0.00
        false    | 1575.72               | 8          | false          || 11645.00  || 1265.00
        false    | 396.97                | 6          | false          || 9793.03   || 0.00
        false    | 1699.15               | 2          | false          || 3485.00   || 1265.00
        false    | 1950.28               | 1          | false          || 2125.00   || 1265.00
        false    | 672.51                | 13         | false          || 19037.49  || 0.00
        false    | 580.69                | 1          | false          || 2809.31   || 0.00
    }

    def "Tier 4 Doctorate - Check 'Inner London Borough'"() {

        expect:
        maintenanceThresholdCalculator.calculateDoctorateExtensionScheme(inLondon, bd(accommodationFeesPaid), dependants, dependantsOnly)._1 == bd(threshold)

        where:
        inLondon | accommodationFeesPaid | dependants | dependantsOnly || threshold || feesCapped
        true     | 1036.43               | 6          | false          || 11633.57  || 0.00
        true     | 266.29                | 2          | false          || 5643.71   || 0.00
        true     | 0.00                  | 2          | false          || 5910.00   || 0.00
        true     | 722.09                | 4          | false          || 8567.91   || 0.00
        true     | 0.00                  | 14         | false          || 26190.00  || 0.00
        true     | 0.00                  | 12         | false          || 22810.00  || 0.00
        true     | 0.00                  | 11         | false          || 21120.00  || 0.00
        true     | 1466.35               | 0          | false          || 1265.00   || 1265.00


    }

    def "Tier 4 Doctorate - Check 'Accommodation Fees paid'"() {

        expect:
        maintenanceThresholdCalculator.calculateDoctorateExtensionScheme(inLondon, bd(accommodationFeesPaid), dependants, dependantsOnly)._1 == bd(threshold)

        where:
        inLondon | accommodationFeesPaid | dependants | dependantsOnly || threshold || feesCapped
        false    | 1871.32               | 8          | false          || 11645.00  || 1265.00
        false    | 1560.76               | 1          | false          || 2125.00   || 1265.00
        false    | 1511.78               | 11         | false          || 15725.00  || 1265.00
        false    | 1597.18               | 14         | false          || 19805.00  || 1265.00
        true     | 555.16                | 12         | false          || 22254.84  || 0.00
        false    | 837.79                | 12         | false          || 17512.21  || 0.00
        false    | 1659.91               | 3          | false          || 4845.00   || 1265.00
        true     | 1506.09               | 0          | false          || 1265.00   || 1265.00
        false    | 139.26                | 0          | false          || 1890.74   || 0.00

    }

    // Dependants only

    def "Tier 4 Doctorate - Check 'Non Inner London Borough' dependants only"() {

        expect:
        maintenanceThresholdCalculator.calculateDoctorateExtensionScheme(inLondon, bd(accommodationFeesPaid), dependants, dependantsOnly)._1 == bd(threshold)

        where:
        inLondon | accommodationFeesPaid | dependants | dependantsOnly || threshold || feesCapped
        false    | 0.00                  | 12         | true           || 16320.00  || 0.00
        false    | 0.00                  | 5          | true           || 6800.00   || 0.00
        false    | 0.00                  | 11         | true           || 14960.00  || 0.00
        false    | 0.00                  | 7          | true           || 9520.00   || 0.00
        false    | 0.00                  | 12         | true           || 16320.00  || 0.00
        false    | 0.00                  | 12         | true           || 16320.00  || 0.00
        false    | 0.00                  | 10         | true           || 13600.00  || 0.00
        false    | 0.00                  | 14         | true           || 19040.00  || 0.00
        false    | 0.00                  | 1          | true           || 1360.00   || 0.00
        false    | 0.00                  | 7          | true           || 9520.00   || 0.00


    }

    def "Tier 4 Doctorate - Check 'Inner London Borough' dependants only"() {

        expect:
        maintenanceThresholdCalculator.calculateDoctorateExtensionScheme(inLondon, bd(accommodationFeesPaid), dependants, dependantsOnly)._1 == bd(threshold)

        where:
        inLondon | accommodationFeesPaid | dependants | dependantsOnly || threshold || feesCapped
        true     | 0.00                  | 10         | true           || 16900.00  || 0.00
        true     | 0.00                  | 13         | true           || 21970.00  || 0.00
        true     | 0.00                  | 1          | true           || 1690.00   || 0.00
        true     | 0.00                  | 8          | true           || 13520.00  || 0.00
        true     | 0.00                  | 2          | true           || 3380.00   || 0.00
        true     | 0.00                  | 10         | true           || 16900.00  || 0.00
        true     | 0.00                  | 8          | true           || 13520.00  || 0.00
        true     | 0.00                  | 1          | true           || 1690.00   || 0.00
        true     | 0.00                  | 12         | true           || 20280.00  || 0.00
        true     | 0.00                  | 14         | true           || 23660.00  || 0.00

    }

    // All variants

    def "Tier 4 Doctorate - Check 'All variants'"() {

        expect:
        def response = maintenanceThresholdCalculator.calculateDoctorateExtensionScheme(inLondon, bd(accommodationFeesPaid), dependants, dependantsOnly)
        def thresholdValue = response._1
        def cappedValues = DataUtils.getCappedValues(response._2)
        def cappedAccommodation = cappedValues.accommodationFeesPaid()

        assert thresholdValue == bd(threshold)
        assert DataUtils.compareAccommodationFees(bd(feesCapped), cappedAccommodation)

        where:
        // Due to groovy not liking Scala's 'None' object we represent this as the value zero
        inLondon | accommodationFeesPaid | dependants | dependantsOnly || threshold || feesCapped
        true     | 0.00                  | 5          | false          || 10980.00  || 0.00
        true     | 643.74                | 13         | true           || 21970.00  || 0.00
        true     | 0.00                  | 9          | false          || 17740.00  || 0.00
        false    | 1063.28               | 6          | true           || 8160.00   || 0.00
        false    | 1496.29               | 0          | true           || 0.00      || 1265.00
        false    | 0.00                  | 8          | true           || 10880.00  || 0.00
        true     | 500.84                | 5          | true           || 8450.00   || 0.00
        false    | 0.00                  | 6          | false          || 10190.00  || 0.00
        false    | 1905.71               | 3          | false          || 4845.00   || 1265.00
        false    | 617.84                | 0          | false          || 1412.16   || 0.00
        true     | 1858.25               | 10         | true           || 16900.00  || 1265.00
        false    | 0.00                  | 0          | false          || 2030.00   || 0.00
        true     | 266.14                | 0          | false          || 2263.86   || 0.00
        true     | 0.00                  | 13         | false          || 24500.00  || 0.00
        true     | 0.00                  | 12         | true           || 20280.00  || 0.00
        false    | 0.00                  | 7          | true           || 9520.00   || 0.00
        true     | 0.00                  | 5          | false          || 10980.00  || 0.00
        false    | 0.00                  | 2          | true           || 2720.00   || 0.00
        true     | 602.21                | 8          | true           || 13520.00  || 0.00
        false    | 359.22                | 12         | false          || 17990.78  || 0.00
        false    | 0.00                  | 0          | true           || 0.00      || 0.00
        true     | 1059.60               | 12         | false          || 21750.40  || 0.00
        false    | 0.00                  | 5          | false          || 8830.00   || 0.00
        true     | 585.44                | 13         | true           || 21970.00  || 0.00
        false    | 0.00                  | 6          | false          || 10190.00  || 0.00
    }


}
