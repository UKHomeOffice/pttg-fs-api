package acceptance;

import cucumber.api.CucumberOptions;
import net.serenitybdd.cucumber.*;
import org.junit.runner.*;

@RunWith(CucumberWithSerenity.class)
@CucumberOptions(features={"src/test/specs"} , glue={"steps"}, tags="@InnerLondonBorough")

public class AcceptanceTests {
}
