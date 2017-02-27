Feature: Total Funds Required Calculation - Continuation Tier 4 (General) Student Union Sabbatical Office with and without dependants (single current account)

    Main applicants Required Maintenance period - Months between course start date and course end date (rounded up & capped to 2 months)
    Main applicant Required Maintenance period is rounded up to the full month (E.g continuation course length of 1month and 5days is rounded up to 2months)

    Continuation Maintenance period for main applicant - Duration of the continuation course rounded up (E.g continuation course duration is 1months and 3days is rounded up to 2months)

    Dependants Required Maintenance period for continuation course - Months between main applicants course start date and course end date + wrap up period then  (rounded up & capped to 2 months)
    Main applicants leave - Entire course length + wrap up period
    Course length - course start date to course end date
    Leave is calculated from original course start date to continuation course end date (E.g. Original course start date 01/01/2016, Continuation Course start date 01/05/2016, Continuation course end date 01/07/2016 - leave calculated from 01/05/16 to 01/07/16)
    Wrap up period - see table below:

    SUSO course length 12 month or more = 4 month
    SUSO course length 6 months or more but less than 12 months = 2 months
    SUSO course length <6 months = 7 days

    The concept of pre-sessional courses does not apply to the SUSO route

    Applicants Required Maintenance threshold general:  In London - £1265, Out London - £1015
    Dependants Required Maintenance threshold: In London - £845, Out London - £680

    Accommodation fees already paid - The maximum amount paid can be £1265

    Background:
        Given A Service is consuming the FSPS Calculator API
        And the default details are
            | Student Type                    | suso |
            | In London                       | Yes  |
            | Accommodation fees already paid | 500  |
            | Dependants only   | No          |

    #Required Maintenance threshold calculation to pass this feature file

    #Maintenance threshold amount = (Required Maintenance threshold general * Course length) +
    #((Dependants Required Maintenance threshold * Dependants Required Maintenance period)  * number of dependants) - (accommodation fees paid)

    #SUSO course:
    #12 months or more: ((£1265 x 12) + (£845 x (12+4) x 1) - (£50)
    #6 months or more but less than 12 months: ((£1265 x 7) + (845 x (7+2) x 1) - (£100)
    #< 6 months: ((£1265 x 2) + (£845 x (2+7days) x 1) - (£100)

    #SUSO course worked examples:

    #12 months: Tier 4 (General) Student - suso - In London, with dependents In Country - (£1265 x 2) + (£845 x 2 x 1) - (£50) = £4,170
    #7 months: Tier 4 (General) Student - suso - In London, with dependents In Country - (£1265 x 2) + (£845 x 2 x 2) - (£100) = £5,810
    #2 months: Tier 4 (General) Student - suso - In London, with dependents In Country - (£1265 x 2) + (£845 x 2 x 3) - (£100) = £7500


    #### suso continuation course ####

    Scenario: John is on a 2 month SUSO continuation course. John's Threshold calculated


        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Original course start date | 2015-12-15 |
            | Course start date          | 2016-01-03 |
            | Course end date            | 2016-02-10 |
            | Dependants                 | 0          |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 2030.00    |
            | Leave end date | 2016-02-17 |

    Scenario: Mike is on a 2 month SUSO continuation course. Mike's Threshold calculated

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Original course start date | 2014-10-15 |
            | Course start date          | 2016-01-03 |
            | Course end date            | 2016-02-10 |
            | Dependants                 | 0          |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 2030.00    |
            | Leave end date | 2016-06-10 |

    Scenario: Ann is on a 3 month SUSO continuation course and has 2 dependants. Ann's Threshold calculated

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | In London                  | No         |
            | Original course start date | 2015-09-15 |
            | Course start date          | 2016-01-01 |
            | Course end date            | 2016-04-09 |
            | Dependants                 | 2          |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 4250.00    |
            | Course Length  | 2          |
            | Leave end date | 2016-06-09 |

    Scenario: Alvin is on a 5 month SUSO continuation course and has 4 dependants. Alvin's Threshold calculated

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Original course start date | 2015-08-15 |
            | Course start date          | 2016-05-01 |
            | Course end date            | 2016-10-09 |
            | Dependants                 | 4          |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 8790.00    |
            | Course Length  | 2          |
            | Leave end date | 2017-02-09 |

    Scenario: Kira is on a 1 month SUSO continuation course and has 1 dependants. Kira's Threshold calculated

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Original course start date | 2016-04-15 |
            | Course start date          | 2016-05-11 |
            | Course end date            | 2016-06-01 |
            | Dependants                 | 1          |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 1610.00    |
            | Leave end date | 2016-06-08 |

        # SUSO continuation course - dependants only #
        # capped at 2 months #

    Scenario: 2 dependant only application (main applicant is on a 3 month SUSO continuation course)

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | In London                  | No         |
            | Original course start date | 2015-09-15 |
            | Course start date          | 2016-01-01 |
            | Course end date            | 2016-04-09 |
            | Dependants                 | 2          |
            | Dependants only            | Yes        |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 2720.00    |
            | Course Length  | 2          |
            | Leave end date | 2016-06-09 |

    Scenario: 4 dependant only application (main applicant is on a 5 month SUSO continuation course)

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Original course start date | 2015-08-15 |
            | Course start date          | 2016-05-01 |
            | Course end date            | 2016-10-09 |
            | Dependants                 | 4          |
            | Dependants only            | Yes        |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 6760.00    |
            | Course Length  | 2          |
            | Leave end date | 2017-02-09 |

    Scenario: Kira is on a 1 month SUSO continuation course and has 1 dependants. Kira's Threshold calculated

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Original course start date | 2016-04-15 |
            | Course start date          | 2016-05-11 |
            | Course end date            | 2016-06-01 |
            | Dependants                 | 1          |
            | Dependants only            | Yes        |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 845.00     |
            | Leave end date | 2016-06-08 |

