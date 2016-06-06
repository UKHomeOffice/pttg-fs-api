//package uk.gov.digital.ho.proving.financialstatus.api;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//class PingService {
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(PingService.class);
//
//    @RequestMapping(value = "/incomeproving/v1/individual/financialstatus/ping", method = RequestMethod.GET)
//    public ResponseEntity<String> ping() {
//        LOGGER.info("Ping received");
//        return new ResponseEntity<String>("Pong", HttpStatus.OK);
//    }
//
//}
