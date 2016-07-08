package acceptance;

import cucumber.api.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(CucumberWithSerenity.class)
@CucumberOptions(features={"src/test/specs"} , glue={"steps"}, tags = {"@wiremock"})
public class AcceptanceTests {
    @Test
    public void test(){

    }
}
