package uk.gov.digital.ho.proving.financialstatus.api.test.tier2and5

import org.springframework.context.support.ResourceBundleMessageSource
import uk.gov.digital.ho.proving.financialstatus.domain.ApplicantTypeChecker
import uk.gov.digital.ho.proving.financialstatus.domain.MaintenanceThresholdCalculatorT2AndT5

class TestUtilsTier2And5 {

    public static def thresholdUrl = "/pttg/financialstatus/v1/t2/maintenance/threshold"

    static getMessageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages")
        messageSource
    }

    private static def MAIN_APPLICANT_VALUE = 945
    private static def DEPENDANT_APPLICANT_VALUE = 630
    private static def YOUTH_MOBILITY_APPLICANT_VALUE = 1890

    static def getApplicantTypeChecker() { new ApplicantTypeChecker("main", "dependant") }

    static def maintenanceThresholdServiceBuilder() {
        new MaintenanceThresholdCalculatorT2AndT5(MAIN_APPLICANT_VALUE, DEPENDANT_APPLICANT_VALUE, YOUTH_MOBILITY_APPLICANT_VALUE)
    }

}
