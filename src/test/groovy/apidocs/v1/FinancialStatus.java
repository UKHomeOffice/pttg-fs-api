package apidocs.v1;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.restassured.RestDocumentationFilter;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import steps.WireMockTestDataLoader;
import uk.gov.digital.ho.proving.financialstatus.api.ServiceRunner;
import uk.gov.digital.ho.proving.financialstatus.api.configuration.ServiceConfiguration;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.core.Is.is;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.removeHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.restassured.operation.preprocess.RestAssuredPreprocessors.modifyUris;
import static org.springframework.restdocs.snippet.Attributes.key;

@SpringBootTest(
    webEnvironment = RANDOM_PORT,
    classes = {ServiceRunner.class, ServiceConfiguration.class}
)
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(properties = {
    "barclays.service.url=http://localhost:8089"
})
public class FinancialStatus {

    public static final String BASEPATH = "/pttg/financialstatus/v1/";

    @Rule
    public JUnitRestDocumentation restDocumentationRule = new JUnitRestDocumentation("build/generated-snippets");

    @Value("${local.server.port}")
    private int port;

    private WireMockTestDataLoader testDataLoader;
    private int stubPort = 8089;

    private RequestSpecification documentationSpec;

    private RequestSpecification requestSpec;

    private RestDocumentationFilter document =
        document("{method-name}",
            preprocessRequest(
                prettyPrint(),
                modifyUris()
                    .scheme("https")
                    .host("api.host.address")
                    .removePort()
            ),
            preprocessResponse(
                prettyPrint(),
                removeHeaders("Date", "Connection", "Transfer-Encoding")
            )
        );

    private FieldDescriptor[] accountModelFields = new FieldDescriptor[]{
        fieldWithPath("account").description("The account corresponding to this request"),
        fieldWithPath("account.sortCode").description("The account's sort code"),
        fieldWithPath("account.accountNumber").description("The account number"),
    };

    private FieldDescriptor[] statusModelFields = new FieldDescriptor[]{
        fieldWithPath("status").description("The result status"),
        fieldWithPath("status.code").description("A numeric code identifying the error condition - see <<Errors>>"),
        fieldWithPath("status.message").description("Details to further explain the error condition")
    };

    private FieldDescriptor[] bodyModelFields = new FieldDescriptor[]{
        fieldWithPath("fromDate").description("Start date for the financial check"),
        fieldWithPath("toDate").description("End date of the financial check"),
        fieldWithPath("minimum").description("Minimum allowed daily balance"),
        fieldWithPath("pass").description("Status of minimum balance check"),
        fieldWithPath("accountHolderName").description("The name associated with the account")

    };

    @Before
    public void setUp() {

        RestAssured.port = this.port;
        RestAssured.basePath = BASEPATH;

        requestSpec = new RequestSpecBuilder()
            .setAccept(ContentType.JSON)
            .build();

        this.documentationSpec =
            new RequestSpecBuilder()
                .addFilter(documentationConfiguration(this.restDocumentationRule))
                .addFilter(document)
                .build();

        testDataLoader = new WireMockTestDataLoader(stubPort);
    }

    @After
    public void tearDown() {
        testDataLoader.stop();
    }

    @Test
    public void commonHeaders() throws Exception {

        testDataLoader.stubTestData("01010312", "/financialstatus/v1");

        given(documentationSpec)
            .spec(requestSpec)
            .param("fromDate", "2016-05-05")
            .param("toDate", "2016-06-01")
            .param("minimum", 1000)
            .param("dob", "2000-01-01")
            .param("userId", "userid123456")
            .filter(document.snippets(
                requestHeaders(
                    headerWithName("Accept").description("The requested media type eg application/json. See <<Schema>> for supported media types.")
                ),
                responseHeaders(
                    headerWithName("Content-Type").description("The Content-Type of the payload, e.g. `application/json`")
                )
            ))

            .when().get("/accounts/{sortCode}/{accountNumber}/dailybalancestatus", "123456", "01010312")
            .then().assertThat().statusCode(is(200));
    }

    @Test
    public void financialStatus() throws Exception {

        testDataLoader.stubTestData("01010312", "/financialstatus/v1");

        given(documentationSpec)
            .spec(requestSpec)
            .param("fromDate", "2016-05-05")
            .param("toDate", "2016-06-01")
            .param("minimum", 1000)
            .param("dob", "2000-01-01")
            .param("userId", "userid123456")
            .filter(document.snippets(
                responseFields(bodyModelFields)
                    .and(accountModelFields)
                    .and(statusModelFields),
                requestParameters(
                    parameterWithName("fromDate")
                        .description("The start date of the financial check - `yyyy-mm-dd` eg `2015-09-23`")
                        .attributes(key("optional").value(false)),
                    parameterWithName("toDate")
                        .description("The end date of the financial check - `yyyy-mm-dd` eg `2015-09-23`")
                        .attributes(key("optional").value(false)),
                    parameterWithName("minimum")
                        .description("The minimum value allowed for the daily closing balance")
                        .attributes(key("optional").value(false)),
                    parameterWithName("dob")
                        .description("The account holder's date of birth - `yyyy-mm-dd` eg `2015-09-23`")
                        .attributes(key("optional").value(false)),
                    parameterWithName("userId")
                        .description("A user ID for the requester - any string")
                        .attributes(key("optional").value(false))
                ),
                pathParameters(
                    parameterWithName("sortCode")
                        .description("The bank account sort code"),
                    parameterWithName("accountNumber")
                        .description("The bank account number")
                )
            ))

            .when().get("/accounts/{sortCode}/{accountNumber}/dailybalancestatus", "123456", "01010312")
            .then().assertThat().statusCode(is(200));
    }

    @Test
    public void financialStatusFail() throws Exception {

        testDataLoader.stubTestData("01010312", "/financialstatus/v1.*");

        given(documentationSpec)
            .spec(requestSpec)
            .param("fromDate", "2016-05-05")
            .param("toDate", "2016-06-01")
            .param("minimum", 100000)
            .param("dob", "2000-01-01")
            .param("userId", "userid123456")
            .filter(document.snippets(
                responseFields(
                    fieldWithPath("failureReason").description("Contains further details of the failure reason"),
                    fieldWithPath("failureReason.lowestBalanceDate").description("Indicates that the failure was due to the balance falling below the required minimum. The value shows the date on which the lowest balance occurred."),
                    fieldWithPath("failureReason.lowestBalanceValue").description("The lowest balance that occurred during the date range."))
                    .and(bodyModelFields)
                    .and(accountModelFields)
                    .and(statusModelFields),
                requestParameters(
                    parameterWithName("fromDate")
                        .description("The start date of the financial check - `yyyy-mm-dd` eg `2015-09-23`")
                        .attributes(key("optional").value(false)),
                    parameterWithName("toDate")
                        .description("The end date of the financial check - `yyyy-mm-dd` eg `2015-09-23`")
                        .attributes(key("optional").value(false)),
                    parameterWithName("minimum")
                        .description("The minimum value allowed for the daily closing balance")
                        .attributes(key("optional").value(false)),
                    parameterWithName("dob")
                        .description("The account holder's date of birth - `yyyy-mm-dd` eg `2015-09-23`")
                        .attributes(key("optional").value(false)),
                    parameterWithName("userId")
                        .description("A user ID for the requester - any string. This is for audit trail purposes only.")
                        .attributes(key("optional").value(false))
                ),
                pathParameters(
                    parameterWithName("sortCode")
                        .description("The bank account sort code"),
                    parameterWithName("accountNumber")
                        .description("The bank account number")
                )
            ))

            .when().get("/accounts/{sortCode}/{accountNumber}/dailybalancestatus", "123456", "01010312")
            .then().assertThat().statusCode(is(200));
    }

    @Test
    public void financialStatusNoAccount() throws Exception {

        // Don't stub the data, thus causing a 404
        given(documentationSpec)
            .spec(requestSpec)
            .param("fromDate", "2016-05-05")
            .param("toDate", "2016-06-01")
            .param("minimum", 1000)
            .param("dob", "2000-01-01")
            .param("userId", "userid123456")
            .filter(document.snippets(
                responseFields(
                    fieldWithPath("status.code").description("A specific error code to identify further details of this error"),
                    fieldWithPath("status.message").description("A description of the error, in this case identifying the missing mandatory parameter")
                )
            ))

            .when().get("/accounts/{sortCode}/{accountNumber}/dailybalancestatus", "123456", "01010312")
            .then().assertThat().statusCode(is(404));
    }

    @Test
    public void financialStatusNotEnoughData() throws Exception {

        testDataLoader.stubTestData("toofew", "/financialstatus/v1.*");

        given(documentationSpec)
            .spec(requestSpec)
            .param("fromDate", "2016-05-05")
            .param("toDate", "2016-06-01")
            .param("minimum", 1000)
            .param("dob", "2000-01-01")
            .param("userId", "userid123456")
            .filter(document.snippets(
                responseFields(
                    fieldWithPath("failureReason").description("Contains further details of the failure reason"),
                    fieldWithPath("failureReason.recordCount").description("Indicates that the failure was due to the record count. The value shows the number of records available."))
                    .and(bodyModelFields)
                    .and(accountModelFields)
                    .and(statusModelFields)
            ))

            .when().get("/accounts/{sortCode}/{accountNumber}/dailybalancestatus", "123456", "01010312")
            .then().assertThat().statusCode(is(200));
    }

    @Test
    public void missingParameterError() throws Exception {

        given(documentationSpec)
            .spec(requestSpec)
            .filter(document.snippets(
                responseFields(
                    fieldWithPath("status.code").description("A specific error code to identify further details of this error"),
                    fieldWithPath("status.message").description("A description of the error, in this case identifying the missing mandatory parameter")
                )
            ))

            .when().get("/accounts/{sortCode}/{accountNumber}/dailybalancestatus", "123456", "01010312")
            .then().assertThat().statusCode(is(400));
    }

    @Test
    public void missingFromDateError() throws Exception {

        given(documentationSpec)
            .spec(requestSpec)
            .param("toDate", "2016-06-01")
            .param("minimum", 1000)
            .filter(document.snippets(
                responseFields(
                    fieldWithPath("status.code").description("A specific error code to identify further details of this error"),
                    fieldWithPath("status.message").description("A description of the error, in this case identifying the missing mandatory parameter")
                )
            ))

            .when().get("/accounts/{sortCode}/{accountNumber}/dailybalancestatus", "123456", "01010312")
            .then().assertThat().statusCode(is(400));
    }

}
