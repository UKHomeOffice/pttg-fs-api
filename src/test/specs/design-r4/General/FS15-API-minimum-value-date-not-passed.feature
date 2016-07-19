Feature: Not Pass - Total Funds Required Calculation - Tier 4 (General) student (single current account and no dependants)

    Applicant does not have required closing balance every day for a consecutive 28 day period from the date of the Maintenance End Date
    If not passed, API will show minimum balance and the date it was. If there is more than one days when the applicants balance was below the threshold, on the output page will see first date

    Scenario: Shelly is a doctorate in London student and does not have sufficient financial funds

    Application Raised Date 1st of June
    She has < than the Total funds required of £2350 for the previous 28 days


        Given a Service is consuming Financial Status API
        Given the test data for account 23568499
        When the Financial Status API is invoked with the following:
            | To date        | 2016-06-01 |
            | From date      | 2016-05-05 |
            | Minimum        | 2530.00    |
            | Sort code      | 135610     |
            | Account number | 23568499   |


        Then The Financial Status API provides the following results:
            | HTTP Status        | 200        |
            | Pass               | false      |
            | Minimum            | 2530.00    |
            | From date          | 2016-05-05 |
            | To date            | 2016-06-01 |
            | Date funds not met | 2016-05-31 |
            | Amount             | 2500       |
            | Sort code          | 135609     |
            | Account number     | 23568499   |


    Scenario: Brian is doctorate out of London student and does not have sufficient financial funds

    Application Raised Date 4th of July
    He has < than the Total Funds Required of £2030 for the previous 28 days

        Given a Service is consuming Financial Status API
        Given the test data for account 01078912
        When the Financial Status API is invoked with the following:
            | To date        | 2016-07-01 |
            | From date      | 2016-06-04 |
            | Minimum        | 2030.00    |
            | Sort code      | 149303     |
            | Account number | 01078912   |


        Then The Financial Status API provides the following results:
            | HTTP Status        | 200        |
            | Pass               | false      |
            | Minimum            | 2030.00    |
            | From date          | 2016-06-04 |
            | To date            | 2016-07-01 |
            | Date funds not met |            |
            | Amount             |            |
            | Sort code          | 149303     |
            | Account number     | 01078911   |


    Scenario: David is general student and does not have sufficient financial funds

    Application Raised Date 4th of July
    He has < than the Total Funds Required of £2537.48 for the previous 28 days


        Given a Service is consuming Financial Status API
        Given the test data for account 17926767
        When the Financial Status API is invoked with the following:
            | To date        | 2016-07-01 |
            | From date      | 2016-06-04 |
            | Minimum        | 2537.48    |
            | Sort code      | 139302     |
            | Account number | 17926768   |


        Then The Financial Status API provides the following results:
            | HTTP status        | 200        |
            | Pass               | false      |
            | Minimum            | 2537.48    |
            | From date          | 2016-06-04 |
            | To date            | 2016-07-01 |
            | Date funds not met |            |
            | Amount             |            |
            | Sort code          | 139302     |
            | Account number     | 17926768   |
