@wiremock
Feature: Validation of the API fields and data

    Fields mandatory to fill in:
    To Date - Format should be yyyy-mm-dd
    From Date - Format should be yyyy-mm-dd
    Minimum Funds Required - Format should not contain commas or currency symbols
    Sort code - Format should be three pairs of digits 13-56-09 (always numbers 0-9, no letters and cannot be all 0's)
    Account Number - Format should be 12345678 (always 8 numbers, 0-9, no letters, cannot be all 0's)
    Date of birth - should be yyyy-mm-dd (always 8 numbers, 0-9, no letters, cannot be all 0's)

    Background:

        Given a Service is consuming Financial Status API
        When the Financial Status API is invoked with the following:

            | To Date        | 2016-07-04 |
            | From Date      | 2016-04-06 |
            | Minimum        | 945.00     |
            | Sort Code      | 13-56-09   |
            | Account Number | 23568495   |
            | Date of Birth  | 1984-07-27 |
            | User Id        | user12345  |

######################### Validation on the Maintenance Period End Date Field #########################

    Scenario: The API is not provided with End date of 90-day period

        Given a Service is consuming Financial Status API
        When the Financial Status API is invoked with the following:
            | To Date        |            |
            | From Date      |            |
        Then FSPS Tier four general Case Worker tool API provides the following result
            | HTTP Status    | 400                                |
            | Status code    | 0004                               |
            | Status message | Parameter error: Invalid from date |

    Scenario: The API provides incorrect End date of 90-day period - in the future

        Given a Service is consuming Financial Status API
        When the Financial Status API is invoked with the following:
            | To Date        | 2027-08-30 |
            | From Date      | 2027-04-06 |
        Then FSPS Tier four general Case Worker tool API provides the following result
            | HTTP Status    | 400                                |
            | Status code    | 0004                               |
            | Status message | Parameter error: Invalid from date |

    Scenario: The API is provided with an incorrect to date - not numbers 0-9

        Given a Service is consuming Financial Status API
        When the Financial Status API is invoked with the following:
            | To Date        | 2016-06-28 |
            | From Date      | 201@-06-01 |
        Then FSPS Tier four general Case Worker tool API provides the following result
            | HTTP Status    | 400                                           |
            | Status code    | 0002                                          |
            | Status message | Parameter conversion error: Invalid from date |

######################### Validation on the Total Funds Required field #########################

    Scenario: The API is not provided with Total Funds Required

        Given a Service is consuming Financial Status API
        When the Financial Status API is invoked with the following:
            | Minimum        |            |
        Then FSPS Tier four general Case Worker tool API provides the following result
            | HTTP Status    | 400                                        |
            | Status code    | 0004                                       |
            | Status message | Parameter error: Invalid value for minimum |

    Scenario: The API provides incorrect Total Funds Required - just 0

        Given a Service is consuming Financial Status API
        When the Financial Status API is invoked with the following:
            | Minimum        | 0          |
            | Sort Code      | 13-56-09   |
        Then FSPS Tier four general Case Worker tool API provides the following result
            | HTTP Status    | 400                                        |
            | Status code    | 0004                                       |
            | Status message | Parameter error: Invalid value for minimum |

    Scenario: The API provides incorrect Total Funds Required - not numbers 0-9 (letters)

        Given a Service is consuming Financial Status API
        When the Financial Status API is invoked with the following:
            | Minimum        | A          |
        Then FSPS Tier four general Case Worker tool API provides the following result
            | HTTP Status    | 400                                                   |
            | Status code    | 0002                                                  |
            | Status message | Parameter conversion error: Invalid value for minimum |

    Scenario: The API provides incorrect Total Funds Required - not numbers 0-9 (negative)

        Given a Service is consuming Financial Status API
        When the Financial Status API is invoked with the following:
            | Minimum        | -2345.00   |
            | Sort Code      | 13-56-09   |
        Then FSPS Tier four general Case Worker tool API provides the following result
            | HTTP Status    | 400                                        |
            | Status code    | 0004                                       |
            | Status message | Parameter error: Invalid value for minimum |

######################### Validation on the Sort Code Field #########################

    Scenario: The API is not provided with a Sort code (1)

        Given a Service is consuming Financial Status API
        When the Financial Status API is invoked with the following:
            | Sort Code      |            |
        Then FSPS Tier four general Case Worker tool API provides the following result
            | HTTP Status    | 404                                                           |
            | Status code    | 0001                                                          |
            | Status message | Resource not found: Please check URL and parameters are valid |

    Scenario: The API provides incorrect Sort Code - mising digits

        Given a Service is consuming Financial Status API
        When the Financial Status API is invoked with the following:
            | Sort Code      | 13-56-0    |
        Then FSPS Tier four general Case Worker tool API provides the following result
            | HTTP Status    | 400                                |
            | Status code    | 0004                               |
            | Status message | Parameter error: Invalid sort code |

    Scenario: The API provides incorrect Sort Code - all 0's

        Given a Service is consuming Financial Status API
        When the Financial Status API is invoked with the following:
            | Sort Code      | 00-00-00   |
        Then FSPS Tier four general Case Worker tool API provides the following result
            | HTTP Status    | 400                                |
            | Status code    | 0004                               |
            | Status message | Parameter error: Invalid sort code |

    Scenario: The API provides incorrect Sort Code - not numbers 0-9

        Given a Service is consuming Financial Status API
        When the Financial Status API is invoked with the following:
            | Sort Code      | 13-56-0q   |
        Then FSPS Tier four general Case Worker tool API provides the following result
            | HTTP Status    | 404                                                           |
            | Status code    | 0001                                                          |
            | Status message | Resource not found: Please check URL and parameters are valid |

######################### Validation on the Account Number Field #########################

    Scenario: The API is not provided with Account Number

        Given a Service is consuming Financial Status API
        When the Financial Status API is invoked with the following:
            | Account Number |            |
        Then FSPS Tier four general Case Worker tool API provides the following result
            | HTTP Status    | 404                                                           |
            | Status code    | 0001                                                          |
            | Status message | Resource not found: Please check URL and parameters are valid |


    Scenario: The API is not provided with Account Number - too short

        Given a Service is consuming Financial Status API
        When the Financial Status API is invoked with the following:
            | Account Number | 2356849    |
        Then FSPS Tier four general Case Worker tool API provides the following result
            | HTTP Status    | 400                                     |
            | Status code    | 0004                                    |
            | Status message | Parameter error: Invalid account number |

    Scenario: The API is not provided with Account Number - too long

        Given a Service is consuming Financial Status API
        When the Financial Status API is invoked with the following:
            | Account Number | 235684988  |
        Then FSPS Tier four general Case Worker tool API provides the following result
            | HTTP Status    | 400                                     |
            | Status code    | 0004                                    |
            | Status message | Parameter error: Invalid account number |

    Scenario: The API is not provided with Account Number - all 0's

        Given a Service is consuming Financial Status API
        When the Financial Status API is invoked with the following:
            | Account Number | 00000000   |
        Then FSPS Tier four general Case Worker tool API provides the following result
            | HTTP Status    | 400                                     |
            | Status code    | 0004                                    |
            | Status message | Parameter error: Invalid account number |

    Scenario: The API is not provided with Account Number - not numbers 0-9

        Given a Service is consuming Financial Status API
        When the Financial Status API is invoked with the following:
            | Account Number | 23568a98   |
        Then FSPS Tier four general Case Worker tool API provides the following result
            | HTTP Status    | 404                                                           |
            | Status code    | 0001                                                          |
            | Status message | Resource not found: Please check URL and parameters are valid |

    Scenario: The API is provided with an Account Number that does not exist

        Given a Service is consuming Financial Status API
        When the Financial Status API is invoked with the following:
            | Account Number | 21568198   |
        Then FSPS Tier four general Case Worker tool API provides the following result
            | HTTP Status    | 404                                                         |
            | Status code    | 0007                                                        |
            | Status message | No records for sort code 135609 and account number 21568198 |

        ######################### Validation on the Date of Birth Field #########################

    Scenario: The API is not provided with Date of Birth

        Given a Service is consuming Financial Status API
        When the Financial Status API is invoked with the following:
            | Date of Birth  |            |
        Then FSPS Tier four general Case Worker tool API provides the following result
            | HTTP Status    | 400                                    |
            | Status code    | 0004                                   |
            | Status message | Parameter error: Invalid date of birth |

    Scenario: The API provides incorrect Date of birth - in the future

        Given a Service is consuming Financial Status API
        When the Financial Status API is invoked with the following:
            | Date of Birth  | 2019-01-15 |
        Then FSPS Tier four general Case Worker tool API provides the following result
            | HTTP Status    | 400                                    |
            | Status code    | 0004                                   |
            | Status message | Parameter error: Invalid date of birth |

    Scenario: The API is provided with an incorrect Date of birth - not numbers 0-9

        Given a Service is consuming Financial Status API
        When the Financial Status API is invoked with the following:
            | Date of Birth  | 1984-01-1@ |
        Then FSPS Tier four general Case Worker tool API provides the following result
            | HTTP Status    | 400                                     |
            | Status code    | 0002                                    |
            | Status message | Parameter conversion error: Invalid dob |
