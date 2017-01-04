package uk.gov.digital.ho.proving.financialstatus.api.test

import spock.lang.Specification
import uk.gov.digital.ho.proving.financialstatus.domain.MaintenanceThresholdCalculator
import static uk.gov.digital.ho.proving.financialstatus.api.test.TestUtils.*

class DoctorateMaintenanceThresholdCalculatorTest extends Specification {

    MaintenanceThresholdCalculator maintenanceThresholdCalculator =
        new MaintenanceThresholdCalculator(inLondonMaintenance, notInLondonMaintenance,
            maxMaintenanceAllowance, inLondonDependant, notInLondonDependant,
            nonDoctorateMinCourseLength, nonDoctorateMaxCourseLength,
            pgddSsoMinCourseLength, pgddSsoMaxCourseLength, doctorateFixedCourseLength,
            susoMinCourseLength, susoMaxCourseLength
        )

    def bd(a) { new scala.math.BigDecimal(a) }

    def "Tier 4 Doctorate - Check 'Non Inner London Borough'"() {

        expect:
        maintenanceThresholdCalculator.calculateDES(inLondon, bd(accommodationFeesPaid), dependants)._1 == bd(threshold)

        where:
        inLondon | accommodationFeesPaid | dependants || threshold
        false    | 0.00                  | 5          || 8830.00
        false    | 0.00                  | 7          || 11550.00


    }

    def "Tier 4 Doctorate - Check 'Inner London Borough'"() {

        expect:
        maintenanceThresholdCalculator.calculateDES(inLondon, bd(accommodationFeesPaid), dependants)._1 == bd(threshold)

        where:
        inLondon | accommodationFeesPaid | dependants || threshold
        true     | 0.00                  | 4          || 9290.00
        true     | 0.00                  | 15         || 27880.00


    }

    def "Tier 4 Doctorate - Check 'Accommodation Fees paid'"() {

        expect:
        maintenanceThresholdCalculator.calculateDES(inLondon, bd(accommodationFeesPaid), dependants)._1 == bd(threshold)

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
        def response = maintenanceThresholdCalculator.calculateDES(inLondon, bd(accommodationFeesPaid), dependants)
        def thresholdValue = response._1
        def cappedValues = DataUtils.getCappedValues(response._2)
        def cappedAccommodation = cappedValues.accommodationFeesPaid()

        assert thresholdValue == bd(threshold)
        assert DataUtils.compareAccommodationFees(bd(feesCapped), cappedAccommodation)

        where:
        // Dues to groovy not liking Scala's 'None' object we represent this as the value zero
        inLondon | accommodationFeesPaid | dependants || threshold || feesCapped
        false    | 1627.00               | 15         || 21165.00  || 1265.00
        false    | 270.00                | 10         || 15360.00  || 0
        true     | 22.00                 | 1          || 4198.00   || 0
        true     | 636.00                | 9          || 17104.00  || 0
        false    | 1018.00               | 3          || 5092.00   || 0
        true     | 446.00                | 6          || 12224.00  || 0
        false    | 372.00                | 6          || 9818.00   || 0
        true     | 657.00                | 13         || 23843.00  || 0
        true     | 953.00                | 6          || 11717.00  || 0
        true     | 229.00                | 12         || 22581.00  || 0
        true     | 23.00                 | 12         || 22787.00  || 0
        false    | 182.00                | 14         || 20888.00  || 0
        false    | 738.00                | 12         || 17612.00  || 0
        true     | 73.00                 | 9          || 17667.00  || 0
        false    | 970.00                | 6          || 9220.00   || 0
        true     | 1934.00               | 5          || 9715.00   || 1265.00
        true     | 223.00                | 4          || 9067.00   || 0
        true     | 1078.00               | 14         || 25112.00  || 0

    }

}
