@wiremock
Feature: Pass Threshold Calculation - Tier 4 (General) student (single current account and no dependants)

    Requirement to meet Tier 4 pass
    Applicant has the required closing balance every day for a consecutive 28 day period from the date of the Application Raised Date

#Added to Jira PT-28 - Add 'Account holder name' to FSPS API
    Scenario: Shelly is a general student and has sufficient financial funds

    #Application Raised Date 1st of June
    #She has >= than the threshold of £2350 for the previous 28 days

        Given a Service is consuming Financial Status API
        Given the test data for account 01010312
        When the Financial Status API is invoked with the following:
            | Account number         | 01010312   |
            | Sort code              | 12-34-56   |
            | Minimum                | 2530.00    |
            | To Date                | 2016-06-01 |
            | From Date              | 2016-05-05 |
            | Date of Birth          | 1984-07-27 |
            | User Id                | user12345  |
            | Account Holder Consent | true       |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Pass           | true       |
            | Account Holder Name  | Shelly Smith |
            | Minimum        | 2530.00    |
           # | Unique Reference        | value      |
            | To Date        | 2016-06-01 |
            | From Date      | 2016-05-05 |
            | Account number | 01010312   |
            | Sort code      | 123456     |

#Added to Jira PT-28 - Add 'Account holder name' to FSPS API
    Scenario: Brian is general student and has sufficient financial funds

    #Application Raised Date 4th of July
    #He has >= than the threshold of £2530 for the previous 28 days


        Given a Service is consuming Financial Status API
        Given the test data for account 01078912
        When the Financial Status API is invoked with the following:
            | Account number         | 01078912   |
            | Sort code              | 23-53-68   |
            | Minimum                | 2030.00    |
            | To Date                | 2016-07-01 |
            | From Date              | 2016-06-04 |
            | Date of Birth          | 1984-07-27 |
            | User Id                | user12345  |
            | Account Holder Consent | true       |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Pass           | true       |
            | Account Holder Name  | Brian Chang |
            | Minimum        | 2030.00    |
           # | Unique Reference        | value      |
            | To Date        | 2016-07-01 |
            | From Date      | 2016-06-04 |
            | Account number | 01078912   |
            | Sort code      | 235368     |




    Scenario: Shelly is a general student with sufficient financial funds and her application is being considered (defined as the day of UI form submission)  after the course end date.

    #Application Raised Date 1st of June
    #    Course end date - 15th of  May
    #She has >= than the threshold of £2350 for the previous 28 days

        Given a Service is consuming Financial Status API
        And the consideration date is 2016-04-10
        Given the test data for account 01010312
        When the Financial Status API is invoked with the following:
            | Account number         | 01010312   |
            | Sort code              | 12-34-56   |
            | Minimum                | 2530.00    |
            | To Date                | 2016-06-01 |
            | From Date              | 2016-05-05 |
            | Date of Birth          | 1984-07-27 |
            | User Id                | user12345  |
            | Account Holder Consent | true       |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Pass           | true       |
            | Account Holder Name  | Shelly Smith |
            | Minimum        | 2530.00    |
           # | Unique Reference        | value      |
            | To Date        | 2016-06-01 |
            | From Date      | 2016-05-05 |
            | Account number | 01010312   |
            | Sort code      | 123456     |
