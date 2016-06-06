package uk.gov.digital.ho.proving.financialstatus.api.response;

import uk.gov.digital.ho.proving.financialstatus.api.response.BaseResponse;

import java.math.BigDecimal;

public class ThresholdResponse extends BaseResponse {
    private BigDecimal threshold;

    public ThresholdResponse() {
    }

    public ThresholdResponse(BigDecimal threshold) {
        this.threshold = threshold;
    }

    public BigDecimal getThreshold() {
        return threshold;
    }

    public void setThreshold(BigDecimal threshold) {
        this.threshold = threshold;
    }
}
