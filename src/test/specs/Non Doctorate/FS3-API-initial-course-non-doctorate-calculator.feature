Feature: Total Funds Required Calculation - Initial Tier 4 (General) Student Non Doctorate with and without dependants (single current account)

    Main applicants Required Maintenance period: Months between course start date and course end date (rounded up & capped to 9 months)
    Dependants Required Maintenance period - Months between main applicants course start date & course end date wrap up period (rounded up & capped to 9 months)
    Main applicants leave - Entire course length + wrap up period
    Course length - course start date to course end date
    Wrap up period calculated from original course start date to course end date
    Wrap up period - see table below:

    Main course length 12 month or more = 4 month
    Main course length 6 months or more but less than 12 months = 2 months
    Main course length <6 months = 7 days
    Pre-sessional course length 12 month or more = 4 month
    Pre-sessional course length 6 months or more but less than months = 2 months
    Pre-sessional course length <6 months = 1 months

    Applicants Required Maintenance threshold non doctorate:  In London - £1265, Out London - £1015
    Dependants Required Maintenance threshold: In London - £845, Out London - £680

    Total tuition fees - total amount of the tuition fees for the course
    Tuition fees already paid - total amount of tuition fees already paid
    Accommodation fees already paid - The maximum amount paid can be £1265

    Background:
        Given A Service is consuming the FSPS Calculator API
        And the default details are
            | Student Type                    | nondoctorate |
            | In London                       | Yes          |
            | Total tuition fees              | 2000.50      |
            | Tuition fees already paid       | 200          |
            | Accommodation fees already paid | 100          |

    #Required Maintenance threshold calculation to pass this feature file

    #Maintenance threshold amount = (Required Maintenance threshold non doctorate * Course length) +
    #((Dependants Required Maintenance threshold * Dependants Required Maintenance period)  * number of dependants) + (total tuition fees - tuition fees paid - accommodation fees paid)

    #Main course:
    #12 months or more: ((£1265 x 4) + (845 x (4+4) x 1) + (£10,000 - 0 - 0))
    #6 months or more but less than 12 months: ((£1265 x 4) + (845 x (4+2) x 1) + (£10,000 - 0 - 0))
    #< 6 months: ((£1265 x 4) + (845 x (4+1) x 1) + (£10,000 - 0 - 0))

    #Main course worked examples:

    #12 months or more: Tier 4 (General) Student - non doctorate - In London, with dependents In Country - (£1265 x 3) + (£845 x (3+4) x 1) + (£10,000 - £0 - £0) = £19,710
    #6 months or more but less than 12 months: Tier 4 (General) Student - non doctorate - In London, with dependents In Country - (£1265 x 8) + (£845 x (8+2) x 2) + (£7,000 - £300 - £500.50) = £31,529.50 (dependant require maintenance period capped at 9 months)
    #< 6 months: Tier 4 (General) Student - non doctorate - In London, with dependents In Country - (£1265 x 4) + (£845 x (4+7 days) x 1) + (£10,000 - £0 - £0) = (£18440)

    #Pre-sessional:
    #12 months or more: Same as Main course above
    #6 months or more but less than 12 months: Same as main course above
    #< 6 months: Tier 4 (General) Student - non doctorate - In London, with dependents In Country - (£1265 x 4) + (£845 x (4+1) x 1) + (£10,000 - £0 - £0) = (£19,285)

    ################# Initial Main course #######################

    Scenario: Martin is on an initial 6 months main course and does not have dependants. Martin's maintenance threshold amount calculated.

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Course type       | Main       |
            | Dependants        | 0          |
            | Course start date | 2016-01-03 |
            | Course end date   | 2016-07-03 |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200       |
            | Threshold      | 10555.50  |
            | Leave end date | 2016-09-03 |

    Scenario: Stuart is on an initial 13 months main course and does not have dependants. Stuart's maintenance threshold amount calculated.

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Course type       | Main       |
            | Dependants        | 0          |
            | Course start date | 2016-01-03 |
            | Course end date   | 2017-01-03 |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 13085.50   |
            | Course Length  | 9          |
            | Leave end date | 2017-05-03 |

    Scenario: Andy is on an initial 2 months main course and has 2 dependants. Andy's maintenance threshold amount calculated.

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Course type       | Main       |
            | Dependants        | 2          |
            | Course start date | 2016-01-03 |
            | Course end date   | 2016-02-03 |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 7610.50    |
            | Leave end date | 2016-02-10 |

    Scenario: Phil is on an initial 2 months main course and has 2 dependants. Phil's maintenance threshold amount calculated.

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Course type       | Main       |
            | Dependants        | 2          |
            | Course start date | 2016-01-03 |
            | Course end date   | 2016-02-28 |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 9300.50    |
            | Leave end date | 2016-03-06 |

    Scenario: Karen is on an initial 7 months main course and has 1 have dependants. Karen's maintenance threshold amount calculated.

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Course type       | Main       |
            | Dependants        | 1          |
            | Course start date | 2016-01-03 |
            | Course end date   | 2016-07-28 |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 18160.50   |
            | Leave end date | 2016-09-28 |

    Scenario: Michael is on an initial 32 months main course and has 3 have dependants. Michael's maintenance threshold amount calculated.

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Course type       | Main       |
            | Dependants        | 3          |
            | Course start date | 2016-01-03 |
            | Course end date   | 2018-09-03 |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 35900.50   |
            | Course length  | 9          |
            | Leave end date | 2019-01-03 |

        ############## Initial Pre-sessional ######################

    Scenario: Saul is on an initial 9 months pre-sessional course and does not have dependants. Saul's maintenance threshold amount calculated.

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | In London         | No            |
            | Dependants        | 0             |
            | Course type       | Pre-sessional |
            | Course start date | 2016-01-03    |
            | Course end date   | 2016-10-10    |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 10835.50   |
            | Course Length  | 9          |
            | Leave end date | 2016-12-10 |

    Scenario: Greg is on an initial 5 months pre-sessional course and does not have dependants. Greg's maintenance threshold amount calculated.

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | In London         | No            |
            | Dependants        | 0             |
            | Course type       | Pre-sessional |
            | Course start date | 2015-01-03    |
            | Course end date   | 2015-05-03    |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 6775.50    |
            | Leave end date | 2015-06-03 |

    Scenario: Wasim is on an initial 25 months pre-sessional course and does not have dependants. Wasim's maintenance threshold account calculated.

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | In London         | No            |
            | Dependants        | 0             |
            | Course type       | Pre-sessional |
            | Course start date | 2014-11-03    |
            | Course end date   | 2016-11-10    |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 10835.50   |
            | Course Length  | 9          |
            | Leave end date | 2017-03-10 |

    Scenario: Calvin is on an initial 2 months main course and has 2 dependants. Calvin's maintenance threshold account calculated.

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Course type       | Pre-sessional |
            | Dependants        | 2             |
            | Course start date | 2016-09-03    |
            | Course end date   | 2016-10-03    |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 9300.50    |
            | Leave end date | 2016-11-03 |

    Scenario: Lizzie is on an initial 8 months main course and has 1 have dependants. Lizzie's maintenance threshold account calculated.

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Course type       | Pre-sessional |
            | Dependants        | 1             |
            | Course start date | 2016-03-10    |
            | Course end date   | 2016-10-28    |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 19425.50   |
            | Leave end date | 2016-12-28 |

    Scenario: Levi is on an initial 21 months main course and has 4 have dependants. Livi's maintenance threshold account calculated.

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Course type       | Pre-sessional |
            | Dependants        | 4             |
            | Course start date | 2016-01-03    |
            | Course end date   | 2017-09-15    |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 43505.50   |
            | Course length  | 9          |
            | Leave end date | 2018-01-15 |
