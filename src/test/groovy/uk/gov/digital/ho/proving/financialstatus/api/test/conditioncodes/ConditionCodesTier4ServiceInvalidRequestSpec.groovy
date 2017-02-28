package uk.gov.digital.ho.proving.financialstatus.api.test.conditioncodes

import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import spock.lang.Specification
import uk.gov.digital.ho.proving.financialstatus.api.configuration.ServiceConfiguration
import uk.gov.digital.ho.proving.financialstatus.audit.EmbeddedMongoClientConfiguration

@WebAppConfiguration
@ContextConfiguration(classes = [ ServiceConfiguration.class, EmbeddedMongoClientConfiguration.class ])
class ConditionCodesTier4ServiceInvalidRequestSpec extends Specification {


}
