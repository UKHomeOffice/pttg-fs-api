package uk.gov.digital.ho.proving.financialstatus.api.test.tier2and5

import spock.lang.Specification
import uk.gov.digital.ho.proving.financialstatus.domain.ApplicantTypeChecker
import uk.gov.digital.ho.proving.financialstatus.domain.MaintenanceThresholdCalculatorT2AndT5
import uk.gov.digital.ho.proving.financialstatus.domain.Tier2
import uk.gov.digital.ho.proving.financialstatus.domain.TierChecker

import static uk.gov.digital.ho.proving.financialstatus.api.test.DataUtils.buildScalaBigDecimal

class ApplicantMaintenanceThresholdCalculatorTest extends Specification {

    MaintenanceThresholdCalculatorT2AndT5 maintenanceThresholdCalculator = TestUtilsTier2And5.maintenanceThresholdServiceBuilder()

    def "Tier 2/5 Applicant threshold calculation"() {

        expect:
        def response = maintenanceThresholdCalculator.calculateThresholdForT2AndT5(new TierChecker("t2", "t4", "t5").getTier(tier),
                                                                                    new ApplicantTypeChecker("main", "dependant").getApplicantType(applicantType),
                                                                                    null,
                                                                                    dependants)
        assert (response.get() == buildScalaBigDecimal(threshold))

        where:
        tier    | applicantType | dependants || threshold
        "t2"    | "main"        | 0          || 945
        "t2"    | "main"        | 1          || 1575
        "t2"    | "main"        | 2          || 2205
        "t2"    | "main"        | 3          || 2835
        "t2"    | "main"        | 4          || 3465
        "t2"    | "main"        | 5          || 4095
        "t2"    | "main"        | 6          || 4725
        "t2"    | "main"        | 7          || 5355
        "t2"    | "main"        | 8          || 5985
        "t2"    | "main"        | 9          || 6615
        "t2"    | "main"        | 10         || 7245
        "t2"    | "main"        | 11         || 7875
        "t2"    | "main"        | 12         || 8505
        "t2"    | "main"        | 13         || 9135
        "t2"    | "main"        | 14         || 9765
        "t2"    | "main"        | 15         || 10395
        "t2"    | "main"        | 16         || 11025
        "t2"    | "main"        | 17         || 11655
        "t2"    | "main"        | 18         || 12285
        "t2"    | "main"        | 19         || 12915
        "t2"    | "main"        | 20         || 13545
        "t2"    | "dependant"   | 0          || 0
        "t2"    | "dependant"   | 1          || 630
        "t2"    | "dependant"   | 2          || 1260
        "t2"    | "dependant"   | 3          || 1890
        "t2"    | "dependant"   | 4          || 2520
    }

}
