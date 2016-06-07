package steps

import com.jayway.restassured.response.Response
import cucumber.api.DataTable
import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import cucumber.api.java.en.When
import net.thucydides.core.annotations.Managed
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.text.WordUtils

import static com.jayway.jsonpath.JsonPath.read
import static com.jayway.restassured.RestAssured.get

class FinancialStatusApiSteps {
    @Managed
    public Response resp
    String jsonAsString
    String dependants = ""
    String applicationRaisedDate
    String applicantDateOfBirth
    String accountNumber = ""
    String sortCode
    String courseLength
    String tutionFee
    String tutionFeePaid
    String accomodationFeePaid

    def String toCamelCase(String s) {
        String allUpper = StringUtils.remove(WordUtils.capitalizeFully(s), " ")
        String camelCase = allUpper[0].toLowerCase() + allUpper.substring(1)
        camelCase
    }

    def String getTableData(DataTable arg) {
        Map<String, String> entries = arg.asMap(String.class, String.class)
        String[] tableKey = entries.keySet()

        for (String s : tableKey) {

            if (s.equalsIgnoreCase("application raised date")) {
                applicationRaisedDate = entries.get(s)
            }
            if (s.equalsIgnoreCase("appilcation date of birth")) {
                applicantDateOfBirth = entries.get(s)
            }

            if (s.equalsIgnoreCase("Account Number")) {
                accountNumber = entries.get(s)
            }

            if(s.equalsIgnoreCase("Sort Code")){
                sortCode = entries.get(s)
            }
            if(s.equalsIgnoreCase("course length")){
                courseLength = entries.get(s)
            }
            if(s.equalsIgnoreCase("tution fee")){
                tutionFee = entries.get(s)
            }
            if(s.equalsIgnoreCase("tution fee paid")){
                tutionFeePaid = entries.get(s)
            }
            if(s.equalsIgnoreCase("accomodation fee paid")){
                accomodationFeePaid = entries.get(s)
            }
        }

    }


    public void validateJsonResult(DataTable arg) {
        Map<String, String> entries = arg.asMap(String.class, String.class);
        String[] tableKey = entries.keySet();

        for (String key : tableKey) {
            switch (key) {
                case "HTTP Status":
                    assert entries.get(key) == resp.getStatusCode().toString();
                    break;
                default:
                    String jsonPath = FeatureKeyMapper.buildJsonPath(key);
                    assert entries.get(key) == read(jsonAsString, jsonPath).toString();
            }
        }
    }


    @Given("^A service is consuming the Income Proving TM Family API\$")
    public void a_service_is_consuming_the_Income_Proving_TM_Family_API() throws Throwable {

    }

    @When("^the Income Proving TM Family API is invoked with the following:\$")
    public void the_Income_Proving_TM_Family_API_is_invoked_with_the_following(DataTable arg1) throws Throwable {
        resp = get("http://localhost:8080/incomeproving/v1/individual/threshold/")
        jsonAsString = resp.asString()

        println ("Family Case Worker API: "+ jsonAsString)
    }

    @Then("^The Income Proving TM Family API provides the following result:\$")
    public void the_Income_Proving_TM_Family_API_provides_the_following_result(DataTable arg1) throws Throwable {
      validateJsonResult(arg1)
    }


}
