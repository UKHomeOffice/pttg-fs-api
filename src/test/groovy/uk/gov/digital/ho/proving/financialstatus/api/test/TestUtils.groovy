package uk.gov.digital.ho.proving.financialstatus.api.test

import org.springframework.context.support.ResourceBundleMessageSource

class TestUtils {

    public static def thresholdUrl = "/pttg/financialstatusservice/v1/maintenance/threshold"

    public static getMessageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages")
        messageSource
    }

    public static def innerLondonMaintenance = 1265
    public static def nonInnerLondonMaintenance = 1015
    public static def maxMaintenanceAllowance = 1265
    public static def maxDoctorateMonths = 2
    public static def innerLondonDependant = 845
    public static def nonInnerLondonDependant = 680


}
