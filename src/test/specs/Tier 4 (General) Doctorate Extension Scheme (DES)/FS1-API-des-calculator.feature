Feature: Total Funds Required Calculation - Tier 4 (General) Doctorate Extension Scheme with and without dependants (single current account)

    Main applicants Required Maintenance period - 2 months regardless of course length

    The concept of continuation or pre-sessional courses do not apply to the Doctorate Extension Scheme route

    Dependants Required Maintenance period - Months between main applicants course start date and course end date + wrap up period then  (rounded up & capped to 2 months)
    Main applicants leave - Entire course length + wrap up period
    Course length - course start date to course end date
    Leave is calculated from course start date to continuation course end date
    Wrap up period - 12 months regardless of leave

    Applicants Required Maintenance threshold non doctorate:  In London - £1265, Out London - £1015
    Dependants Required Maintenance threshold: In London - £845, Out London - £680

    Accommodation fees already paid - The maximum amount paid can be £1265

    Background:
        Given A Service is consuming the FSPS Calculator API
        And the default details are
            | Student Type                    | des |
            | In London                       | Yes |
            | Accommodation fees already paid | 500 |

    #Required Maintenance threshold calculation to pass this feature file

    #Maintenance threshold amount = (Required Maintenance threshold non doctorate * 2) +
    #((Dependants Required Maintenance threshold * 2)  * number of dependants) - (accommodation fees paid)

    DES course:
    #12 months: ((£1265 x 12) + (£845 x (12) x 1) - (£50)
    #7 months: ((£1265 x 7) + (845 x (7) x 1) - (£100)
    #1 month: ((£1265 x 2) + (£845 x (2) x 1) - (£100)

    #Main course worked examples:

    #12 months: Tier 4 (General) Student - des - In London, with dependents In Country - (£1265 x 12) + (£845 x (12) x 1) - (£50) = £3,325
    #6 months: Tier 4 (General) Student - des - In London, with dependents In Country - (£1265 x 7) + (£845 x (7) x 2) - (£100) = £5,810
    #1 month: Tier 4 (General) Student - des - In London, with dependents In Country - (£1265 x 2) + (£845 x (1) x 3) - (£100) = £7500


    #### DES course ####

    Scenario: John is on a 2 month DES course. John's Threshold calculated


        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type      | des        |
            | Course start date | 2016-01-03 |
            | Course end date   | 2016-02-10 |
            | Dependants        | 0          |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 2030.00    |
            | Course Length  | 2          |
            | Leave end date | 2017-02-10 |

    Scenario: Ann is on a 3 month DES continuation course and has 2 dependants. Ann's Threshold calculated

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type      | des        |
            | In London         | No         |
            | Course start date | 2016-01-01 |
            | Course end date   | 2016-04-09 |
            | Dependants        | 2          |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 4250.00    |
            | Course Length  | 2          |
            | Leave end date | 2017-04-09 |

    Scenario: Alvin is on a 5 month DES continuation course and has 4 dependants. Alvin's Threshold calculated

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type      | des        |
            | Course start date | 2016-05-01 |
            | Course end date   | 2016-10-09 |
            | Dependants        | 4          |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 8790.00    |
            | Course Length  | 2          |
            | Leave end date | 2017-10-09 |

    Scenario: Kira is on a 1 month DES continuation course and has 1 dependant. Kira's Threshold calculated

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type      | des        |
            | Course start date | 2016-05-11 |
            | Course end date   | 2016-06-01 |
            | Dependants        | 1          |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 3720.00    |
            | Leave end date | 2017-06-01 |
