Feature: Total Funds Required Calculation - Continuation Tier 4 (General) Student Non Doctorate with dependents(single current account) In London

    Entire course - initial course length + continuation course length
    Applicants Required Maintenance period - Continuation course length (capped to 9 months)
    Dependants Required Maintenance period - Continuation course length +2 (when entire course <12 months) or +4 months (when entire course 12+) (capped to 9 months) ##
    Course length - this can be 1-9 months
    Total tuition fees - total amount of the tuition fees for the course
    Tuition fees already paid - total amount of tuition fees already paid
    Accommodation fees already paid - The maximum amount paid can be £1265
    Continuation course length calculation - Course end date + 1 day to Continuation end date (inclusive)

    Background:
        Given A Service is consuming the FSPS Calculator API
        And the default details are
            | Student Type                    | nondoctorate |
            | In London                       | Yes          |
            | Total tuition fees              | 2000.50      |
            | Tuition fees already paid       | 200          |
            | Accommodation fees already paid | 100          |

 #   Required Maintenance threshold calculation to pass this feature file

#    Maintenance threshold amount = (Required Maintenance threshold non doctorate In London * Course length) + ((Dependants Required Maintenance threshold In London * Dependants Required Maintenance period)  * number of dependants) + (total tuition fees - tuition fees paid - accommodation fees paid)

#   Requirement to meet Tier 4 pass (Continuation application only)
#
#   Applicants Required Maintenance threshold non doctorate:  In London - £1265, Out London - £1015
#   Dependants Required Maintenance threshold: In London - £845, Out London - £680
#
#   Entire course <12 months ((£1265 x 4) + (845 x (4+2) x 1) + (£10,000 - 0 - 0)) ##
#    Entire course 12+ months ((£1265 x 4) + (845 x (4+4) x 1) + (£10,000 - 0 - 0)) ##

#    Entire course <12 months
#   Tier 4 (General) Student - non doctorate - In London, with dependents In Country - (£1265 x 3) + (£845 x (3+2) x 1) + (£10,000 - £0 - £0) = £18,020
#   Tier 4 (General) Student - non doctorate - In London, with dependents In Country - (£1265 x 8) + (£845 x (8+2) x 2) + (£7,000 - £300 - £500.50) = £31,529.50 (dependant require maintenance period capped at 9 months)
#
#    Entire course 12+ months
#    Tier 4 (General) Student - non doctorate - In London, with dependents In Country - (£1265 x 3) + (£845 x (3+4) x 1) + (£10,000 - £0 - £0) = £19,710.00
#    Tier 4 (General) Student - non doctorate - In London, with dependents In Country - (£1265 x 8) + (£845 x (8+4) x 2) + (£7,000 - £300 - £500.50) = £31,529.50 (dependant require maintenance period capped at 9 months)

    ##################### Entire course length <12 months In London #######################

    Scenario: John's maintenance threshold amount calculated

        #Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Course start date     | 2016-03-01|
            | Course end date       | 2016-04-01|
            | Continuation end date | 2016-06-05|
            | dependants  | 1          |
        Then The Financial Status API provides the following results:
            | HTTP Status | 200     |
            | Threshold   | 7610.50 |

    Scenario: Lizzie's maintenance threshold amount calculated

        #Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Course start date     | 2016-01-01|
            | Course end date       | 2016-10-05|
            | Continuation end date | 2016-12-01|
            | dependants  | 2          |
        Then The Financial Status API provides the following results:
            | HTTP Status   | 200               |
            | Threshold     | 28295.50          |
            | Course length | 9 |


    ##################### Entire course length 12+ months In London #######################

    Scenario: Rob's maintenance threshold amount calculated

        #Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Course start date     | 2015-01-01|
            | Course end date       | 2016-02-01|
            | Continuation end date | 2016-04-02|
            | dependants  | 2          |
        Then The Financial Status API provides the following results:
            | HTTP Status | 200      |
            | Threshold   | 17325.50 |

    Scenario: Raul's maintenance threshold amount calculated

        #Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Course start date     | 2015-01-05|
            | Course end date       | 2015-12-05|
            | Continuation end date | 2016-04-06|
            | dependants  | 3          |
        Then The Financial Status API provides the following results:
            | HTTP Status | 200      |
            | Threshold   | 30840.50 |

    Scenario: Karen's maintenance threshold amount calculated

        #Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Course start date     | 2015-06-01|
            | Course end date       | 2015-12-05|
            | Continuation end date | 2016-10-00|
            | dependants  | 2          |
        Then The Financial Status API provides the following results:
            | HTTP Status                | 200      |
            | Threshold                  | 20690.50 |
            | Continuation course length | 9        |

