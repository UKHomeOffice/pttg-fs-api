package uk.gov.digital.ho.proving.financialstatus.audit

import com.github.fakemongo.Fongo
import com.mongodb.MongoClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class EmbeddedMongoClientConfiguration {

    @Bean(destroyMethod = "close")
    MongoClient mongoClient() {
        new Fongo("integration-test").getMongo()
    }
}
