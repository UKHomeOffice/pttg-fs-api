Feature: Total Funds Required Calculation - Continuation Tier 4 (General) Student general with and without dependants (single current account)

    Main applicants Required Maintenance period - Months between course start date and course end date (rounded up & capped to 9 months)
    Main applicant Required Maintenance period is rounded up to the full month (E.g continuation course length of 2month and 5days is rounded up to 3months)

    Continuation Maintenance period for main applicant - Duration of the continuation course rounded up (E.g continuation course duration is 2months and 3days is rounded up to 3months)

    Dependants Required Maintenance period for continuation course - Months between main applicants course start date and course end date + wrap up period then  (rounded up & capped to 9 months)
    Main applicants leave - Entire course length + wrap up period
    Course length - course start date to course end date
    Leave is calculated from original course start date to continuation course end date (E.g. Original course start date 01/01/2016, Continuation Course start date 01/05/2016, Continuation course end date 01/07/2016 - leave calculated from 01/05/16 to 01/07/16)
    Wrap up period - see table below:

    Main course length 12 month or more = 4 month
    Main course length 6 months or more but less than 12 months = 2 months
    Main course length <6 months = 7 days
    Pre-sessional course length 12 month or more = 4 month
    Pre-sessional course length 6 months or more but less than months = 2 months
    Pre-sessional course length <6 months = 1 months
    If course has ended when considering the case, then wrap up period will be added from case consideration date/processing date ##

    Applicants Required Maintenance threshold general:  In London - £1265, Out London - £1015
    Dependants Required Maintenance threshold: In London - £845, Out London - £680

    Total tuition fees - total amount of the tuition fees for the course
    Tuition fees already paid - total amount of tuition fees already paid
    Accommodation fees already paid - The maximum amount paid can be £1265

    Background:
        Given A Service is consuming the FSPS Calculator API
        And the default details are
            | Student Type                    | general |
            | In London                       | Yes     |
            | Total tuition fees              | 2000.50 |
            | Tuition fees already paid       | 200     |
            | Accommodation fees already paid | 100     |
            | Dependants only                 | No      |

    #Required Maintenance threshold calculation to pass this feature file

    #Maintenance threshold amount = (Required Maintenance threshold general * Course length) +
    #((Dependants Required Maintenance threshold * Dependants Required Maintenance period)  * number of dependants) + (total tuition fees - tuition fees paid - accommodation fees paid)

    #Main course:
    #12 months or more: ((£1265 x 4) + (845 x (4+4) x 1) + (£10,000 - 0 - 0))
    #6 months or more but less than 12 months: ((£1265 x 4) + (845 x (4+2) x 1) + (£10,000 - 0 - 0))
    #< 6 months: ((£1265 x 4) + (845 x (4+1) x 1) + (£10,000 - 0 - 0))

    #Main course worked examples:

    #12 months or more: Tier 4 (General) Student - general - In London, with dependents In Country - (£1265 x 3) + (£845 x (3+4) x 1) + (£10,000 - £0 - £0) = £19,710
    #6 months or more but less than 12 months: Tier 4 (General) Student - general - In London, with dependents In Country - (£1265 x 8) + (£845 x (8+2) x 2) + (£7,000 - £300 - £500.50) = £31,529.50 (dependant require maintenance period capped at 9 months)
    #< 6 months: Tier 4 (General) Student - general - In London, with dependents In Country - (£1265 x 4) + (£845 x (4+7 days) x 1) + (£10,000 - £0 - £0) = (£18440)

    #Pre-sessional:
    #12 months or more: Same as Main course above
    #6 months or more but less than 12 months: Same as main course above
    #< 6 months: Tier 4 (General) Student - general - In London, with dependents In Country - (£1265 x 4) + (£845 x (4+1) x 1) + (£10,000 - £0 - £0) = (£19,285)

    ###### Continuation Main course ########

    Scenario: Chris is on an 5 month continuation main course and has 1 dependants. Chris's maintenance threshold amount calculated.

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Course type                | main       |
            | Original course start date | 2015-02-15 |
            | Course start date          | 2016-01-05 |
            | Course end date            | 2016-05-10 |
            | Dependants                 | 1          |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 15630.50   |
            | Leave end date | 2016-09-10 |

    ## NEW SCENARIO ADDED ON 26/06/2017
    @ignore
    Scenario: Chris is on an 5 month continuation main course and has 1 dependants. The consideration date is taking place on 2016-07-01 after the course has ended. Chris's maintenance threshold amount calculated.

        Given A Service is consuming the FSPS Calculator API
        And the consideration date is 2016-07-01
        When the FSPS Calculator API is invoked with the following
            | Course type                | main       |
            | Original course start date | 2015-02-15 |
            | Course start date          | 2016-01-05 |
            | Course end date            | 2016-05-10 |
            | Dependants                 | 1          |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 15630.50   |
            | Leave end date | 2016-12-01 |

    Scenario: Mike is on an 2 month continuation main course and has 3 dependants. Chris's maintenance threshold amount calculated.

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Course type                | main       |
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
            | Course type                | main       |
            | Original course start date | 2015-09-18 |
            | Course start date          | 2016-02-06 |
            | Course end date            | 2017-04-01 |
            | Dependants                 | 2          |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 28295.50   |
            | Course Length  | 9          |
            | Leave end date | 2017-08-01 |

###New scenario 05/07/2017 with rounding dates######
    Scenario: Zlatan is on an 8 month 1 day continuation main course and has 7 dependants.
    Zlatan's maintenance threshold amount and course end date calculated.

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Course type                | main       |
            | Original course start date | 2013-01-01 |
            | Course start date          | 2017-04-30 |
            | Course end date            | 2017-12-31 |
            | Dependants                 | 7          |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 51975.00   |
            | Course Length  | 9          |
            | Leave end date | 2018-04-30 |


    Scenario: Paul is on an 2 month continuation main course and does not have dependants. Paul's maintenance threshold amount calculated.

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Course type                | main       |
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
            | Course type                | main       |
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
            | Course type                | main       |
            | Original course start date | 2015-09-18 |
            | Course start date          | 2016-02-06 |
            | Course end date            | 2017-01-01 |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 13085.50   |
            | Course Length  | 9          |
            | Leave end date | 2017-05-01 |

    Scenario: John is on an 2 months and 2 days continuation pre-sessional course and does not have dependants. John's maintenance threshold amount calculated.

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Course type                | main       |
            | Original course start date | 2015-09-18 |
            | Course start date          | 2016-02-06 |
            | Course end date            | 2016-04-07 |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 5495.50    |
            | Leave end date | 2016-06-07 |

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

   ## NEW SCENARIO ADDED ON 26/06/2017
    @ignore
   Scenario: Jane is on an 5 month continuation pre-sessional course and has 2 dependants. The consideration date is taking place on 2016-06-23 after the course has ended. Jane's maintenance threshold amount calculated.

           Given A Service is consuming the FSPS Calculator API
           And the consideration date is 2016-06-23
           When the FSPS Calculator API is invoked with the following
               | Course type                | Pre-sessional |
               | Original course start date | 2016-08-08    |
               | Course start date          | 2017-01-05    |
               | Course end date            | 2017-05-10    |
               | Dependants                 | 2             |
           Then The Financial Status API provides the following results:
               | HTTP Status    | 200        |
               | Threshold      | 19855.50   |
               | Leave end date | 2017-08-23 |

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

    Scenario: Mark is on an 3 month continuation pre-sessional course and does not have dependants. Mark's maintenance threshold amount calculated.

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Course type                | Pre-sessional |
            | Original course start date | 2015-09-18    |
            | Course start date          | 2016-02-06    |
            | Course end date            | 2016-05-01    |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 5495.50    |
            | Leave end date | 2016-07-01 |

###### Continuation Main course - Dependants Only ########

        # Capped at 9 months for dependants #
        # round up to the nearest month #
        # Calculate the threshold period using Course Start Date to Leave End Date #

#    Background:
#        Given A Service is consuming the FSPS Calculator API
#        And the default details are
#            | Student Type | general |
#            | In London    | Yes     |

    Scenario: Isaac is on a dependant (x1) only application (main applicant is on a 5 month continuation main course)

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Course type                | main       |
            | Original course start date | 2015-02-15 |
            | Course start date          | 2016-01-05 |
            | Course end date            | 2016-05-10 |
            | Dependants                 | 1          |
            | Dependants only            | Yes        |
            | Student Type               | general    |
            | In London                  | Yes        |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 7605.00    |
            | Leave end date | 2016-09-10 |

    ## NEW SCENARIO ADDED ON 26/06/2017
    @ignore
    Scenario: Isaac is on a dependant (x1) only application (main applicant is on a 5 month continuation main course). The consideration date is taking place on 2016-06-15 after the course has ended.

            Given A Service is consuming the FSPS Calculator API
            And the consideration date is 2016-06-15
            When the FSPS Calculator API is invoked with the following
                | Course type                | main       |
                | Original course start date | 2015-02-15 |
                | Course start date          | 2016-01-05 |
                | Course end date            | 2016-05-10 |
                | Dependants                 | 1          |
                | Dependants only            | Yes        |
                | Student Type               | general    |
                | In London                  | Yes        |
            Then The Financial Status API provides the following results:
                | HTTP Status    | 200        |
                | Threshold      | 7605.00    |
                | Leave end date | 2016-10-15 |

    Scenario: Mel, Geri, Victoria are on a dependant (x3) only application (main applicant is on a 2 month continuation main course)

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Course type                | main       |
            | Original course start date | 2016-01-10 |
            | Course start date          | 2016-03-06 |
            | Course end date            | 2016-05-01 |
            | Dependants                 | 3          |
            | Dependants only            | Yes        |
            | Student Type               | general    |
            | In London                  | Yes        |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 7605.00    |
            | Leave end date | 2016-05-08 |

    Scenario: Ander and Juan are on a dependant (x2) only application (main applicant is on a 14 month continuation main course)

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Course type                | main       |
            | Original course start date | 2015-09-18 |
            | Course start date          | 2016-02-06 |
            | Course end date            | 2017-04-01 |
            | Dependants                 | 2          |
            | Dependants only            | Yes        |
            | Student Type               | general    |
            | In London                  | Yes        |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 15210.00   |
            | Course Length  | 9          |
            | Leave end date | 2017-08-01 |

###### Continuation Pre-sessional - Dependants Only ########

    Scenario: 2 dependants only application (main applicant is on a 5 month continuation pre-sessional course)

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Course type                | Pre-sessional |
            | Original course start date | 2016-08-08    |
            | Course start date          | 2017-01-05    |
            | Course end date            | 2017-05-10    |
            | Dependants                 | 2             |
            | Dependants only            | Yes           |
            | Student Type               | general       |
            | In London                  | Yes           |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 11830.00   |
            | Leave end date | 2017-07-10 |

    ## NEW SCENARIO ADDED ON 26/06/2017
    @ignore
    Scenario: 2 dependants only application (main applicant is on a 5 month continuation pre-sessional course). The consideration date is taking place on 2016-09-12 after the course has ended.

            Given A Service is consuming the FSPS Calculator API
            And the consideration date is 2016-09-12
            When the FSPS Calculator API is invoked with the following
                | Course type                | Pre-sessional |
                | Original course start date | 2016-08-08    |
                | Course start date          | 2017-01-05    |
                | Course end date            | 2017-05-10    |
                | Dependants                 | 2             |
                | Dependants only            | Yes           |
                | Student Type               | general       |
                | In London                  | Yes           |
            Then The Financial Status API provides the following results:
                | HTTP Status    | 200        |
                | Threshold      | 11830.00   |
                | Leave end date | 2017-11-12 |

    Scenario: 3 dependants only application (main applicant is on a 6 month continuation pre-sessional course)

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Course type                | Pre-sessional |
            | Original course start date | 2013-10-15    |
            | Course start date          | 2016-02-06    |
            | Course end date            | 2016-08-01    |
            | Dependants                 | 3             |
            | Dependants only            | Yes           |
            | Student Type               | general       |
            | In London                  | Yes           |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 22815.00   |
            | Leave end date | 2016-12-01 |

    Scenario: 5 dependants only application (main applicant is on a 14 month continuation pre-sessional course.

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Course type                | Pre-sessional |
            | Original course start date | 2014-01-01    |
            | Course start date          | 2015-02-06    |
            | Course end date            | 2016-03-10    |
            | Dependants                 | 5             |
            | Dependants only            | Yes           |
            | Student Type               | general       |
            | In London                  | Yes           |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 38025.00   |
            | Leave end date | 2016-07-10 |


