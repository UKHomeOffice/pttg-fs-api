Feature: Total Funds Required Calculation - Continuation Tier 4 (General) Student Non Doctorate with and without dependants (single current account)

    Main applicants Required Maintenance period - Months between course start date and course end date (rounded up & capped to 9 months)
    Dependants Required Maintenance period - Months between main applicants course start date and course end date + wrap up period  (rounded up & capped to 9 months)
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

    ###### Continuation Main course ########

    Scenario: Chris is on an 5 month continuation main course and has 1 dependants. Chris's maintenance threshold amount calculated.

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Course type                | Main       |
            | Original course start date | 2015-02-15 |
            | Course start date          | 2016-01-05 |
            | Course end date            | 2016-05-10 |
            | Dependants                 | 1          |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 15630.50   |
            | Leave end date | 2016-09-10 |

    Scenario: Mike is on an 2 month continuation main course and has 3 dependants. Chris's maintenance threshold amount calculated.

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Course type                | Main       |
            | Original course start date | 2016-01-10 |
            | Course start date          | 2016-03-06 |
            | Course end date            | 2016-05-01 |
            | Dependants                 | 3          |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 11835.50   |
            | Leave end date | 2016-05-08 |

    Scenario: Adam is on an 14 month continuation main course and has 2 dependants. Adam's maintenance threshold amount calculated.

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Course type                | Main       |
            | Original course start date | 2015-09-18 |
            | Course start date          | 2016-02-06 |
            | Course end date            | 2017-04-01 |
            | Dependants                 | 2          |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 28295.50   |
            | Course Length  | 9          |
            | Leave end date | 2017-08-01 |

    Scenario: Paul is on an 2 month continuation main course and does not have dependants. Paul's maintenance threshold amount calculated.

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Course type                | Main       |
            | Original course start date | 2015-12-15 |
            | Course start date          | 2016-01-05 |
            | Course end date            | 2016-02-10 |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 4230.50    |
            | Leave end date | 2016-02-17 |

    Scenario: Winston is on an 7 month continuation main course and does not have dependants. Winston's maintenance threshold amount calculated.

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Course type                | Main       |
            | Original course start date | 2016-01-10 |
            | Course start date          | 2016-03-06 |
            | Course end date            | 2016-10-01 |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 10555.50   |
            | Leave end date | 2016-12-01 |

    Scenario: Lucy is on an 11 month continuation main course and does not have dependants. Lucy's maintenance threshold amount calculated.

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Course type                | Main       |
            | Original course start date | 2015-09-18 |
            | Course start date          | 2016-02-06 |
            | Course end date            | 2017-01-01 |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 13085.50   |
            | Course Length  | 9          |
            | Leave end date | 2017-05-01 |

    ###### Continuation Pre-sessional ########

    Scenario: Jane is on an 5 month continuation pre-sessional course and has 2 dependants. Jane's maintenance threshold amount calculated.

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Course type                | Pre-sessional |
            | Original course start date | 2016-08-08    |
            | Course start date          | 2017-01-05    |
            | Course end date            | 2017-05-10    |
            | Dependants                 | 2             |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 19855.50   |
            | Leave end date | 2017-07-10 |

    Scenario: Ellie is on an 6 month continuation pre-sessional course and has 3 dependants. Ellie's maintenance threshold amount calculated.

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Course type                | Pre-sessional |
            | Original course start date | 2013-10-15    |
            | Course start date          | 2016-02-06    |
            | Course end date            | 2016-08-01    |
            | Dependants                 | 3             |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 32105.50   |
            | Leave end date | 2016-12-01 |

    Scenario: Tom is on an 14 month continuation pre-sessional course and has 5 dependants. Tom's maintenance threshold amount calculated.

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Course type                | Pre-sessional |
            | Original course start date | 2014-01-01    |
            | Course start date          | 2015-02-06    |
            | Course end date            | 2016-03-10    |
            | Dependants                 | 5             |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 51110.50   |
            | Leave end date | 2016-07-10 |

    Scenario: Lorraine is on an 2 month continuation pre-sessional course and does not have dependants. Lorraine's maintenance threshold amount calculated.

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Course type                | Pre-sessional |
            | Original course start date | 2016-02-15    |
            | Course start date          | 2016-03-05    |
            | Course end date            | 2016-04-10    |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 4230.50    |
            | Leave end date | 2016-05-10 |

    Scenario: Jean is on an 7 month continuation pre-sessional course and does not have dependants. Jean's maintenance threshold amount calculated.

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Course type                | Pre-sessional |
            | Original course start date | 2016-01-10    |
            | Course start date          | 2016-04-16    |
            | Course end date            | 2016-11-08    |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 10555.50   |
            | Leave end date | 2017-01-08 |

    Scenario: Jeanette is on an 11 month continuation pre-sessional course and does not have dependants. Jeanette's maintenance threshold amount calculated.

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Course type                | Pre-sessional |
            | Original course start date | 2015-09-18    |
            | Course start date          | 2016-02-06    |
            | Course end date            | 2017-01-01    |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 13085.50   |
            | Course Length  | 9          |
            | Leave end date | 2017-05-01 |

    Scenario: Mark is on an 3 month continuation pre-sessional course and does not have dependants. Jeanette's maintenance threshold amount calculated.

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Course type                | Pre-sessional |
            | Original course start date | 2015-09-18    |
            | Course start date          | 2016-02-06    |
            | Course end date            | 2016-05-01    |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 5495.50   |
            | Leave end date | 2016-07-01 |
