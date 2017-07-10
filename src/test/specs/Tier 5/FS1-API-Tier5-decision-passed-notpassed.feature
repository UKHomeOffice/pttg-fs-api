Feature: Pass & Not Pass - Total Funds Required Calculation - Tier 5 temporary and youth mobility applicants with & without dependants (single current account)

    Requirement to meet Tier 5 pass

    Applicant has required closing balance every day for a consecutive 90 day period from the date of the Maintenance End Date

   # Background:
     #   Given a Service is consuming Financial Status API
     #   When the Financial Status API is invoked with the following:
      #      | Sort code      | 111111     |
      #      | Account number | 12345678   |
      #      | Date of Birth  | 1984-07-27 |


        ############ pass tier 5 temporary main applicant #############################

    Scenario: Shelly is a Tier 5 applicant and has sufficient financial funds

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




    ######################### not pass ################################################################


    Scenario: Ashwin is a Tier 5 Temporary Worker applicant and does not have sufficient financial funds

    #Application Raised Date 4th of July
    #He has < than the Total Funds Required of £945 for the previous 90 days

       # Given a Service is consuming Financial Status API
        Given the test data for account 23568496
        When the Financial Status API is invoked with the following:
            | Minimum        | 1890.00    |
            | To Date        | 2016-07-04 |
            | From Date      | 2016-04-06 |
            | Sort code      | 111111     |
            | Account number | 12345678   |
            | User Id        | user12345  |
            | Date of Birth  | 1984-07-27 |
        Then The Financial Status API provides the following results:
            | HTTP Status          | 200          |
            | Pass                 | false        |
            | Account Holder Name  | Shelly Jones |
            | Minimum              | 1890.00      |
            | From Date            | 2016-04-06   |
            | Lowest Balance Date  | 2016-05-31   |
            | Lowest Balance Value | 740.00       |
            | To Date              | 2016-07-04   |
            | Sort code            | 111111       |
            | Account number       | 12345678     |


    ####################pass tier 5 temporary worker with dependant#############################################
    Scenario: Jacques is a Tier 5 Temporary Worker applicant with a dependant and has sufficient financial funds

    #Application Raised Date 4th of July
    #He has > than the Total Funds Required of £1575.00 for the previous 90 days

       # Given a Service is consuming Financial Status API
        Given the test data for account 23568495
        When the Financial Status API is invoked with the following:
            | Minimum        | 1575.00    |
            | To Date        | 2016-07-04 |
            | From Date      | 2016-04-06 |
            | User Id        | user12345  |
            | Sort code      | 111111     |
            | Account number | 23568495   |
            | Date of Birth  | 1984-07-27 |
        Then The Financial Status API provides the following results:
            | HTTP Status         | 200          |
            | Pass                | true         |
            | Account Holder Name | Shelly Jones |
            | Minimum             | 1575.00      |
            | From Date           | 2016-04-06   |
            | To Date             | 2016-07-04   |
            | Sort code           | 111111       |
            | Account number      | 23568495     |



   ########## PASS - Tier 5 - dependant only (2 applicants) #####

    Scenario: Theresa and David are dependant only (x2) Tier 5 applicants and have sufficient funds

        #Application Raised Date 1st of June
        #She has >= than the threshold of £1260 for the previous 90 days

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




    ################## not pass tier 5 youth mobility scheme applicant#################################################
    Scenario: Aswin is Tier 5 youth mobility scheme applicant and has insufficient financial funds - amount for one month falls below the threshold

    #Application Raised Date 4th of July
    #He has >= than the threshold of £945 for the previous 90 days

       # Given a Service is consuming Financial Status API
        Given the test data for account 23568494
        When the Financial Status API is invoked with the following:
            | Minimum        | 1890.00    |
            | To Date        | 2016-07-04 |
            | From Date      | 2016-04-06 |
            | User Id        | user12345  |
            | Account number | 23568494   |
            | Sort code      | 111111     |
            | Date of Birth  | 1984-07-27 |
        Then The Financial Status API provides the following results:
            | HTTP Status          | 200          |
            | Pass                 | false        |
            | Account Holder Name  | Shelly Smith |
            | Minimum              | 1890.00      |
            | Lowest Balance Date  | 2016-03-10   |
            | Lowest Balance Value | 944.99       |
            | To Date              | 2016-07-04   |
            | From Date            | 2016-04-06   |
            | Sort code            | 111111       |
            | Account number       | 23568494     |
