package steps

import com.jayway.restassured.response.Response
import cucumber.api.DataTable
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.text.WordUtils

class FinancialStatusApiSteps {

    public Response resp
    String jsonAsString

    def String toCamelCase(String s) {
        String allUpper = StringUtils.remove(WordUtils.capitalizeFully(s), " ")
        String camelCase = allUpper[0].toLowerCase() + allUpper.substring(1)
        camelCase
    }

    def String getTableData(DataTable arg) {
       resp = get("http://localhost:8080/incomeproving/v1/individual/financialstatus/ping")
       println ""+resp.toString()

    }

    //function to loop through three column table
    def checkIncome(DataTable table){


    }


    public void validateJsonResult(DataTable arg) {

    }


}
