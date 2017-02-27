package uk.gov.digital.ho.proving.financialstatus.api.test.conditioncodes

import spock.lang.Specification
import uk.gov.digital.ho.proving.financialstatus.domain.DoctorateExtensionStudent$
import uk.gov.digital.ho.proving.financialstatus.domain.GeneralStudent$
import uk.gov.digital.ho.proving.financialstatus.domain.PostGraduateDoctorDentistStudent$
import uk.gov.digital.ho.proving.financialstatus.domain.StudentUnionSabbaticalOfficerStudent$
import uk.gov.digital.ho.proving.financialstatus.domain.conditioncodes.ConditionCodesCalculatorProvider
import uk.gov.digital.ho.proving.financialstatus.domain.conditioncodes.GeneralConditionCodesCalculator
import uk.gov.digital.ho.proving.financialstatus.domain.conditioncodes.OtherNonGeneralConditionCodesCalculator

class ConditionCodesCalculatorProviderSpec extends Specification {

    def 'provides GeneralConditionCodesCalculator for a General student'() {
        given:

        def underTest = new ConditionCodesCalculatorProvider()

        when:
        def calculator = underTest.provide(GeneralStudent$.MODULE$)

        then:

        assert calculator.valid
        assert calculator.toOption().get() instanceof GeneralConditionCodesCalculator

    }

    def 'provides OtherNonGeneralConditionCodesCalculator for a Doctorate Extension student'() {
        given:

        def underTest = new ConditionCodesCalculatorProvider()

        when:
        def calculator = underTest.provide(DoctorateExtensionStudent$.MODULE$)

        then:
        assert calculator.valid
        assert calculator.toOption().get() instanceof OtherNonGeneralConditionCodesCalculator
    }

    def 'provides OtherNonGeneralConditionCodesCalculator for a Post Graduate Doctor Dentist student'() {
        given:

        def underTest = new ConditionCodesCalculatorProvider()

        when:
        def calculator = underTest.provide(PostGraduateDoctorDentistStudent$.MODULE$)

        then:
        assert calculator.valid
        assert calculator.toOption().get() instanceof OtherNonGeneralConditionCodesCalculator
    }

    def 'provides OtherNonGeneralConditionCodesCalculator for a Student Union Sabbatical Officer student'() {
        given:

        def underTest = new ConditionCodesCalculatorProvider()

        when:
        def calculator = underTest.provide(StudentUnionSabbaticalOfficerStudent$.MODULE$)

        then:
        assert calculator.valid
        assert calculator.toOption().get() instanceof OtherNonGeneralConditionCodesCalculator
    }
}
