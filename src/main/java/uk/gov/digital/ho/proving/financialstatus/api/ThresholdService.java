package uk.gov.digital.ho.proving.financialstatus.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.digital.ho.proving.financialstatus.api.response.ResponseStatus;
import uk.gov.digital.ho.proving.financialstatus.api.response.ThresholdResponse;

import java.math.BigDecimal;
import java.math.MathContext;

@RestController
@ControllerAdvice
class ThresholdService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThresholdService.class);

    @RequestMapping(value = "/incomeproving/v1/individual/threshold", method = RequestMethod.GET)
    public ResponseEntity<ThresholdResponse> calculateThreshold() {
        LOGGER.info("Calculating threshold");

        ThresholdResponse thresholdResponse = new ThresholdResponse(new BigDecimal(123.45, MathContext.DECIMAL64));
        thresholdResponse.setStatus(new ResponseStatus("200", "OK"));

        return new ResponseEntity<>(thresholdResponse, HttpStatus.OK);
    }

}
