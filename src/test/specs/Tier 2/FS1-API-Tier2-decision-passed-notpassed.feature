Feature: Pass & Not Pass - Total Funds Required Calculation - Tier 2 applicants with & without dependants (single current account)

    Requirement to meet Tier 2 pass

    Applicant has required closing balance every day for a consecutive 90 day period from the date of the Maintenance End Date

    Background:
        Given a Service is consuming Financial Status API
        When the Financial Status API is invoked with the following:
            | Sort code      | 111111     |
            | Account number | 12345678   |
            | Date of Birth  | 1984-07-27 |


    ################## not pass ##############################

    Scenario: Shelly is a Tier 2 applicant and does not have sufficient funds.

    #Application Raised Date 1st of June
    # She has < than the Total Fund Required of £945 for the previous 90 days

        #Given a Service is consuming Financial Status API
        Given the test data for account 23568496
        When the Financial Status API is invoked with the following:
            | Minimum                | 945.00     |
            | To Date                | 2016-06-01 |
            | From Date              | 2016-03-04 |
            | Sort code              | 111111     |
            | Account number         | 23568498   |
            | Date of Birth          | 1984-07-27 |
            | Account Holder Consent | Yes        |
            | User Id                | user12345  |
        Then The Financial Status API provides the following results:
            | HTTP Status          | 200          |
            | Pass                 | false        |
            | Account Holder Name  | Shelly Jones |
            | Minimum              | 945.00       |
            | From Date            | 2016-03-04   |
            | Lowest Balance Date  | 2016-05-31   |
            | Lowest Balance Value | 740.00       |
            | To Date              | 2016-06-01   |
            | Sort code            | 111111       |
            | Account number       | 23568498     |


    Scenario: Ann is a Tier 2 applicant - funds have been in her account for less than 90 days

   #Application Raised Date 1st of June
    #She has the Total Fund Required of £2205 but not for 90 days #

       # Given a Service is consuming Financial Status API
        Given the test data for account 23568492
        When the Financial Status API is invoked with the following:
            | Minimum        | 2205.00    |
            | To Date        | 2016-06-01 |
            | From Date      | 2016-03-04 |
            | User Id        | user12345  |
            | Sort code      | 111111     |
            | Account number | 23568492   |
            | Date of Birth  | 1984-07-27 |
        Then The Financial Status API provides the following results:
            | HTTP Status         | 200        |
            | Pass                | false      |
            | Account Holder Name | Ann Po     |
            | Minimum             | 2205.00    |
            | From Date           | 2016-03-04 |
            | Record Count        | 89         |
            | To Date             | 2016-06-01 |
            | Sort code           | 111111     |
            | Account number      | 23568492   |

    ######################## pass #############################

    Scenario: Shelly is a Tier 2 applicant and has sufficient financial funds

    #Application Raised Date 1st of June
    #She has >= than the threshold of £1575 for the previous 90 days

        #Given a Service is consuming Financial Status API
        Given the test data for account 23568493
        When the Financial Status API is invoked with the following:
            | Minimum        | 1575.00    |
            | To Date        | 2016-06-01 |
            | From Date      | 2016-03-04 |
            | User Id        | user12345  |
            | Account number | 23568493   |
            | Sort code      | 111111     |
            | Date of Birth  | 1984-07-27 |
        Then The Financial Status API provides the following results:
            | HTTP Status         | 200          |
            | Pass                | true         |
            | Account Holder Name | Shelly Smith |
            | Minimum             | 1575.00      |
            | To Date             | 2016-06-01   |
            | From Date           | 2016-03-04   |
            | Sort code           | 111111       |
            | Account number      | 23568493     |


        ##### NOT PASS - Tier 2 - dependant only (2 applicants) #####

    Scenario: Donald and Hilary are dependant only (x2) Tier 2 applicants and do not have sufficient funds

    #Application Raised Date 1st of June
    #He has < than the Total Fund Required of £630 for the previous 90 days

        #Given a Service is consuming Financial Status API
        Given the test data for account 23568496
        When the Financial Status API is invoked with the following:
            | Minimum                | 1260.00    |
            | To Date                | 2016-06-01 |
            | From Date              | 2016-03-04 |
            | Sort code              | 111111     |
            | Account number         | 23568498   |
            | Date of Birth          | 1984-07-27 |
            | Account Holder Consent | Yes        |
        Then The Financial Status API provides the following results:
            | HTTP Status          | 200          |
            | Pass                 | false        |
            | Account Holder Name  | Shelly Jones |
            | Minimum              | 1260.00      |
            | From Date            | 2016-03-04   |
            | Lowest Balance Date  | 2016-05-31   |
            | Lowest Balance Value | 740.00       |
            | To Date              | 2016-06-01   |
            | Sort code            | 111111       |
            | Account number       | 23568498     |


        ##### PASS - Tier 2 - dependant only (2 applicants) #####

    Scenario: Kim and Silvio are dependant only applicants (Tier 2) and have sufficient funds

        #Application Raised Date 1st of June
        #She has >= than the threshold of £630 for the previous 90 days

            #Given a Service is consuming Financial Status API
        Given the test data for account 23568493
        When the Financial Status API is invoked with the following:
            | Minimum        | 1260.00    |
            | To Date        | 2016-06-01 |
            | From Date      | 2016-03-04 |
            | Account number | 23568493   |
            | Sort code      | 111111     |
            | Date of Birth  | 1984-07-27 |
        Then The Financial Status API provides the following results:
            | HTTP Status         | 200          |
            | Pass                | true         |
            | Account Holder Name | Shelly Smith |
            | Minimum             | 1260.00      |
            | To Date             | 2016-06-01   |
            | From Date           | 2016-03-04   |
            | Sort code           | 111111       |
            | Account number      | 23568493     |

