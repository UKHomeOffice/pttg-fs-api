package acceptance;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import net.serenitybdd.cucumber.*;
import org.junit.runner.*;

@RunWith(Cucumber.class)
@CucumberOptions(features={"src/test/specs"} , glue={"steps"}, tags = {"@wiremock"})

public class AcceptanceTests {
}
