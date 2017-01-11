package uk.gov.digital.ho.proving.financialstatus.api.test.tier4

import org.springframework.context.support.ResourceBundleMessageSource
import uk.gov.digital.ho.proving.financialstatus.api.validation.ServiceMessages
import uk.gov.digital.ho.proving.financialstatus.domain.ApplicantTypeChecker
import uk.gov.digital.ho.proving.financialstatus.domain.CourseTypeChecker
import uk.gov.digital.ho.proving.financialstatus.domain.MaintenanceThresholdCalculator
import uk.gov.digital.ho.proving.financialstatus.domain.StudentTypeChecker

class TestUtilsTier4 {

    public static def thresholdUrl = "/pttg/financialstatus/v1/t4/maintenance/threshold"

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

    public static def nonDoctorateCappedCourseLength = 9

    public static def susoCappedCourseLength = 2

    public static def pgddSsoCappedCourseLength = 2
    public static def doctorateFixedCourseLength = 2


    public static def getStudentTypeChecker() { new StudentTypeChecker("doctorate", "nondoctorate", "pgdd", "sso") }

    public static def getCourseTypeChecker() { new CourseTypeChecker("main", "pre-sessional") }

    public static def maintenanceThresholdServiceBuilder() {
        new MaintenanceThresholdCalculator(
            inLondonMaintenance,
            notInLondonMaintenance,
            maxMaintenanceAllowance,
            inLondonDependant,
            notInLondonDependant,
            //nonDoctorateMinCourseLength,
            nonDoctorateCappedCourseLength,
            //pgddSsoMinCourseLength,
            pgddSsoCappedCourseLength,
            doctorateFixedCourseLength,
            //susoMinCourseLength,
            susoCappedCourseLength
        )
    }

}
