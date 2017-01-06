package uk.gov.digital.ho.proving.financialstatus.authentication

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.http.{HttpEntity, HttpHeaders, HttpMethod}
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import uk.gov.digital.ho.proving.financialstatus.domain.UserProfile

@Service()
class Authentication @Autowired()(val objectMapper: ObjectMapper,
                                  rest: RestTemplate,
                                  @Value("${keycloak.account.url}") keycloakAccountUrl: String) {

  private val LOGGER = LoggerFactory.getLogger(classOf[Authentication])

  def getUserProfileFromToken(accessToken: String): Option[UserProfile] = {

    val headers = new HttpHeaders()
    val emptyBody = ""

    headers.add("Accept", "application/json")
    headers.add("Authorization", s"Bearer $accessToken")

    val response = try {
      val requestEntity = new HttpEntity[String](emptyBody, headers)
      val response = rest.exchange(this.keycloakAccountUrl, HttpMethod.GET, requestEntity, classOf[String])
      Some(objectMapper.readValue(response.getBody, classOf[UserProfile]))
    } catch {
      case e: Exception =>
        LOGGER.info(s"Failed to retrieve user details (${e.getMessage}) for token: $accessToken ")
        None
    }
    response
  }

}
