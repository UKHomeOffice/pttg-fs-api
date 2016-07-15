package apidocs.v1;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.restassured.RestDocumentationFilter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.digital.ho.proving.financialstatus.api.ServiceRunner;
import uk.gov.digital.ho.proving.financialstatus.api.configuration.ServiceConfiguration;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.core.Is.is;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.restassured.operation.preprocess.RestAssuredPreprocessors.modifyUris;
import static org.springframework.restdocs.snippet.Attributes.key;

@SpringApplicationConfiguration(classes = ServiceRunner.class)
@WebIntegrationTest("server.port=8080")
@ContextConfiguration(classes = ServiceConfiguration.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class FinancialStatus {

    public static final String BASEPATH = "/pttg/financialstatusservice/v1/";

    @Rule
    public JUnitRestDocumentation restDocumentationRule = new JUnitRestDocumentation("build/generated-snippets");

    @Value("${server.port}")
    private int port ;

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
        fieldWithPath("account.sortCode").description("The accounts's sort code"),
        fieldWithPath("account.accountNumber").description("The account number"),
    };

    private FieldDescriptor[] statusModelFields = new FieldDescriptor[]{
        fieldWithPath("status").description("to do - i don't know what this means"),
        fieldWithPath("status.code").description("to do - i don't know what this means"),
        fieldWithPath("status.message").description("to do - i don't know what this means")
    };

    private FieldDescriptor[] bodyModelFields = new FieldDescriptor[]{
        fieldWithPath("fromDate").description("start date for the financial check"),
        fieldWithPath("toDate").description("end date of the financial check"),
        fieldWithPath("minimum").description("minimum allowed daily balance"),
        fieldWithPath("pass").description("status of minimum balance check")
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
    }

    @Test
    public void commonHeaders() throws Exception {

        given(documentationSpec)
            .spec(requestSpec)
            .param("fromDate", "2015-05-05")
            .param("toDate", "2015-06-01")
            .param("minimum", 1000)
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

        given(documentationSpec)
            .spec(requestSpec)
            .param("fromDate", "2016-05-05")
            .param("toDate", "2016-06-01")
            .param("minimum", 1000)
            .filter(document.snippets(
                responseFields(bodyModelFields)
                    .and(accountModelFields)
                    .and(statusModelFields),
                requestParameters(
                    parameterWithName("fromDate")
                        .description("the start date of the financial check `yyyy-mm-dd` eg `2015-09-23`")
                        .attributes(key("optional").value(false)),
                    parameterWithName("toDate")
                        .description("the end date of the financial check `yyyy-mm-dd` eg `2015-09-23`")
                        .attributes(key("optional").value(false)),
                    parameterWithName("minimum")
                        .description("the minimum value allowed for the daily closing balance")
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
