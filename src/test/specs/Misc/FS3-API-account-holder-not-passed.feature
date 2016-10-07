Feature:  Not Pass - Account holder consent does not exist


    If false, API will
#Added to Jira PT-28 - Add 'Account holder name' to FSPS API
    Scenario: Shelly is a doctorate in London student and does not have sufficient financial funds

    Application Raised Date 1st of June
    She has < than the Total funds required of £2350 for the previous 28 days

        Given a Service is consuming Financial Status API
        Given the test data for account 23568499
        When the Financial Status API is invoked with the following:
            | To date                | 2016-06-01 |
            | From date              | 2016-05-05 |
            | Minimum                | 2530.00    |
            | Sort code              | 135610     |
            | Account number         | 23568499   |
            | Date of Birth          | 1984-07-27 |
            | User Id                | user12345  |
            | Account Holder Consent | true       |
        Then The Financial Status API provides the following results:
            | HTTP Status          | 200          |
            | Pass                 | false        |
            | Account Holder Name  | Shelly Smith |
            | Minimum              | 2530.00      |
            | From date            | 2016-05-05   |
            | To date              | 2016-06-01   |
            | Lowest Balance Date  | 2016-05-30   |
            | Lowest Balance Value | 2429.99      |
            | sort code            | 135610       |
            | Account number       | 23568499     |

#Added to Jira PT-28 - Add 'Account holder name' to FSPS API
    Scenario: Brian is doctorate out of London student and does not have sufficient financial funds

    Application Raised Date 1st of July
    He has < than the Total Funds Required of £2030 for the previous 28 days

        Given a Service is consuming Financial Status API
        Given the test data for account 01078913
        When the Financial Status API is invoked with the following:
            | To date                | 2016-07-01 |
            | From date              | 2016-06-04 |
            | Minimum                | 2030.00    |
            | Sort code              | 149303     |
            | Account number         | 01078912   |
            | Account number         | 01078913   |
            | Date of Birth          | 1984-07-27 |
            | User Id                | user12346  |
            | Account Holder Consent | true       |

        Then The Financial Status API provides the following results:
            | HTTP Status          | 200         |
            | Pass                 | false       |
            | Account Holder Name  | Brian Kosac |
            | Minimum              | 2030.00     |
            | From date            | 2016-06-04  |
            | To date              | 2016-07-01  |
            | Lowest Balance Date  | 2016-06-27  |
            | Lowest Balance Value | 2029.99     |
            | Sort code            | 149303      |
            | Account number       | 01078913    |

#Added to Jira PT-28 - Add 'Account holder name' to FSPS API
    Scenario: David is general student and does not have sufficient financial funds

    Application Raised Date 4th of July
    He has < than the Total Funds Required of £2537.48 for the previous 28 days


        Given a Service is consuming Financial Status API
        Given the test data for account 17926768
        When the Financial Status API is invoked with the following:
            | To date                | 2016-07-04 |
            | From date              | 2016-06-07 |
            | Minimum                | 2537.48    |
            | Sort code              | 139302     |
            | Account number         | 17926768   |
            | Date of Birth          | 1984-07-27 |
            | User Id                | user12347  |
            | Account Holder Consent | true       |
        Then The Financial Status API provides the following results:
            | HTTP Status          | 200        |
            | Pass                 | false      |
            | Account Holder Name  | David Wash |
            | Minimum              | 2537.48    |
            | From date            | 2016-06-07 |
            | To date              | 2016-07-04 |
            | Lowest Balance Date  | 2016-06-07 |
            | Lowest Balance Value | 2537.00    |
            | Sort code            | 139302     |
            | Account number       | 17926768   |
