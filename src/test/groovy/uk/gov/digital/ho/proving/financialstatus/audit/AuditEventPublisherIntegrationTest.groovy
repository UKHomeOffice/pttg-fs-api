package uk.gov.digital.ho.proving.financialstatus.audit

import com.mongodb.MongoClient
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.bson.Document
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification
import uk.gov.digital.ho.proving.financialstatus.api.ServiceRunner
import uk.gov.digital.ho.proving.financialstatus.api.configuration.ServiceConfiguration
import uk.gov.digital.ho.proving.financialstatus.audit.configuration.AuditConfiguration

import java.time.LocalDate
import java.time.ZoneOffset

@SpringBootTest(classes = [
    ServiceRunner.class,
    ServiceConfiguration.class,
    AuditConfiguration.class,
    EmbeddedMongoClientConfiguration.class
])
class AuditEventPublisherIntegrationTest extends Specification {

    @Autowired
    MongoClient mongoClient

    @Autowired
    AuditEventPublisher underTest

    @Value('${auditing.mongodb.databaseName}') String databaseName
    @Value('${auditing.mongodb.collectionName}') String collectionName

    def 'published events are written to Mongo'() {
        given:
        def auditApplicationEvent = createAuditApplicationEvent()

        when:
        underTest.publishEvent(auditApplicationEvent)

        then:
        println()
        MongoDatabase database = mongoClient.getDatabase(databaseName)
        MongoCollection<Document> collection = database.getCollection(collectionName)
        collection.count() == 1L
        Document document = collection.find().first()
        document.getString("timestamp") == expectedTimestampString
        document.getString("principal") == expectedPrincipal
        document.getString("type") == expectedType
        Map<String, String> actualData = document.get("data", Map.class)
        actualData == expectedData
    }

    private static final expectedType = "some expectedType"
    private static final expectedPrincipal = "user"
    private static final expectedData = ["key":"value"]
    private static final expectedTimestamp = Date.from(LocalDate.of(2017, 2, 1).atStartOfDay().toInstant(ZoneOffset.UTC))
    private static final expectedTimestampString = "2017-02-01T00:00:00+0000"

    private static createAuditApplicationEvent() {
        new AuditApplicationEvent(expectedTimestamp, expectedPrincipal, expectedType, expectedData)
    }
}
