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

class FinancialStatusApiSteps {

    @Managed
    public WebDriver driver;

    def String toCamelCase(String s) {
        String allUpper = StringUtils.remove(WordUtils.capitalizeFully(s), " ")
        String camelCase = allUpper[0].toLowerCase() + allUpper.substring(1)
        camelCase
    }

    def parseDate(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy")
        Date date = sdf.parse(dateString)
        date
    }

    def sendKeys(WebElement element, String v) {
        element.clear();
        if (v != null && v.length() != 0) {
            element.sendKeys(v);
        }
    }

    private void submitFormWithData(DataTable arg1) {
        // driver.sleep(delay)
        Map<String, String> entries = arg1.asMap(String.class, String.class)
        submitForm(entries, driver);
        driver.findElement(By.className("button")).click();
    }

    public void submitForm(Map<String, String> entries, WebDriver driver) {
        entries.each { k, v ->
            String key = toCamelCase(k)

            if (key.endsWith("Date")) {
                if (v != null && v.length() != 0) {
                    String day = v.substring(0, v.indexOf("/"))
                    String month = v.substring(v.indexOf("/") + 1, v.lastIndexOf("/"))
                    String year = v.substring(v.lastIndexOf("/") + 1)

                    sendKeys(driver.findElement(By.id(key + "Day")), day)
                    sendKeys(driver.findElement(By.id(key + "Month")), month)
                    sendKeys(driver.findElement(By.id(key + "Year")), year)
                } else {
                    driver.findElement(By.id(key + "Day")).clear()
                    driver.findElement(By.id(key + "Month")).clear()
                    driver.findElement(By.id(key + "Year")).clear()
                }
            } else {
                sendKeys(driver.findElement(By.id(key)), v)
            }
        }
    }

    public void checkOutput(Map<String, String> entries, WebDriver driver) {
        entries.each { k, v ->
            String key = toCamelCase(k)

            if (key != "outcome") {
                assert v.equals(driver.findElement(By.id(key)).getText())
            } else {
                WebElement element = driver.findElement(By.id(key))
                String cssValue = element.getAttribute("class")
                assert cssValue.contains("panel-fail") == false
            }
        }
    }

}
