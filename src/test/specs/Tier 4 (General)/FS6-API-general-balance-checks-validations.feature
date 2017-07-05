@wiremock
Feature: Validation of the API fields and data

    Fields mandatory to fill in:
    To Date - Format should be yyyy-mm-dd
    From Date - Format should be yyyy-mm-dd
    Minimum Funds Required - Format should not contain commas or currency symbols
    Sort code - Format should be three pairs of digits 13-56-09 (always numbers 0-9, no letters and cannot be all 0's)
    Account Number - Format should be 12345678 (always 8 numbers, 0-9, no letters, cannot be all 0's)
    Date of birth - should be dd/mm/yyyy (always 8 numbers, 0-9, no letters, cannot be all 0's)

    Background: The API is not provided with End date of 28-day period

        Given a Service is consuming Financial Status API
        When the Financial Status API is invoked with the following:

            | To Date                 | 2017-06-28 |
            | From Date               | 2017-06-01 |
            | Minimum                 | 2350.00    |
            | Sort Code               | 13-56-09   |
            | Account Number          | 23568498   |
            | Date of Birth           | 1984-07-27 |
            | User Id                 | user12345  |


######################### Validation on the Maintenance Period End Date Field #########################

    Scenario: The API is not provided with End date of 28-day period

        Given a Service is consuming Financial Status API
        When the Financial Status API is invoked with the following:
            | To Date                |            |
            | From Date              |            |
            | Minimum                | 2350.00    |
            | Sort Code              | 13-56-09   |
            | Account Number         | 23568498   |
            | Date of Birth          | 1984-07-27 |
            | User Id                | user12345  |
        Then FSPS Tier four general Case Worker tool API provides the following result
            | HTTP Status    | 400                                |
            | Status code    | 0004                               |
            | Status message | Parameter error: Invalid from date |

    Scenario: The API provides incorrect End date of 28-day period - in the future

        Given a Service is consuming Financial Status API
        When the Financial Status API is invoked with the following:
            | To Date                | 2027-06-28 |
            | From Date              | 2027-06-01 |
            | Minimum                | 2350.00    |
            | Sort Code              | 13-56-09   |
            | Account Number         | 23568498   |
            | Date of Birth          | 1984-07-27 |
            | User Id                | user12345  |
        Then FSPS Tier four general Case Worker tool API provides the following result
            | HTTP Status    | 400                                |
            | Status code    | 0004                               |
            | Status message | Parameter error: Invalid from date |

    Scenario: The API is provided with an incorrect to date - not numbers 0-9

        Given a Service is consuming Financial Status API
        When the Financial Status API is invoked with the following:
            | To Date                | 2016-06-28 |
            | From Date              | 201@-06-01 |
            | Minimum                | 2350.00    |
            | Sort Code              | 13-56-09   |
            | Account Number         | 23568498   |
            | Date of Birth          | 1984-07-27 |
            | User Id                | user12345  |
        Then FSPS Tier four general Case Worker tool API provides the following result
            | HTTP Status    | 400                                           |
            | Status code    | 0002                                          |
            | Status message | Parameter conversion error: Invalid from date |

######################### Validation on the Total Funds Required field #########################

    Scenario: The API is not provided with Total Funds Required

        Given a Service is consuming Financial Status API
        When the Financial Status API is invoked with the following:
            | To Date                | 2016-06-28 |
            | From Date              | 2016-06-01 |
            | Minimum                |            |
            | Sort Code              | 13-56-09   |
            | Account Number         | 23568498   |
            | Date of Birth          | 1984-07-27 |
            | User Id                | user12345  |
        Then FSPS Tier four general Case Worker tool API provides the following result
            | HTTP Status    | 400                                        |
            | Status code    | 0004                                       |
            | Status message | Parameter error: Invalid value for minimum |

    Scenario: The API provides incorrect Total Funds Required - just 0

        Given a Service is consuming Financial Status API
        When the Financial Status API is invoked with the following:
            | To Date                | 2016-06-28 |
            | From Date              | 2016-06-01 |
            | Minimum                | 0          |
            | Sort Code              | 13-56-09   |
            | Account Number         | 23568498   |
            | Date of Birth          | 1984-07-27 |
            | User Id                | user12345  |
        Then FSPS Tier four general Case Worker tool API provides the following result
            | HTTP Status    | 400                                        |
            | Status code    | 0004                                       |
            | Status message | Parameter error: Invalid value for minimum |

    Scenario: The API provides incorrect Total Funds Required - not numbers 0-9 (letters)

        Given a Service is consuming Financial Status API
        When the Financial Status API is invoked with the following:
            | To Date                | 2016-06-28 |
            | From Date              | 2016-06-01 |
            | Minimum                | A          |
            | Sort Code              | 13-56-09   |
            | Account Number         | 23568498   |
            | Date of Birth          | 1984-07-27 |
            | User Id                | user12345  |
        Then FSPS Tier four general Case Worker tool API provides the following result
            | HTTP Status    | 400                                                   |
            | Status code    | 0002                                                  |
            | Status message | Parameter conversion error: Invalid value for minimum |

    Scenario: The API provides incorrect Total Funds Required - not numbers 0-9 (negative)

        Given a Service is consuming Financial Status API
        When the Financial Status API is invoked with the following:
            | To Date                | 2016-06-28 |
            | From Date              | 2016-06-01 |
            | Minimum                | -2345.00   |
            | Sort Code              | 13-56-09   |
            | Account Number         | 23568498   |
            | Date of Birth          | 1984-07-27 |
            | User Id                | user12345  |
        Then FSPS Tier four general Case Worker tool API provides the following result
            | HTTP Status    | 400                                        |
            | Status code    | 0004                                       |
            | Status message | Parameter error: Invalid value for minimum |

######################### Validation on the Sort Code Field #########################

  # need to double check if correct
    Scenario: The API is not provided with a Sort code (1)

        Given a Service is consuming Financial Status API
        When the Financial Status API is invoked with the following:
            | To Date                | 2016-06-28 |
            | From Date              | 2016-06-01 |
            | Minimum                | 2345.00    |
            | Sort Code              |            |
            | Account Number         | 23568498   |
            | Date of Birth          | 1984-07-27 |
            | User Id                | user12345  |
        Then FSPS Tier four general Case Worker tool API provides the following result
            | HTTP Status    | 404                                                           |
            | Status code    | 0001                                                          |
            | Status message | Resource not found: Please check URL and parameters are valid |


    Scenario: The API provides incorrect Sort Code - mising digits

        Given a Service is consuming Financial Status API
        When the Financial Status API is invoked with the following:
            | To Date                | 2016-06-28 |
            | From Date              | 2016-06-01 |
            | Minimum                | 2345.00    |
            | Sort Code              | 13-56-0    |
            | Account Number         | 23568498   |
            | Date of Birth          | 1984-07-27 |
            | User Id                | user12345  |
        Then FSPS Tier four general Case Worker tool API provides the following result
            | HTTP Status    | 400                                |
            | Status code    | 0004                               |
            | Status message | Parameter error: Invalid sort code |

    Scenario: The API provides incorrect Sort Code - all 0's

        Given a Service is consuming Financial Status API
        When the Financial Status API is invoked with the following:
            | To Date                | 2016-06-28 |
            | From Date              | 2016-06-01 |
            | Minimum                | 2345.00    |
            | Sort Code              | 00-00-00   |
            | Account Number         | 23568498   |
            | Date of Birth          | 1984-07-27 |
            | User Id                | user12345  |
        Then FSPS Tier four general Case Worker tool API provides the following result
            | HTTP Status    | 400                                |
            | Status code    | 0004                               |
            | Status message | Parameter error: Invalid sort code |

    Scenario: The API provides incorrect Sort Code - not numbers 0-9

        Given a Service is consuming Financial Status API
        When the Financial Status API is invoked with the following:
            | To Date                | 2016-06-28 |
            | From Date              | 2016-06-01 |
            | Minimum                | 2345.00    |
            | Sort Code              | 13-56-0q   |
            | Account Number         | 23568498   |
            | Date of Birth          | 1984-07-27 |
            | User Id                | user12345  |
        Then FSPS Tier four general Case Worker tool API provides the following result
            | HTTP Status    | 404                                                           |
            | Status code    | 0001                                                          |
            | Status message | Resource not found: Please check URL and parameters are valid |

######################### Validation on the Account Number Field #########################

  # need to double check if correct
    Scenario: The API is not provided with Account Number

        Given a Service is consuming Financial Status API
        When the Financial Status API is invoked with the following:
            | To Date                | 2016-06-28 |
            | From Date              | 2016-06-01 |
            | Minimum                | 2345.00    |
            | Sort Code              | 13-56-09   |
            | Account Number         |            |
            | Date of Birth          | 1984-07-27 |
            | User Id                | user12345  |
        Then FSPS Tier four general Case Worker tool API provides the following result
            | HTTP Status    | 404                                                           |
            | Status code    | 0001                                                          |
            | Status message | Resource not found: Please check URL and parameters are valid |


    Scenario: The API is not provided with Account Number - too short

        Given a Service is consuming Financial Status API
        When the Financial Status API is invoked with the following:
            | To Date                | 2016-06-28 |
            | From Date              | 2016-06-01 |
            | Minimum                | 2345.00    |
            | Sort Code              | 13-56-09   |
            | Account Number         | 2356849    |
            | Date of Birth          | 1984-07-27 |
            | User Id                | user12345  |
        Then FSPS Tier four general Case Worker tool API provides the following result
            | HTTP Status    | 400                                     |
            | Status code    | 0004                                    |
            | Status message | Parameter error: Invalid account number |

    Scenario: The API is not provided with Account Number - too long

        Given a Service is consuming Financial Status API
        When the Financial Status API is invoked with the following:
            | To Date                | 2016-06-28 |
            | From Date              | 2016-06-01 |
            | Minimum                | 2345.00    |
            | Sort Code              | 13-56-09   |
            | Account Number         | 235684988  |
            | Date of Birth          | 1984-07-27 |
            | User Id                | user12345  |
        Then FSPS Tier four general Case Worker tool API provides the following result
            | HTTP Status    | 400                                     |
            | Status code    | 0004                                    |
            | Status message | Parameter error: Invalid account number |

    Scenario: The API is not provided with Account Number - all 0's

        Given a Service is consuming Financial Status API
        When the Financial Status API is invoked with the following:
            | To Date                | 2016-06-28 |
            | From Date              | 2016-06-01 |
            | Minimum                | 2345.00    |
            | Sort Code              | 13-56-09   |
            | Account Number         | 00000000   |
            | Date of Birth          | 1984-07-27 |
            | User Id                | user12345  |
        Then FSPS Tier four general Case Worker tool API provides the following result
            | HTTP Status    | 400                                     |
            | Status code    | 0004                                    |
            | Status message | Parameter error: Invalid account number |

    Scenario: The API is not provided with Account Number - not numbers 0-9

        Given a Service is consuming Financial Status API
        When the Financial Status API is invoked with the following:
            | To Date                | 2016-06-28 |
            | From Date              | 2016-06-01 |
            | Minimum                | 2345.00    |
            | Sort Code              | 13-56-09   |
            | Account Number         | 23568a98   |
            | Date of Birth          | 1984-07-27 |
            | User Id                | user12345  |
        Then FSPS Tier four general Case Worker tool API provides the following result
            | HTTP Status    | 404                                                           |
            | Status code    | 0001                                                          |
            | Status message | Resource not found: Please check URL and parameters are valid |


    Scenario: The API is provided with an Account Number that does not exist

        Given a Service is consuming Financial Status API
        When the Financial Status API is invoked with the following:
            | To Date                | 2016-06-28 |
            | From Date              | 2016-06-01 |
            | Minimum                | 2345.00    |
            | Sort Code              | 10-09-08   |
            | Account Number         | 21568198   |
            | Date of Birth          | 1984-07-27 |
            | User Id                | user12345  |
        Then FSPS Tier four general Case Worker tool API provides the following result
            | HTTP Status    | 404                                                         |
            | Status code    | 0007                                                        |
            | Status message | No records for sort code 100908 and account number 21568198 |


        ######################### Validation on the Date of birth Field #########################

    Scenario: The API is not provided with Date of Birth

        Given a Service is consuming Financial Status API
        When the Financial Status API is invoked with the following:
            | To Date                | 2016-06-28 |
            | From Date              | 2016-06-01 |
            | Minimum                | 2350.00    |
            | Sort Code              | 13-56-09   |
            | Account Number         | 23568498   |
            | Date of Birth          |            |
            | User Id                | user12345  |
        Then FSPS Tier four general Case Worker tool API provides the following result
            | HTTP Status    | 400                                    |
            | Status code    | 0004                                   |
            | Status message | Parameter error: Invalid date of birth |

    Scenario: The API provides incorrect Date of birth - in the future

        Given a Service is consuming Financial Status API
        When the Financial Status API is invoked with the following:
            | To Date                | 2016-06-28 |
            | From Date              | 2016-06-01 |
            | Minimum                | 2350.00    |
            | Sort Code              | 13-56-09   |
            | Account Number         | 23568498   |
            | Date of Birth          | 2019-01-15 |
            | User Id                | user12345  |
        Then FSPS Tier four general Case Worker tool API provides the following result
            | HTTP Status    | 400                                    |
            | Status code    | 0004                                   |
            | Status message | Parameter error: Invalid date of birth |

    Scenario: The API is provided with an incorrect Date of birth - not numbers 0-9

        Given a Service is consuming Financial Status API
        When the Financial Status API is invoked with the following:
            | To Date                | 2016-06-28 |
            | From Date              | 2016-06-01 |
            | Minimum                | 2350.00    |
            | Sort Code              | 13-56-09   |
            | Account Number         | 23568498   |
            | Date of Birth          | 1984-01-1@ |
            | User Id                | user12345  |
        Then FSPS Tier four general Case Worker tool API provides the following result
            | HTTP Status    | 400                                     |
            | Status code    | 0002                                    |
            | Status message | Parameter conversion error: Invalid dob |
