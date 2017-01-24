package uk.gov.digital.ho.proving.financialstatus.api.test.consent

import org.springframework.context.support.ResourceBundleMessageSource
import uk.gov.digital.ho.proving.financialstatus.domain.ApplicantTypeChecker
import uk.gov.digital.ho.proving.financialstatus.domain.MaintenanceThresholdCalculatorT2AndT5

class ConsentUtils {

    public static def consentUrl = "/pttg/financialstatus/v1/accounts/%s/%s/consent"

    static getMessageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages")
        messageSource
    }

    private static def MAIN_APPLICANT_VALUE = 945
    private static def DEPENDANT_APPLICANT_VALUE = 630

    static def getApplicantTypeChecker() { new ApplicantTypeChecker("main", "dependant") }

    static def maintenanceThresholdServiceBuilder() {
        new MaintenanceThresholdCalculatorT2AndT5(MAIN_APPLICANT_VALUE, DEPENDANT_APPLICANT_VALUE)
    }

}
