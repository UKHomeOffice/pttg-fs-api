Feature: Pass & Not Pass - Total Funds Required Calculation - Tier 2 & 5 General applicant with & without dependants (single current account)

    Requirement to meet Tier 2 & 5 pass

    Applicant has required closing balance every day for a consecutive 90 day period from the date of the Maintenance End Date

    Background:
    | Sort code      | 111111     |
    | Account number | 12345678   |
    | Date of Birth  | 1984-07-27 |


    ##### not pass #####

    Scenario: Sam is a Tier 2 applicant and does not have sufficient funds.

    Application Raised Date 1st of June
    He has < than the Total Fund Required of £945 for the previous 90 days

        Given a Service is consuming Financial Status API
        Given the test data for account 23568498
        When the Financial Status API is invoked with the following:
            | Minimum       | 945.00     |
            | To Date       | 2016-06-01 |
            | From Date     | 2016-03-04 |
            | User Id       | user12345  |
        Then The Financial Status API provides the following results:
            | HTTP Status          | 200            |
            | Pass                 | false          |
            | Account Holder Name  | Sam Hutchinson |
            | Minimum              | 945.00         |
            | From Date            | 2016-03-02     |
            | Lowest Balance Date  | 2016-05-30     |
            | Lowest Balance Value | 949.99         |
            | To Date              | 2016-06-01     |
            | Sort code            | 111111         |
            | Account number       | 12345678       |

    Scenario: Barry is a Tier 5 applicant and does not have sufficient financial funds

    Application Raised Date 4th of July
    He has < than the Total Funds Required of £1575 for the previous 90 days

        Given a Service is consuming Financial Status API
        Given the test data for account 01078911
        When the Financial Status API is invoked with the following:
            | Minimum       | 2030.00    |
            | To Date       | 2016-07-04 |
            | From Date     | 2016-04-06 |
            | User Id       | user12345  |
        Then The Financial Status API provides the following results:
            | HTTP Status          | 200          |
            | Pass                 | false        |
            | Account Holder Name  | Barry Bannan |
            | Minimum              | 1575.00      |
            | From Date            | 2016-04-06   |
            | Lowest Balance Date  | 2016-06-05   |
            | Lowest Balance Value | -1575.99     |
            | To Date              | 2016-07-04   |
            | Sort code            | 111111       |
            | Account number       | 12345678     |

    Scenario: Jacques is a Tier 5 applicant and does not have sufficient financial funds

    Application Raised Date 4th of July
    He has < than the Total Funds Required of £945.00 for the previous 90 days

        Given a Service is consuming Financial Status API
        Given the test data for account 17926767
        When the Financial Status API is invoked with the following:
            | Minimum       | 2537.48    |
            | To Date       | 2016-07-04 |
            | From Date     | 2016-04-06 |
            | User Id       | user12345  |
        Then The Financial Status API provides the following results:
            | HTTP Status          | 200             |
            | Pass                 | false           |
            | Account Holder Name  | Jacques Maghoma |
            | Minimum              | 945.00          |
            | From Date            | 2016-04-06      |
            | Lowest Balance Date  | 2016-06-20      |
            | Lowest Balance Value | 940.47          |
            | To Date              | 2016-07-04      |
            | Sort code            | 111111          |
            | Account number       | 12345678        |

    Scenario: Lucas is a Tier 2 applicant - funds have been in her account for less than 90 days

    Application Raised Date 1st of June
    She has < than the Total Fund Required of £2205 for the previous 90 days

        Given a Service is consuming Financial Status API
        Given the test data for account 23568491
        When the Financial Status API is invoked with the following:
            | Minimum       | 2205.00    |
            | To Date       | 2016-06-01 |
            | From Date     | 2016-03-04 |
            | User Id       | user12345  |
        Then The Financial Status API provides the following results:
            | HTTP Status         | 200        |
            | Pass                | false      |
            | Account Holder Name | Lucas Joao |
            | Minimum             | 2205.00    |
            | From Date           | 2016-03-04 |
            | Record Count        | 89         |
            | To Date             | 2016-06-01 |
            | Sort code           | 111111     |
            | Account number      | 12345678   |

    ##### pass #####

    Scenario: Shelly is a Tier 5 applicant and has sufficient financial funds

    Application Raised Date 1st of June
    She has >= than the threshold of £1575 for the previous 90 days

        Given a Service is consuming Financial Status API
        Given the test data for account 01010312
        When the Financial Status API is invoked with the following:
            | Minimum       | 1575.00    |
            | To Date       | 2016-06-01 |
            | From Date     | 2016-03-04 |
            | User Id       | user12345  |
        Then The Financial Status API provides the following results:
            | HTTP Status         | 200          |
            | Pass                | true         |
            | Account Holder Name | Shelly Smith |
            | Minimum             | 1575.00      |
           # | Unique Reference        | value      |
            | To Date             | 2016-06-01   |
            | From Date           | 2016-03-04   |
            | Sort code           | 111111       |
            | Account number      | 12345678     |

    Scenario: Brian is Tier 2 applicant and has sufficient financial funds

    Application Raised Date 4th of July
    He has >= than the threshold of £945 for the previous 90 days

        Given a Service is consuming Financial Status API
        Given the test data for account 01078912
        When the Financial Status API is invoked with the following:
            | Minimum       | 945.00     |
            | To Date       | 2016-07-04 |
            | From Date     | 2016-04-06 |
            | User Id       | user12345  |
        Then The Financial Status API provides the following results:
            | HTTP Status         | 200         |
            | Pass                | true        |
            | Account Holder Name | Brian Chang |
            | Minimum             | 945.00      |
           # | Unique Reference        | value      |
            | To Date             | 2016-07-04  |
            | From Date           | 2016-04-06  |
            | Sort code           | 111111      |
            | Account number      | 12345678    |
