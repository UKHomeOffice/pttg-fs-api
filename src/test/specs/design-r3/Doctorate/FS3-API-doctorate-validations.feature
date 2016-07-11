Feature: Validation of the API fields and data

  Acceptance criteria

  The same validations for in and out of London

  Inner London borough - Yes or No options (mandatory)
  Course length - 1-2 months (mandatory)
  Accommodation fees already paid - numbers only. Highest amount £1,265. Format should not contain commas or currency symbols
  To date - format should be yyyy-mm-dd (mandatory)
  From date - format should be yyyy-mm-dd (mandatory)
  Sort code - format should be three pairs of digits 13-56-09 (always numbers 0-9, no letters and cannot be all 0's) (mandatory)
  Account number - format should be 12345678 (always 8 numbers, 0-9, no letters, cannot be all 0's) (mandatory)

######################### Validation on the End of 28-day period #########################

  Scenario: The API is not provided with End date of 28-day period

    Given a Service is consuming Financial Status API
    When the Financial Status API is invoked with the following:
      | To Date        | 2016-06-01 |
      | From Date      |            |
      ## | Minimum        | 2350.00    |
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
      | HTTP Status    | 404                                                                                                                                                             |
      | Status code    | 0000                                                                                                                                                            |
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
      | Account Number | 23568a98   |
    Then FSPS Tier four general Case Worker tool API provides the following result
      | HTTP Status    | 404                                                                                                                                                             |
      | Status code    | 0000                                                                                                                                                            |
      | Status message | Resource not found: Please check the sort code and account number are valid values/pttg/financialstatusservice/v1/accounts/13-56-09/23568a98/dailybalancestatus |


  Scenario: The API is provided with an Account Number that does not exist

    Given a Service is consuming Financial Status API
    When the Financial Status API is invoked with the following:
      | To Date        | 2016-06-01 |
      | From Date      | 2016-05-05 |
      | Minimum        | 2345.00    |
      | Sort Code      | 10-09-08   |
      | Account Number | 21568198   |
    Then FSPS Tier four general Case Worker tool API provides the following result
      | HTTP Status    | 404                                                         |
      | Status code    | 0000                                                        |
      | Status message | No records for sort code 100908 and account number 21568198 |

      ######################### Validation on the Inner London borough Field #########################

  Scenario: The API is not provided with Inner London borough Yes or No field
    Given a Service is consuming Financial Status API
    When the Financial Status API is invoked with the following:
      | The end of 28-day period        | 20/06/2016 |
      | Inner London borough            |            |
      | Course length                   | 2          |
      | Accommodation fees already paid | 250.50     |
      | To Date                         | 23/05/2016 |
      | From Date                       | 20/06/2016 |
      | Sort code                       | 14-00-11   |
      | Account number                  | 24568599   |
    Then FSPS Tier four general Case Worker tool API provides the following result
      | HTTP Status    | 400                                           |
      | Status code    | 0000                                          |
      | Status message | Parameter error: Invalid Inner London borough |

######################### Validation on the Remaining course length Field #########################

  Scenario: The API is not provided with the Course length
    Given a Service is consuming Financial Status API
    When the Financial Status API is invoked with the following:
      | The end of 28-day period        | 20/06/2016 |
      | Inner London borough            | Yes        |
      | Course length                   |            |
      | Accommodation fees already paid | 250.50     |
      | To Date                         | 23/05/2016 |
      | From Date                       | 20/06/2016 |
      | Sort code                       | 14-00-11   |
      | Account number                  | 24568599   |
    Then FSPS Tier four general Case Worker tool API provides the following result
      | HTTP Status    | 400                                    |
      | Status code    | 0000                                   |
      | Status message | Parameter error: Invalid Course length |

  Scenario: The API is provided with incorrect Course Length - not numbers 1-2
    Given a Service is consuming Financial Status API
    When the Financial Status API is invoked with the following:
      | The end of 28-day period        | 20/06/2016 |
      | Inner London borough            | Yes        |
      | Course length                   | a          |
      | Accommodation fees already paid | 250.50     |
      | To Date                         | 23/05/2016 |
      | From Date                       | 20/06/2016 |
      | Minimum                         | 2350.00    |
      | Sort code                       | 14-00-11   |
      | Account number                  | 24568599   |
    Then FSPS Tier four general Case Worker tool API provides the following result
      | HTTP Status    | 400                                    |
      | Status code    | 0000                                   |
      | Status message | Parameter error: Invalid Course length |

  Scenario: The API is provided with incorrect Course Length - more than 2
    Given caseworker is using the financial status service ui
    When the financial status check is performed with
      | The end of 28-day period        | 20/06/2016 |
      | Inner London borough            | Yes        |
      | Course length                   | 4          |
      | Accommodation fees already paid | 250.50     |
      | To Date                         | 23/05/2016 |
      | From Date                       | 20/06/2016 |
      | Minimum                         | 2350.00    |
      | Sort code                       | 14-00-11   |
      | Account number                  | 24568599   |
    Then FSPS Tier four general Case Worker tool API provides the following result
      | HTTP Status    | 400                                    |
      | Status code    | 0000                                   |
      | Status message | Parameter error: Invalid Course length |


######################## Validation on the Accommodation fees already paid Field #########################

  Scenario: The API is not provided with Accommodation fees already paid
    Given caseworker is using the financial status service ui
    When the financial status check is performed with
      | The end of 28-day period        | 20/06/2016 |
      | Inner London borough            | Yes        |
      | Course length                   | 1          |
      | Accommodation fees already paid | 250.50     |
      | To Date                         | 23/05/2016 |
      | From Date                       | 20/06/2016 |
      | Minimum                         | 2350.00    |
      | Sort code                       | 14-00-11   |
      | Account number                  | 24568599   |
    Then FSPS Tier four general Case Worker tool API provides the following result
      | HTTP Status    | 400                                                      |
      | Status code    | 0000                                                     |
      | Status message | Parameter error: Invalid Accommodation fees already paid |

  Scenario: The API is provided with incorrect  Accommodation fees already paid - not numbers 0-9
    Given caseworker is using the financial status service ui
    When the financial status check is performed with
      | The end of 28-day period        | 20/06/2016 |
      | Inner London borough            | Yes        |
      | Course length                   | -          |
      | Accommodation fees already paid | 250.50     |
      | To Date                         | 23/05/2016 |
      | From Date                       | 20/06/2016 |
      | Minimum                         | 2350.00    |
      | Sort code                       | 14-00-11   |
      | Account number                  | 24568599   |
    Then FSPS Tier four general Case Worker tool API provides the following result
      | HTTP Status    | 400                                                      |
      | Status code    | 0000                                                     |
      | Status message | Parameter error: Invalid Accommodation fees already paid |

  Scenario: The API is provided with incorrect  Accommodation fees already paid - >£1,265
    Given caseworker is using the financial status service ui
    When the financial status check is performed with
      | The end of 28-day period        | 20/06/2016 |
      | Inner London borough            | Yes        |
      | Course length                   | -          |
      | Accommodation fees already paid | 250.50     |
      | To Date                         | 23/05/2016 |
      | From Date                       | 20/06/2016 |
      | Minimum                         | 2350.00    |
      | Sort code                       | 14-00-11   |
      | Account number                  | 24568599   |
    Then FSPS Tier four general Case Worker tool API provides the following result
      | HTTP Status    | 400                                                      |
      | Status code    | 0000                                                     |
      | Status message | Parameter error: Invalid Accommodation fees already paid |