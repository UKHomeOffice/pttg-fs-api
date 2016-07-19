package acceptance;

import cucumber.api.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import org.junit.runner.RunWith;


@RunWith(CucumberWithSerenity.class)
@CucumberOptions(features={"src/test/specs/design-r4"} , glue={"steps"})

public class AcceptanceTests {
}
