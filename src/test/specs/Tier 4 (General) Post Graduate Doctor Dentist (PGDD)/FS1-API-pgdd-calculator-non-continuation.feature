Feature: Total Funds Required Calculation - Initial Tier 4 (General) Student Post Graduate Doctor Dentist In and Out of London (single current account and no dependants)

    Pre-sessional courses do not apply to the Post Graduate Doctor Dentist route
    Main applicants Required Maintenance period: Months between course start date and course end date (rounded up & capped to 2 months)
    Main applicant Required Maintenance period is rounded up to the full month (E.g course length of 1month and 4days is rounded up to 2months)

    Dependants Required Maintenance period - Months between main applicants course start date & course end date + wrap up period (rounded up & capped to 2 months)
    Dependants Required Maintenance period is only rounded up to the full month after the wrap up is applied then rounded up to 2 months

    Main applicants leave - Entire course length + wrap up period
    Course length - course start date to course end date (Main Course)
    Wrap up period calculated from original course start date to course end date
    Wrap up period - 1 month in all instances regardless of course length
    If course has ended when assessing case, then wrap up period will be added from case consideration/ processing date ##


    Applicants Required Maintenance threshold general:  In London - £1265, Out London - £1015
    Dependants Required Maintenance threshold: In London - £845, Out London - £680

    Background:
        Given A Service is consuming the FSPS Calculator API
        And the default details are
            | Student Type                    | pgdd    |
            | In London                       | Yes     |
            | Accommodation fees already paid | 0       |
            | Dependants only                 | No   |

#Required Maintenance threshold calculation to pass this feature file

#Maintenance threshold amount = (Required Maintenance threshold general * Course length) +
#((Dependants Required Maintenance threshold * Dependants Required Maintenance period)  * number of dependants) - (accommodation fees paid)

#12 months: ((£1265 x 12) + (845 x (12+1) x 1) - (£100)
#8 months: ((£1265 x 8) + (845 x (8+1) x 2) - (£100)
#1 months: ((£1265 x 1) + (845 x (1+1) x 3) - (£0)

#Worked examples:

#12 months: Tier 4 (General) Student - pgdd - In London, with dependents In Country - (£1265 x 2) + (£845 x 2 x 1) - (£100) = £4,120
#8 months: Tier 4 (General) Student - pgdd - In London, with dependents In Country - (£1265 x 2) + (£845 x 2 x 2) - (£100) = £5,810
#1 month: Tier 4 (General) Student - pgdd - In London, with dependents In Country - (£1265 x 2) + (£845 x 2 x 3) - (£0) = £6,335

 ##### In London ####

    Scenario: Tony's maintenance threshold amount calculated. He is on a 2 month pgdd course

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type                    | pgdd       |
            | Course start date               | 2016-01-03 |
            | Course end date                 | 2016-02-03 |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 2530.00    |
            | Leave end date | 2016-03-03 |

    Scenario: Shelly's maintenance threshold amount calculated. She is on a 3 month pgdd course

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Course start date               | 2016-01-03 |
            | Course end date                 | 2016-03-03 |
            | Accommodation fees already paid | 0          |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 2530.00    |
            | Course Length  | 2          |
            | Leave end date | 2016-04-03 |

    Scenario: Martin's Threshold calculated. He is on a 1 month pgdd course and has 1 dependent

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Course start date               | 2016-01-03 |
            | Course end date                 | 2016-01-31 |
            | dependants                      | 1          |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 2955.00    |
            | Leave end date | 2016-02-29 |


    Scenario: Jean's Threshold calculated. She is on a 3 month pdgg course and has 3 dependents

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Course start date               | 2016-01-03 |
            | Course end date                 | 2016-03-03 |
            | Accommodation fees already paid | 250.50     |
            | dependants                      | 3          |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 7349.50    |
            | Course Length  | 2          |
            | Leave end date | 2016-04-03 |


   ###########           ######### new scenario########
    @ignore
    Scenario: The consideration date is taking place on 2016-04-15 after the course has ended. + 1 month and 13 Days
    Shelly's maintenance threshold amount calculated. She is on a 3 month pgdd course

        Given A Service is consuming the FSPS Calculator API
        And the consideration date is 2016-04-15
        When the FSPS Calculator API is invoked with the following
            | Course start date               | 2016-01-03 |
            | Course end date                 | 2016-03-03 |
            | Accommodation fees already paid | 0          |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 2530.00    |
            | Course Length  | 2          |
            | Leave end date | 2016-05-15 |

      #######  #### new scenario#############
    @ignore
    Scenario: The consideration date is taking place on "Current date" after the course has ended.
    Shelly's maintenance threshold amount calculated. She is on a 3 month pgdd course

        Given A Service is consuming the FSPS Calculator API
        And the consideration date is Current date
        When the FSPS Calculator API is invoked with the following
            | Course start date               | 2017-03-03 |
            | Course end date                 | 2017-06-03 |
            | Accommodation fees already paid | 0          |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200                          |
            | Threshold      | 2530.00                      |
            | Course Length  | 2                            |
            | Leave end date | + 1 month from current date  |


### Out London ###

    Scenario: John's maintenance threshold amount calculated. He is on a 2 month pgdd course

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | In London                       | No         |
            | Course start date               | 2016-01-03 |
            | Course end date                 | 2016-02-03 |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 2030.00    |
            | Leave end date | 2016-03-03 |

    Scenario: Ann's Threshold maintenance threshold amount. She is on a 5 months pgdd course

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | In London                       | No         |
            | Course start date               | 2016-01-03 |
            | Course end date                 | 2016-05-03 |
            | Accommodation fees already paid | 250.00        |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 1780.00    |
            | Course Length  | 2          |
            | Leave end date | 2016-06-03 |

    Scenario: Mo's Threshold calculated. He is on a 2 month course and has 3 dependents

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | In London                       | No         |
            | Course start date               | 2016-01-03 |
            | Course end date                 | 2016-02-03 |
            | dependants                      | 3          |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 6110.00    |
            | Leave end date | 2016-03-03 |

    Scenario: Adam's Threshold calculated. He is on a 9 month course and has 1 dependents.

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | In London                       | No         |
            | Course start date               | 2016-01-03 |
            | Course end date                 | 2016-08-03 |
            | Accommodation fees already paid | 100.00     |
            | dependants                      | 1          |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 3290.00    |
            | Course Length  | 2          |
            | Leave end date | 2016-09-03 |

    # In London - dependants only #
    # Capped at 2 months #

    Scenario: 1 dependant only application (main applicant is on a 1 month pgdd course) and in London

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Course start date | 2016-01-03 |
            | Course end date   | 2016-01-31 |
            | In London         | Yes        |
            | Dependants        | 1          |
            | Dependants Only   | Yes        |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 1690.00    |
            | Leave end date | 2016-02-29 |


    Scenario: 3 dependant only application (main applicant is on a 3 month pgdd course) and in London

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Course start date | 2016-01-03 |
            | Course end date   | 2016-03-03 |
            | In London         | Yes        |
            | Dependants        | 3          |
            | Dependants Only   | Yes        |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 5070.00    |
            | Course Length  | 2          |
            | Leave end date | 2016-04-03 |

        # Out of London - dependants only #
        # Capped at 2 months #

    Scenario: 3 dependant only application (main applicant is on a 3 month pgdd course) and out of London

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | In London         | No         |
            | Course start date | 2016-01-03 |
            | Course end date   | 2016-02-03 |
            | Dependants        | 3          |
            | Dependants Only   | Yes        |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 4080.00    |
            | Leave end date | 2016-03-03 |

    Scenario: 1 dependant only application (main applicant is on a 9 month pgdd course) and out of London

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | In London         | No         |
            | Course start date | 2016-01-03 |
            | Course end date   | 2016-08-03 |
            | Dependants        | 1          |
            | Dependants Only   | Yes        |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 1360.00    |
            | Course Length  | 2          |
            | Leave end date | 2016-09-03 |


