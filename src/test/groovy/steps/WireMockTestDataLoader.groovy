package steps

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.google.common.net.HostAndPort
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static com.github.tomakehurst.wiremock.client.WireMock.*

class WireMockTestDataLoader {


    private static Logger LOGGER = LoggerFactory.getLogger(WireMockTestDataLoader.class);

    def dataDirName = "account-data"

    def WireMockServer wireMockServer

    WireMockTestDataLoader(String service) {

        HostAndPort hostAndPort = HostAndPort.fromString(service)
        wireMockServer = new WireMockServer(hostAndPort.getPort())
        wireMockServer.start()
        WireMock.configureFor(hostAndPort.getHostText(), hostAndPort.getPort())
    }

    def stubTestData(String fileName, String url) {

        def json = jsonFromFile(fileName)

        if (json == null) {
            assert false: "No test data file was loaded for $fileName from the resources/account-data directory - " +
                "Please add it or check filename is correct"
        }

        addStub(fileName, json, url)
    }

    private def jsonFromFile(String fileName) {

        println ''
        def fileLocation = "/$dataDirName/$fileName" + ".json"
        LOGGER.debug("Loading test data for {}", fileLocation.toString())

        def file =  this.getClass().getResource( fileLocation)

        if (file == null) {
            return null
        }

        return file.text
    }

    private def addStub(String fileName, String json, String url) {

        println ''
        LOGGER.debug("Stubbing Response data with $fileName")

        stubFor(get(urlPathMatching(url))
            .willReturn(aResponse()
            .withBody(json)
            .withHeader("Content-Type", "application/json")
            .withStatus(200)));

        println ''
        LOGGER.debug("Completed Stubbing Response data with $fileName")
    }

    def clearTestData() {
        wireMockServer.stop()
    }

}
