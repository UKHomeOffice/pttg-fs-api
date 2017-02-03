package uk.gov.digital.ho.proving.financialstatus.api.test.tier4

import spock.lang.Specification
import uk.gov.digital.ho.proving.financialstatus.api.test.DataUtils
import uk.gov.digital.ho.proving.financialstatus.domain.MaintenanceThresholdCalculator

class DoctorateExtensionMaintenanceThresholdCalculatorTest extends Specification {

    MaintenanceThresholdCalculator maintenanceThresholdCalculator = TestUtilsTier4.maintenanceThresholdServiceBuilder()

    def bd(a) { new scala.math.BigDecimal(a) }

    def "Tier 4 Doctorate - Check 'Non Inner London Borough'"() {

        expect:
        maintenanceThresholdCalculator.calculateDoctorateExtensionScheme(inLondon, bd(accommodationFeesPaid), dependants)._1 == bd(threshold)

        where:
        inLondon | accommodationFeesPaid | dependants || threshold
        false    | 0.00                  | 5          || 8830.00
        false    | 0.00                  | 7          || 11550.00


    }

    def "Tier 4 Doctorate - Check 'Inner London Borough'"() {

        expect:
        maintenanceThresholdCalculator.calculateDoctorateExtensionScheme(inLondon, bd(accommodationFeesPaid), dependants)._1 == bd(threshold)

        where:
        inLondon | accommodationFeesPaid | dependants || threshold
        true     | 0.00                  | 4          || 9290.00
        true     | 0.00                  | 15         || 27880.00


    }

    def "Tier 4 Doctorate - Check 'Accommodation Fees paid'"() {

        expect:
        maintenanceThresholdCalculator.calculateDoctorateExtensionScheme(inLondon, bd(accommodationFeesPaid), dependants)._1 == bd(threshold)

        where:
        inLondon | accommodationFeesPaid | dependants || threshold
        true     | 1039.00               | 14         || 25151.00
        true     | 692.00                | 11         || 20428.00
        true     | 622.00                | 3          || 6978.00
        true     | 154.00                | 9          || 17586.00
        true     | 869.00                | 10         || 18561.00
        false    | 860.00                | 12         || 17490.00
        false    | 206.00                | 9          || 14064.00
        false    | 106.00                | 11         || 16884.00
        false    | 1245.00               | 0          || 785.00
        false    | 2106.00               | 11         || 15725.00
        false    | 1845.00               | 0          || 765.00

    }


    def "Tier 4 Doctorate - Check 'All variants'"() {

        expect:
        def response = maintenanceThresholdCalculator.calculateDoctorateExtensionScheme(inLondon, bd(accommodationFeesPaid), dependants)
        def thresholdValue = response._1
        def cappedValues = DataUtils.getCappedValues(response._2)
        def cappedAccommodation = cappedValues.accommodationFeesPaid()

        assert thresholdValue == bd(threshold)
        assert DataUtils.compareAccommodationFees(bd(feesCapped), cappedAccommodation)

        where:
        // Due to groovy not liking Scala's 'None' object we represent this as the value zero
        inLondon | accommodationFeesPaid | dependants || threshold || feesCapped
        true     | 887.96                | 2          || 5022.04   || 0.00
        false    | 1561.61               | 8          || 11645.00  || 1265.00
        false    | 313.14                | 9          || 13956.86  || 0.00
        false    | 839.30                | 5          || 7990.70   || 0.00
        false    | 1999.65               | 3          || 4845.00   || 1265.00
        false    | 1807.86               | 3          || 4845.00   || 1265.00
        false    | 731.08                | 11         || 16258.92  || 0.00
        false    | 1697.75               | 5          || 7565.00   || 1265.00
        true     | 1238.75               | 5          || 9741.25   || 0.00
        true     | 1539.94               | 14         || 24925.00  || 1265.00
        true     | 108.92                | 13         || 24391.08  || 0.00
        true     | 1245.11               | 6          || 11424.89  || 0.00
        false    | 544.57                | 0          || 1485.43   || 0.00
        true     | 1921.41               | 0          || 1265.00   || 1265.00
        false    | 358.24                | 1          || 3031.76   || 0.00
        true     | 888.37                | 6          || 11781.63  || 0.00
        false    | 1235.56               | 7          || 10314.44  || 0.00
        true     | 137.34                | 4          || 9152.66   || 0.00
        false    | 775.94                | 0          || 1254.06   || 0.00
        false    | 1214.73               | 0          || 815.27    || 0.00
        false    | 1779.54               | 5          || 7565.00   || 1265.00
        true     | 1675.86               | 12         || 21545.00  || 1265.00
        true     | 542.20                | 0          || 1987.80   || 0.00
        true     | 974.76                | 9          || 16765.24  || 0.00
        true     | 1179.37               | 7          || 13180.63  || 0.00

    }

}
