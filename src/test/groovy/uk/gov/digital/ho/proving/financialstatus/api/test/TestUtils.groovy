package uk.gov.digital.ho.proving.financialstatus.api.test

import org.springframework.context.support.ResourceBundleMessageSource

class TestUtils {

    public static def thresholdUrl = "/pttg/financialstatusservice/v1/maintenance/threshold"

    public static getMessageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages")
        messageSource
    }

}
