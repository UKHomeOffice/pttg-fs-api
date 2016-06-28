Feature:

  Scenario: Shelly is a general student and does not have sufficient financial funds

  Application Raised Date 1st of June
  She has < than the Total Fund Required of £2350 for the previous 28 days


    Given a Service is consuming Financial Status API
    When the Financial Status API is invoked with the following:
      | To Date        | 2016-06-01 |
      | From Date      | 2016-05-05 |
      | Minimum        | 2530.00    |
      | Sort code      | 135609     |
      | Account number | 23568498   |
    #  | Applicant Date of Birth     | Value |  *** Need to confirm with Barclay's ***


    Then The Financial Status API provides the following results:
      | HTTP Status    | 200        |
      | Pass           | false      |
      | Minimum        | 2530.00    |
      | From Date      | 2016-05-05 |
      | To Date        | 2016-06-01 |
      | Sort code      | 135609     |
      | Account number | 23568498   |


  Scenario: Brian is general student and does not have sufficient financial funds

  Application Raised Date 4th of July
  He has < than the Total Funds Required of £2030 for the previous 28 days

    Given a Service is consuming Financial Status API
    When the Financial Status API is invoked with the following:
      | To Date        | 2016-07-01 |
      | From Date      | 2016-06-04 |
      | Minimum        | 2030.00    |
      | Sort code      | 149302     |
      | Account number | 01078911   |

     # | Applicant Date of Birth    |Value | *** Need to confirm with Barclay's ***

    Then The Financial Status API provides the following results:
      | HTTP Status    | 200        |
      | Pass           | false      |
      | Minimum        | 2030.00    |
      | From Date      | 2016-06-04 |
      | To Date        | 2016-07-01 |
      | Sort code      | 149302     |
      | Account number | 01078911   |


  Scenario: David is general student and does not have sufficient financial funds

  Application Raised Date 4th of July
  He has < than the Total Funds Required of £2537.48 for the previous 28 days


    Given a Service is consuming Financial Status API
    When the Financial Status API is invoked with the following:
      | To Date        | 2016-07-01 |
      | From Date      | 2016-06-04 |
      | Minimum        | 2537.48    |
      | Sort code      | 139302     |
      | Account number | 17926767   |

     # | Applicant Date of Birth    |Value | *** Need to confirm with Barclay's ***

    Then The Financial Status API provides the following results:
      | HTTP Status    | 200        |
      | Pass           | false      |
      | Minimum        | 2537.48    |
      | From Date      | 2016-06-04 |
      | To Date        | 2016-07-01 |
      | Sort code      | 139302     |
      | Account number | 17926767   |


    Scenario: Shelly is a general student and has sufficient financial funds

    Application Raised Date 1st of June
    She has >= than the threshold of £2350 for the previous 28 days

        Given a Service is consuming Financial Status API
      #Given the test data for account 11111111
        When the Financial Status API is invoked with the following:
            | Account number | 01010312   |
            | Sort code      | 12-34-56   |
            | Minimum        | 2530.00    |
            | To Date        | 2016-06-01 |
            | From Date      | 2016-05-05 |
    #  | Applicant Date of Birth     | Value |  *** Need to confirm with Barclay's ***


        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Pass           | true       |
            | Minimum        | 2530.00    |
           # | Unique Reference        | value      |
            | To Date        | 2016-06-01 |
            | From Date      | 2016-05-05 |
            | Account number | 01010312   |
            | Sort code      | 123456     |

    Scenario: Brian is general student and has sufficient financial funds

    Application Raised Date 4th of July
    He has >= than the threshold of £2530 for the previous 28 days


        Given a Service is consuming Financial Status API
        When the Financial Status API is invoked with the following:
            | Account number | 01078912   |
            | Sort code      | 23-53-68   |
            | Minimum        | 2030.00    |
            | To Date        | 2016-07-01 |
            | From Date      | 2016-06-04 |
       # | Applicant Date of Birth    |Value | *** Need to confirm with Barclay's ***

        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Pass           | true       |
            | Minimum        | 2030.00    |
           # | Unique Reference        | value      |
            | To Date        | 2016-07-01 |
            | From Date      | 2016-06-04 |
            | Account number | 01078912   |
            | Sort code      | 235368     |



    Scenario: The API is not provided with End date of 28-day period

        Given a Service is consuming Financial Status API
        When the Financial Status API is invoked with the following:
            | To Date        | 2016-06-01 |
            | From Date      |            |
            | Minimum        | 2350.00    |
            | Sort Code      | 13-56-09   |
            | Account Number | 23568498   |
        Then FSPS Tier four general Case Worker tool API provides the following result
            | HTTP Status    | 400                                |
            | Status code    | 0000                               |
            | Status message | Parameter error: Invalid from date |

    Scenario: The API provides incorrect End date of 28-day period - in the future

        Given a Service is consuming Financial Status API
        When the Financial Status API is invoked with the following:
            | To Date        | 2016-06-01 |
            | From Date      | 2016-07-37 |
            | Minimum        | 2350.00    |
            | Sort Code      | 13-56-09   |
            | Account Number | 23568498   |
        Then FSPS Tier four general Case Worker tool API provides the following result
            | HTTP Status    | 400                                |
            | Status code    | 0000                               |
            | Status message | Parameter error: Invalid from date |

    Scenario: The API is provided with an incorrect to date - not numbers 0-9

        Given a Service is consuming Financial Status API
        When the Financial Status API is invoked with the following:
            | To Date        | 01/0d/2016 |
            | From Date      | 2016-07-27 |
            | Minimum        | 2350.00    |
            | Sort Code      | 13-56-09   |
            | Account Number | 23568498   |
        Then FSPS Tier four general Case Worker tool API provides the following result
            | HTTP Status    | 400                              |
            | Status code    | 0000                             |
            | Status message | Parameter error: Invalid to date |

######################### Validation on the Total Funds Required field #########################

    Scenario: The API is not provided with Total Funds Required

        Given a Service is consuming Financial Status API
        When the Financial Status API is invoked with the following:
            | To Date        | 2016-06-01 |
            | From Date      | 2016-05-05 |
            | Minimum        |            |
            | Sort Code      | 13-56-09   |
            | Account Number | 23568498   |
        Then FSPS Tier four general Case Worker tool API provides the following result
            | HTTP Status    | 400                                           |
            | Status code    | 0000                                          |
            | Status message | Parameter error: Invalid value for minimum |

    Scenario: The API provides incorrect Total Funds Required - just 0

        Given a Service is consuming Financial Status API
        When the Financial Status API is invoked with the following:
            | To Date        | 2016-06-01 |
            | From Date      | 2016-05-05 |
            | Minimum        | 0          |
            | Sort Code      | 13-56-09   |
            | Account Number | 23568498   |
        Then FSPS Tier four general Case Worker tool API provides the following result
            | HTTP Status    | 400                                           |
            | Status code    | 0000                                          |
            | Status message | Parameter error: Invalid value for minimum |

    Scenario: The API provides incorrect Total Funds Required - not numbers 0-9 (letters)

        Given a Service is consuming Financial Status API
        When the Financial Status API is invoked with the following:
            | To Date        | 2016-06-01 |
            | From Date      | 2016-05-05 |
            | Minimum        | A          |
            | Sort Code      | 13-56-09   |
            | Account Number | 23568498   |
        Then FSPS Tier four general Case Worker tool API provides the following result
            | HTTP Status    | 400                                        |
            | Status code    | 0000                                       |
            | Status message | Parameter error: Invalid value for minimum |

    Scenario: The API provides incorrect Total Funds Required - not numbers 0-9 (negative)

        Given a Service is consuming Financial Status API
        When the Financial Status API is invoked with the following:
            | To Date        | 2016-06-01 |
            | From Date      | 2016-05-05 |
            | Minimum        | -2345.00   |
            | Sort Code      | 13-56-09   |
            | Account Number | 23568498   |
        Then FSPS Tier four general Case Worker tool API provides the following result
            | HTTP Status    | 400                                           |
            | Status code    | 0000                                          |
            | Status message | Parameter error: Invalid value for minimum |

######################### Validation on the Sort Code Field #########################

  # need to double check if correct
    Scenario: The API is not provided with a Sort code (1)

        Given a Service is consuming Financial Status API
        When the Financial Status API is invoked with the following:
            | To Date        | 2016-06-01 |
            | From Date      | 2016-05-05 |
            | Minimum        | 2345.00    |
            | Sort Code      |            |
            | Account Number | 23568498   |
        Then FSPS Tier four general Case Worker tool API provides the following result
            | HTTP Status    | 404                                                                                                                                                     |
            | Status code    | 0000                                                                                                                                                    |
            | Status message | Resource not found: Please check the sort code and account number are valid values/pttg/financialstatusservice/v1/accounts//23568498/dailybalancestatus |


    Scenario: The API provides incorrect Sort Code - mising digits

        Given a Service is consuming Financial Status API
        When the Financial Status API is invoked with the following:
            | To Date        | 2016-06-01 |
            | From Date      | 2016-05-05 |
            | Minimum        | 2345.00    |
            | Sort Code      | 13-56-0    |
            | Account Number | 23568498   |
        Then FSPS Tier four general Case Worker tool API provides the following result
            | HTTP Status    | 400                                |
            | Status code    | 0000                               |
            | Status message | Parameter error: Invalid sort code |

    Scenario: The API provides incorrect Sort Code - all 0's

        Given a Service is consuming Financial Status API
        When the Financial Status API is invoked with the following:
            | To Date        | 2016-06-01 |
            | From Date      | 2016-05-05 |
            | Minimum        | 2345.00    |
            | Sort Code      | 00-00-00   |
            | Account Number | 23568498   |
        Then FSPS Tier four general Case Worker tool API provides the following result
            | HTTP Status    | 404                                                         |
            | Status code    | 0000                                                        |
            | Status message | No records for sort code 000000 and account number 23568498 |

    Scenario: The API provides incorrect Sort Code - not numbers 0-9

        Given a Service is consuming Financial Status API
        When the Financial Status API is invoked with the following:
            | To Date        | 2016-06-01 |
            | From Date      | 2016-05-05 |
            | Minimum        | 2345.00    |
            | Sort Code      | 13-56-0q   |
            | Account Number | 23568498   |
        Then FSPS Tier four general Case Worker tool API provides the following result
            | HTTP Status    | 404                                |
            | Status code    | 0000                               |
            | Status message | Resource not found: Please check the sort code and account number are valid values/pttg/financialstatusservice/v1/accounts/13-56-0q/23568498/dailybalancestatus |

######################### Validation on the Account Number Field #########################

  # need to double check if correct
    Scenario: The API is not provided with Account Number

        Given a Service is consuming Financial Status API
        When the Financial Status API is invoked with the following:
            | To Date        | 2016-06-01 |
            | From Date      | 2016-05-05 |
            | Minimum        | 2345.00    |
            | Sort Code      | 13-56-09   |
            | Account Number |            |
        Then FSPS Tier four general Case Worker tool API provides the following result
            | HTTP Status    | 404                                                                                                                                                     |
            | Status code    | 0000                                                                                                                                                    |
            | Status message | Resource not found: Please check the sort code and account number are valid values/pttg/financialstatusservice/v1/accounts/13-56-09//dailybalancestatus |


    Scenario: The API is not provided with Account Number - too short

        Given a Service is consuming Financial Status API
        When the Financial Status API is invoked with the following:
            | To Date        | 2016-06-01 |
            | From Date      | 2016-05-05 |
            | Minimum        | 2345.00    |
            | Sort Code      | 13-56-09   |
            | Account Number | 2356849    |
        Then FSPS Tier four general Case Worker tool API provides the following result
            | HTTP Status    | 400                                     |
            | Status code    | 0000                                    |
            | Status message | Parameter error: Invalid account number |

    Scenario: The API is not provided with Account Number - too long

        Given a Service is consuming Financial Status API
        When the Financial Status API is invoked with the following:
            | To Date        | 2016-06-01 |
            | From Date      | 2016-05-05 |
            | Minimum        | 2345.00    |
            | Sort Code      | 13-56-09   |
            | Account Number | 235684988  |
        Then FSPS Tier four general Case Worker tool API provides the following result
            | HTTP Status    | 400                                     |
            | Status code    | 0000                                    |
            | Status message | Parameter error: Invalid account number |

    Scenario: The API is not provided with Account Number - all 0's

        Given a Service is consuming Financial Status API
        When the Financial Status API is invoked with the following:
            | To Date        | 2016-06-01 |
            | From Date      | 2016-05-05 |
            | Minimum        | 2345.00    |
            | Sort Code      | 13-56-09   |
            | Account Number | 00000000   |
        Then FSPS Tier four general Case Worker tool API provides the following result
            | HTTP Status    | 404                                                         |
            | Status code    | 0000                                                        |
            | Status message | No records for sort code 135609 and account number 00000000 |

    Scenario: The API is not provided with Account Number - not numbers 0-9

        Given a Service is consuming Financial Status API
        When the Financial Status API is invoked with the following:
            | To Date        | 2016-06-01 |
            | From Date      | 2016-05-05 |
            | Minimum        | 2345.00    |
            | Sort Code      | 13-56-09   |
            | Account Number | 23568a98    |
        Then FSPS Tier four general Case Worker tool API provides the following result
            | HTTP Status    | 404                                     |
            | Status code    | 0000                                    |
            | Status message | Resource not found: Please check the sort code and account number are valid values/pttg/financialstatusservice/v1/accounts/13-56-09/23568a98/dailybalancestatus |


    Scenario: The API is provided with an Account Number that does not exist

        Given a Service is consuming Financial Status API
        When the Financial Status API is invoked with the following:
            | To Date        | 2016-06-01 |
            | From Date      | 2016-05-05 |
            | Minimum        | 2345.00    |
            | Sort Code      | 10-09-08   |
            | Account Number | 21568198    |
        Then FSPS Tier four general Case Worker tool API provides the following result
            | HTTP Status    | 404                                     |
            | Status code    | 0000                                    |
            | Status message | No records for sort code 100908 and account number 21568198 |
