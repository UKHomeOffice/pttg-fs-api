package uk.gov.digital.ho.proving.financialstatus.audit

import spock.lang.Specification
import uk.gov.digital.ho.proving.financialstatus.api.test.DataUtils

class NewLineRemoverSpec extends Specification {

    def underTest = DataUtils.createNewLineRemover()

    def 'Replaces unix newlines with spaces'() {
        given:
        def originalString = "\nx\ny\nz\n"

        when:
        def actualResultString = underTest.removeNewlines(originalString)

        then:
        def expectedResultString = " x y z "
        actualResultString == expectedResultString
    }

    def 'replaces carriage returns with spaces'() {
        given:
        def originalString = "\ra\rb\rc\r"

        when:
        def actualResultString = underTest.removeNewlines(originalString)

        then:
        def expectedResultString = " a b c "
        actualResultString == expectedResultString
    }

    def 'replaces new line carriage return pairs with spaces'() {
        given:
        def originalString = "\r\nz\r\ny\r\nx\r\n"

        when:
        def actualResultString = underTest.removeNewlines(originalString)

        then:
        def expectedResultString = " z y x "
        actualResultString == expectedResultString
    }

}
