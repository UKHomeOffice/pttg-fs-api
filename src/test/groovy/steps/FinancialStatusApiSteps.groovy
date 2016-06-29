package steps

import com.jayway.restassured.response.Response
import cucumber.api.DataTable
import cucumber.api.Scenario
import cucumber.api.java.After
import cucumber.api.java.Before
import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import cucumber.api.java.en.When
import net.thucydides.core.annotations.Managed
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.text.WordUtils
import org.json.JSONObject

import static com.jayway.jsonpath.JsonPath.read
import static com.jayway.restassured.RestAssured.get

class FinancialStatusApiSteps {
    @Managed
    public Response resp
    String jsonAsString
    String dependants = ""
    String fromDate = ""
    String toDate = ""
    String accountNumber = ""
    String sortCode = ""
    String minimum = ""
    String days = ""
    boolean innerLondon
    String courseLength = ""
    String tuitionFees = ""
    String tuitionFeesPaid = ""
    String accommodationFeesPaid = ""
    def barclaysStubHost = "localhost"
    def barclaysStubPort = 8082
    def testDataLoader

    @Before
    def setUp(Scenario scenario) {
        testDataLoader = new TestDataLoader(barclaysStubHost, barclaysStubPort)
        testDataLoader.prepareFor(scenario)
    }

    @After
    def tearDown() {
        testDataLoader?.clearTestData()
    }

    def String toCamelCase(String s) {
        String allUpper = StringUtils.remove(WordUtils.capitalizeFully(s), " ")
        String camelCase = allUpper[0].toLowerCase() + allUpper.substring(1)
        camelCase
    }

    def String getTableData(DataTable arg) {
        Map<String, String> entries = arg.asMap(String.class, String.class)
        String[] tableKey = entries.keySet()

        for (String s : tableKey) {
            if (s.equalsIgnoreCase("Account Number")) {
                accountNumber = entries.get(s)
            }
            if(s.equalsIgnoreCase("Minimum")){
                minimum = entries.get(s)
            }
            if(s.equalsIgnoreCase("From Date")){
                fromDate = entries.get(s)
            }
            if(s.equalsIgnoreCase("Sort Code")){
                sortCode = entries.get(s)
            }
            if(s.equalsIgnoreCase("To Date")){
                toDate = entries.get(s)
            }
            if(s.equalsIgnoreCase("Course Length")){
                courseLength  = entries.get(s)
            }
            if(s.equalsIgnoreCase("Total tuition fees")){
                tuitionFees = entries.get(s)
            }
            if(s.equalsIgnoreCase("Inner London Borough") && entries.get(s).equalsIgnoreCase("Yes")){
                innerLondon = true
            }
            if(s.equalsIgnoreCase("Inner London Borough") && entries.get(s).equalsIgnoreCase("No")){
                innerLondon = false
            }
            if(s.equalsIgnoreCase("Inner London Borough") && entries.get(s).equalsIgnoreCase(" ")){
                innerLondon = null
            }

            if(s.equalsIgnoreCase("Tuition fees already paid")){
                tuitionFeesPaid = entries.get(s)
            }

            if(s.equalsIgnoreCase("Accommodation fees already paid")){
                accommodationFeesPaid = entries.get(s)
            }

        }
    }

    public String tocamelcase(String g) {
        StringBuilder sbl = new StringBuilder()

        String firstString
        String nextString
        String finalString = null
        char firstChar

        String[] f = g.split(" ")

        for (int e = 0; e < f.length; e++) {

            if (e == 0) {
                firstString = f[0].toLowerCase()
                sbl.append(firstString)

            }

            if (e > 0) {
                nextString = f[e].toLowerCase()
                firstChar = nextString.charAt(0)
                nextString = nextString.replaceFirst(firstChar.toString(), firstChar.toString().toUpperCase())
                sbl.append(nextString)
            }
            finalString = sbl.toString()

        }
        return finalString
    }

    public void validateJsonResult(DataTable arg) {
        Map<String, String> entries = arg.asMap(String.class, String.class);
        String[] tableKey = entries.keySet();
        JSONObject json = new JSONObject(jsonAsString);

        Iterator<String> jasonKey = json.keys()

        while (jasonKey.hasNext()) {
            String Keys = jasonKey.next()
            if (Keys == "status") {
                assert entries.get("HTTP Status") == resp.getStatusCode().toString();
                break;
            }
            println "--------->" + Keys

            String jsonValue = json.get(Keys)

            if(Keys != "account") {

                   assert entries.containsValue(jsonValue)
            }

            println "===========>" + jsonValue

           if(Keys == "account") {
               JSONObject innerJson = new JSONObject(jsonValue);
               Iterator<String> innerJasonKey = innerJson.keys()

               while (innerJasonKey.hasNext()) {
                   String keys2 = innerJasonKey.next()
                   println "***********" + keys2
                   //json.getJSONObject()
                   String innerjsonValue = innerJson.get(keys2).toString()
                   println ">>>>>>>>>>>>>>>" + innerjsonValue
                   for (String s : tableKey) {
                       println "" + entries.get(s)
                       assert entries.containsValue(innerjsonValue)

                   }

               }
           }

        }
    }

    public void validateResult(DataTable arg) {
        Map<String, String> entries = arg.asMap(String.class, String.class);
        String[] tableKey = entries.keySet();

        for (String key : tableKey) {
            switch (key) {
                case "HTTP Status":
                    assert entries.get(key) == resp.getStatusCode().toString();
                    break;
                default:
                    String jsonPath = FeatureKeyMapper.buildJsonPath(key)

                    assert entries.get(key) == read(jsonAsString, jsonPath).toString();
            }
        }
    }

    @Given("^the test data for account (.+)\$")
    public void the_test_data_for_account_number(String fileName) {
        testDataLoader.loadTestData(fileName)
    }

    @Given("^a Service is consuming Financial Status API\$")
    public void a_Service_is_consuming_Financial_Status_API() {

    }

    @Given("^A Service is consuming the FSPS Calculator API\$")
    public void a_Service_is_consuming_the_FSPS_Calculator_API() {

    }



    @When("^the Financial Status API is invoked with the following:\$")
    public void the_Financial_Status_API_is_invoked_with_the_following(DataTable arg1) {
        getTableData(arg1)
        resp = get("http://localhost:8080/pttg/financialstatusservice/v1/accounts/{sortCode}/{accountNumber}/dailybalancestatus?fromDate={fromDate}&toDate={toDate}&minimum={minimum}",sortCode, accountNumber, fromDate, toDate, minimum)
        jsonAsString = resp.asString()

        println ("Family Case Worker API: "+ jsonAsString)
    }

    @When("^the FSPS Calculator API is invoked with the following\$")
    public void the_FSPS_Calculator_API_is_invoked_with_the_following(DataTable arg1) {
        getTableData(arg1)
        resp = get("http://localhost:8080/pttg/financialstatusservice/v1/maintenance/threshold?innerLondon={innerLondon}&courseLength={courseLength}&tuitionFees={tuitionFees}&tuitionFeesPaid={tuitionFeesPaid}&accommodationFeesPaid={accommodationFeesPaid}",innerLondon, courseLength, tuitionFees, tuitionFeesPaid, accommodationFeesPaid)
        jsonAsString = resp.asString()

        println ("FSPS API Calculator: "+ jsonAsString)
    }



    @Then("^The Financial Status API provides the following results:\$")
    public void the_Financial_Status_API_provides_the_following_results(DataTable arg1) {
              validateJsonResult(arg1)
    }

    @Then("^FSPS Tier four general Case Worker tool API provides the following result\$")
    public void fsps_Tier_four_general_Case_Worker_tool_API_provides_the_following_result(DataTable arg1) {
        validateResult(arg1)
    }

    @Then("^the service displays the following result\$")
    public void the_service_displays_the_following_result(DataTable arg1) {
        validateJsonResult(arg1)
    }


}
