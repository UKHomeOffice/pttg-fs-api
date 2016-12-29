Feature: Total Funds Required Calculation - Initial Tier 4 (General) Student Union Sabbatical Officer with and without dependants (single current account)

    Main applicants Required Maintenance period: Months between course start date and course end date (rounded up & capped to 2 months)
    Main applicant Required Maintenance period is rounded up to the full month (E.g course length of 1month and 4 days is rounded up to 2 months)

    Dependants Required Maintenance period - Months between main applicants course start date & course end date + wrap up period (rounded up & capped to 2 months)
    Dependants Required Maintenance period is only rounded up to the full month after the wrap up is applied (E.g leave length of 1 month 15 days is wrapped up to 2 months) then rounded up to 2months

    Main applicants leave - Entire course length + wrap up period
    Course length - course start date to course end date (Main Course)
    Wrap up period calculated from original course start date to course end date
    Wrap up period - see table below:

    Main course length 12 month or more = 4 month
    Main course length 6 months or more but less than 12 months = 2 months
    Main course length <6 months = 7 days

    The concept of pre-sessional courses does not apply to the SUSO route##

    Applicants Required Maintenance threshold non doctorate:  In London - £1265, Out London - £1015
    Dependants Required Maintenance threshold: In London - £845, Out London - £680

    Total tuition fees - total amount of the tuition fees for the course
    Tuition fees already paid - total amount of tuition fees already paid
    Accommodation fees already paid - The maximum amount paid can be £1265

    Background:
        Given A Service is consuming the FSPS Calculator API
        And the default details are
            | Student Type                    | sso |
            | In London                       | Yes |
            | Accommodation fees already paid | 100 |

    #Required Maintenance threshold calculation to pass this feature file

    #Maintenance threshold amount = (Required Maintenance threshold non doctorate * Course length) +
    #((Dependants Required Maintenance threshold * Dependants Required Maintenance period)  * number of dependants) + (total tuition fees - tuition fees paid - accommodation fees paid)

    #SUSO course:
    #12 months or more: ((£1265 x 4) + (845 x (4+4) x 1) + (£10,000 - 0 - 0))
    #6 months or more but less than 12 months: ((£1265 x 4) + (845 x (4+2) x 1) + (£10,000 - 0 - 0))
    #< 6 months: ((£1265 x 4) + (845 x (4+7day) x 1) + (£10,000 - 0 - 0))

    #SUSO course worked examples:

    #12 months or more: Tier 4 (General) Student - sso - In London, with dependents In Country - (£1265 x 3) + (£845 x (3+4) x 1) + (£10,000 - £0 - £0) = £19,710
    #6 months or more but less than 12 months: Tier 4 (General) Student - sso - In London, with dependents In Country - (£1265 x 8) + (£845 x (8+2) x 2) + (£7,000 - £300 - £500.50) = £31,529.50 (dependant require maintenance period capped at 9 months)
    #< 6 months: Tier 4 (General) Student - sso - In London, with dependents In Country - (£1265 x 4) + (£845 x (4+7 days) x 1) + (£10,000 - £0 - £0) = (£18440)


    ################# SUSO course #######################

    Scenario: Martin is on an initial 7 SUSO course and does not have dependants. Martin's maintenance threshold amount calculated.

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Dependants        | 0          |
            | Course start date | 2016-01-03 |
            | Course end date   | 2016-07-03 |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 2430.00    |
            | Course length  | 2          |
            | Leave end date | 2016-09-03 |

    Scenario: Stuart is on an initial 13 months SUSO course and does not have dependants. Stuart's maintenance threshold amount calculated.

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Dependants        | 0          |
            | Course start date | 2016-01-03 |
            | Course end date   | 2017-01-03 |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 13085.50   |
            | Course Length  | 2          |
            | Leave end date | 2017-05-03 |

    Scenario: Andy is on an initial 1 month SUSO course and has 2 dependants. Andy's maintenance threshold amount calculated.

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Dependants        | 2          |
            | Course start date | 2016-01-03 |
            | Course end date   | 2016-02-02 |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 5810.00    |
            | Leave end date | 2016-02-09 |

    Scenario: Phil is on an initial 2 months SUSO course and has 2 dependants. Phil's maintenance threshold amount calculated.

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Dependants        | 2          |
            | Course start date | 2016-01-03 |
            | Course end date   | 2016-02-28 |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 5810.00    |
            | Leave end date | 2016-03-06 |

    Scenario: Karen is on an initial 7 months SUSO course and has 1 have dependants. Karen's maintenance threshold amount calculated.

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Dependants        | 1          |
            | Course start date | 2016-01-03 |
            | Course end date   | 2016-07-28 |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 4120.00    |
            | Course Length  | 2          |
            | Leave end date | 2016-09-28 |

    Scenario: Michael is on an initial 33 months SUSO course and has 3 have dependants. Michael's maintenance threshold amount calculated.

        Given A Service is consuming the FSPS Calculator API
            | Dependants        | 3          |
            | Course start date | 2016-01-03 |
            | Course end date   | 2018-09-03 |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 7500.00    |
            | Course length  | 2          |
            | Leave end date | 2019-01-03 |

