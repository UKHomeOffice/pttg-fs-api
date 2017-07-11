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
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.restdocs.restassured.RestDocumentationFilter;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
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
public class ConditionCodesTier4 {

    public static final String BASEPATH = "/pttg/financialstatus/v1/t4";

    @Rule
    public JUnitRestDocumentation restDocumentationRule = new JUnitRestDocumentation("build/generated-snippets/condition-codes");

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

    private final FieldDescriptor[] statusModelFields = new FieldDescriptor[]{
        fieldWithPath("status").description("The result status"),
        fieldWithPath("status.code").description("A numeric code identifying the error condition - see <<Errors>>"),
        fieldWithPath("status.message").description("Details to further explain the error condition")
    };

    private final FieldDescriptor[] bodyModelFields = new FieldDescriptor[]{
        fieldWithPath("applicantConditionCode")
            .description("The condition code for the applicant. Only returned if this is not a dependants only application.")
            .attributes(key("optional").value(true)),
        fieldWithPath("partnerConditionCode")
            .description("The condition code for any partner. Only returned if number of dependants is greater than zero.")
            .attributes(key("optional").value(true)),
        fieldWithPath("childConditionCode")
            .description("The condition code for any children. Only returned if number of dependants is greater than zero.")
            .attributes(key("optional").value(true))
    };

    private final ParameterDescriptor studentTypeField = parameterWithName("studentType")
        .description("Type of student. For allowed values see <<Glossary>>")
        .attributes(key("optional").value(false));
    private final ParameterDescriptor dependantsField = parameterWithName("dependants")
        .description("The number of dependants as an integer")
        .attributes(key("optional").value(false));

    private final ParameterDescriptor dependantsOnlyField = parameterWithName("dependantsOnly")
        .description("Whether this is a dependants only application or not. Allowed values are true or false.")
        .attributes(key("optional").value(false));

    private final ParameterDescriptor recognisedBodyOrHEIField = parameterWithName("recognisedBodyOrHEI")
        .description("Whether this is an application involving a course with a recognised body / Higher Education Institute or not. Allowed values are true or false.")
        .attributes(key("optional").value(true));

    private final ParameterDescriptor courseTypeField = parameterWithName("courseType")
        .description("The type of course. For allowed values see <<Glossary>>")
        .attributes(key("optional").value(true));

    private final ParameterDescriptor courseStartDateField = parameterWithName("courseStartDate")
        .description("The start date for the course.")
        .attributes(key("optional").value(true));

    private final ParameterDescriptor courseEndDateField = parameterWithName("courseEndDate")
        .description("The end date for the course.")
        .attributes(key("optional").value(true));

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
    public void tier4NonGeneralStudentType() throws Exception {

        given(documentationSpec)
            .spec(requestSpec)
            .param("studentType", "des")
            .param("dependants", "1")
            .param("dependantsOnly", false)
            .filter(document.snippets(
                requestHeaders(
                    headerWithName("Accept").description("The requested media type eg application/json. See <<Schema>> for supported media types.")
                ),
                responseHeaders(
                    headerWithName("Content-Type").description("The Content-Type of the payload, e.g. `application/json`")
                ),
                requestParameters(studentTypeField, dependantsField, dependantsOnlyField),
                responseFields(bodyModelFields).and(statusModelFields)
            ))

            .when().get("/conditioncodes")
            .then().assertThat().statusCode(is(200));
    }

    @Test
    public void tier4GeneralStudentType() throws Exception {

        given(documentationSpec)
            .spec(requestSpec)
            .param("studentType", "general")
            .param("dependants", "1")
            .param("dependantsOnly", false)
            .param("recognisedBodyOrHEI", false)
            .param("courseType", "main")
            .param("courseStartDate", "2016-01-03")
            .param("courseEndDate", "2016-07-03")
            .filter(document.snippets(
                requestHeaders(
                    headerWithName("Accept").description("The requested media type eg application/json. See <<Schema>> for supported media types.")
                ),
                responseHeaders(
                    headerWithName("Content-Type").description("The Content-Type of the payload, e.g. `application/json`")
                ),
                requestParameters(studentTypeField, dependantsField, dependantsOnlyField, recognisedBodyOrHEIField, courseTypeField, courseStartDateField, courseEndDateField),
                responseFields(bodyModelFields).and(statusModelFields)
            ))

            .when().get("/conditioncodes")
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

            .when().get("/conditioncodes")
            .then().assertThat().statusCode(is(400));
    }
}
