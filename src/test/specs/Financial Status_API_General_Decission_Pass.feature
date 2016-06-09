Feature: Pass Threshold Calculation - Tier 4 (General) student (single current account and no dependants)

  Requirement to meet Tier 4 pass
  Applicant has the required closing balance every day for a consecutive 28 day period from the date of the Application Raised Date

  Scenario: Shelly is a general student and has sufficient financial funds

  Application Raised Date 1st of June
  She has >= than the threshold of £2350 for the previous 28 days

    Given a Service is consuming Financial Status API
    When the Financial Status API is invoked with the following:
      | Account no              | 01010312   |
      | Sort code               | 12-34-56   |
      | Threshold               | 2530.00    |
      | Application Raised Date | 2016-06-01 |
    #  | Applicant Date of Birth     | Value |  *** Need to confirm with Barclay's ***


    Then The Financial Status API provides the following results:
      | HTTP Status               | 200        |
      | Financial Requirement met | true       |
      | Unique Reference          | value      |
      | Application raised date   | 2016-06-01 |
      | Assessment Start Date     | 2016-05-04 |

  Scenario: Brian is general student and has sufficient financial funds

  Application Raised Date 4th of July
  He has >= than the threshold of £2530 for the previous 28 days


    Given a Service is consuming Financial Status API
    When the Financial Status API is invoked with the following:
      | Account no              | 01078912   |
      | Sort code               | 23-53-68   |
      | Threshold               | 2030.00    |
      | Application Raised Date | 2016-07-01 |
     # | Applicant Date of Birth    |Value | *** Need to confirm with Barclay's ***

    Then The Financial Status API provides the following results:
      | HTTP Status               | 200        |
      | Financial Requirement met | true       |
      | Unique Reference          | value      |
      | Application raised date   | 2016-07-04 |
      | Assessment Start Date     | 2016-06-06 |


