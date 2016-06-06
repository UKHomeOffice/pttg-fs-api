package steps

import cucumber.api.DataTable
import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import cucumber.api.java.en.When
import net.thucydides.core.annotations.Managed
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.text.WordUtils
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement

import java.text.SimpleDateFormat

import com.jayway.restassured.response.Response

class FinancialStatusApiSteps {

    public Response resp
    String jsonAsString

    def String toCamelCase(String s) {
        String allUpper = StringUtils.remove(WordUtils.capitalizeFully(s), " ")
        String camelCase = allUpper[0].toLowerCase() + allUpper.substring(1)
        camelCase
    }

    def String getTableData(DataTable arg) {
        //TODO refactor to reject\identify unrecognised keys

        Map<String, String> entries = arg.asMap(String.class, String.class)
        String[] tableKey = entries.keySet()

        for (String s : tableKey) {

            if (s.equalsIgnoreCase("application raised date")) {
                applicationRaisedDate = entries.get(s)
            }
            if (s.equalsIgnoreCase("nino")) {
                nino = entries.get(s)
            }
            if (s.equalsIgnoreCase("dependants")) {
                dependants = entries.get(s)
            }
            if (s.equalsIgnoreCase("From Date")) {
                fromDate = entries.get(s)

            }
            if(s.equalsIgnoreCase("To Date")){
                toDate = entries.get(s)
            }
        }
    }

    //function to loop through three column table
    def checkIncome(DataTable table){

        List<List<String>> rawData = table.raw()
        def incomes = read(jsonAsString, "incomes")
        assert(incomes.size() >= rawData.size() -1)

        String total = read(jsonAsString, "total")

        int index =0

        for (List<String> row : rawData) {

            if (!row.get(0).startsWith("Total")) {
                assert (row.get(0).equals(incomes.get(index).get("payDate")))
                assert (row.get(1).equals(incomes.get(index).get("employer")))
                assert (row.get(2).equals(incomes.get(index).get("income")))
            } else {
                assert (row.get(2).equals(total))
            }
            index++
        }
    }

    /**
     prerequisites:
     - BDD key can be transformed to valid jsonpath OR key name has been added to FeatureKeyMapper.java
     - Date values are in the format yyyy-mm-dd
     - boolean values are lowercase
     */
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


}
