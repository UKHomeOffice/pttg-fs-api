package uk.gov.digital.ho.proving.financialstatus.api.test

import org.springframework.context.support.ResourceBundleMessageSource
import uk.gov.digital.ho.proving.financialstatus.api.validation.ServiceMessages
import uk.gov.digital.ho.proving.financialstatus.domain.StudentTypeChecker

class TestUtils {

    public static def thresholdUrl = "/pttg/financialstatusservice/v1/maintenance/threshold"

    public static getMessageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages")
        messageSource
    }

    public static def inLondonMaintenance = 1265
    public static def notInLondonMaintenance = 1015
    public static def maxMaintenanceAllowance = 1265
    public static def inLondonDependant = 845
    public static def notInLondonDependant = 680

    public static def nonDoctorateMinCourseLength = 1
    public static def nonDoctorateMaxCourseLength = 9
    public static def nonDoctorateMinCourseLengthWithDependants = 7

    public static def nonDoctorateLeaveToRemainBoundary = 12
    public static def nonDoctorateShortLeaveToRemain = 2
    public static def nonDoctorateLongLeaveToRemain = 4

    public static def pgddSsoMinCourseLength = 1
    public static def pgddSsoMaxCourseLength = 2
    public static def doctorateFixedCourseLength = 2


    public static def getStudentTypeChecker() { new StudentTypeChecker("doctorate", "nondoctorate", "pgdd", "sso") }

}
