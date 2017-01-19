package uk.gov.digital.ho.proving.financialstatus.api.test.tier2and5

import spock.lang.Specification
import uk.gov.digital.ho.proving.financialstatus.domain.ApplicantTypeChecker
import uk.gov.digital.ho.proving.financialstatus.domain.MaintenanceThresholdCalculatorT2AndT5

import static uk.gov.digital.ho.proving.financialstatus.api.test.DataUtils.buildScalaBigDecimal

class ApplicantMaintenanceThresholdCalculatorTest extends Specification {

    MaintenanceThresholdCalculatorT2AndT5 maintenanceThresholdCalculator = TestUtilsTier2And5.maintenanceThresholdServiceBuilder()

    def "Tier 2/5 Applicant threshold calculation"() {

        expect:
        def response = maintenanceThresholdCalculator.calculateThresholdForT2AndT5(new ApplicantTypeChecker("main", "dependant").getApplicantType(applicantType), dependants)
        assert (response.get() == buildScalaBigDecimal(threshold))

        where:
        applicantType | dependants || threshold
        "main"        | 0          || 945
        "main"        | 1          || 1575
        "main"        | 2          || 2205
        "main"        | 3          || 2835
        "main"        | 4          || 3465
        "main"        | 5          || 4095
        "main"        | 6          || 4725
        "main"        | 7          || 5355
        "main"        | 8          || 5985
        "main"        | 9          || 6615
        "main"        | 10         || 7245
        "main"        | 11         || 7875
        "main"        | 12         || 8505
        "main"        | 13         || 9135
        "main"        | 14         || 9765
        "main"        | 15         || 10395
        "main"        | 16         || 11025
        "main"        | 17         || 11655
        "main"        | 18         || 12285
        "main"        | 19         || 12915
        "main"        | 20         || 13545
        "dependant"   | 0          || 630
        "dependant"   | 1          || 630
        "dependant"   | 2          || 630
        "dependant"   | 3          || 630
        "dependant"   | 4          || 630
    }

}
