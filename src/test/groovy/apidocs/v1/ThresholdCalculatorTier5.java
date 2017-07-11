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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.restassured.RestDocumentationFilter;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.digital.ho.proving.financialstatus.api.ServiceRunner;
import uk.gov.digital.ho.proving.financialstatus.api.configuration.ServiceConfiguration;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.core.Is.is;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
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
public class ThresholdCalculatorTier5 {

    public static final String BASEPATH = "/pttg/financialstatus/v1/t5";

    @Rule
    public JUnitRestDocumentation restDocumentationRule = new JUnitRestDocumentation("build/generated-snippets");

    @Value("${local.server.port}")
    private int port;

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

    private FieldDescriptor[] statusModelFields = new FieldDescriptor[]{
        fieldWithPath("status").description("The result status"),
        fieldWithPath("status.code").description("A numeric code identifying the error condition - see <<Errors>>"),
        fieldWithPath("status.message").description("Details to further explain the error condition")
    };

    private FieldDescriptor[] bodyModelFields = new FieldDescriptor[]{
        fieldWithPath("threshold").description("minimum daily balance")
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
            .param("applicantType", "main")
            .param("dependants", "0")
            .filter(document.snippets(
                requestHeaders(
                    headerWithName("Accept").description("The requested media type eg application/json. See <<Schema>> for supported media types.")
                ),
                responseHeaders(
                    headerWithName("Content-Type").description("The Content-Type of the payload, e.g. `application/json`")
                )
            ))

            .when().get("/maintenance/threshold")
            .then().assertThat().statusCode(is(200));
    }

    @Test
    public void thresholdCalculationTier5() throws Exception {

        given(documentationSpec)
            .spec(requestSpec)
            .param("applicantType", "main")
            .param("dependants", "1")
             .filter(document.snippets(
                responseFields(bodyModelFields)
                    .and(statusModelFields),
                requestParameters(
                    parameterWithName("applicantType")
                        .description("Type of applicant. Allowed values are 'main', 'dependant'. See <<Glossary>>")
                        .attributes(key("optional").value(false)),
                    parameterWithName("dependants")
                        .description("The number of dependants to take in to account when calculating the threshold value")
                        .attributes(key("optional").value(true))
                )

            ))

            .when().get("/maintenance/threshold")
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

            .when().get("/maintenance/threshold")
            .then().assertThat().statusCode(is(400));
    }

}
