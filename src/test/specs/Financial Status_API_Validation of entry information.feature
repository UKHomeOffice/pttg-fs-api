Feature: Validation of the API fields and data

  Fields mandatory to fill in:
  Maintenance Period End Date - Format should be dd/mm/yyyy
  #Total Funds Required - Format should be (tbc)
  Sort code - Format should be three pairs of digits 13-56-09 (always numbers 0-9, no letters and cannot be all 0's)
  Account Number - Format should be 12345678 (always 8 numbers, 0-9, no letters, cannot be all 0's)

######################### Validation on the Maintenance Period End Date Field #########################

  Scenario: The API is not provided with End date of 28-day period
    Given A service is consuming Financial Status Service Case Worker API
    When the Financial Status API is invoked with the following
      | Maintenance Period End Date |          |
      | Total Funds Required        | 2350.00  |
      | Sort Code                   | 13-56-09 |
      | Account Number              | 23568498 |
    Then FSPS Tier 4 general Case Worker tool API provides the following result
      | HTTP Status    | 400                                                |
      | Status code    | 0004                                               |
      | Status message | Parameter error: Invalid end date of 28-day period |

  Scenario: The API provides incorrect End date of 28-day period - in the future
    Given A service is consuming Financial Status Service Case Worker API
    When the Financial Status API is invoked with the following
      | Maintenance Period End Date | 27/07/2016 |
      | Total Funds Required        | 2350.00    |
      | Sort Code                   | 13-56-09   |
      | Account Number              | 23568498   |
    Then FSPS Tier 4 general Case Worker tool API provides the following result
      | HTTP Status    | 400                                                |
      | Status code    | 0004                                               |
      | Status message | Parameter error: Invalid end date of 28-day period |

  Scenario: The API provides incorrect End date of 28-day period - not numbers 0-9
    Given A service is consuming Financial Status Service Case Worker API
    When the Financial Status API is invoked with the following
      | Maintenance Period End Date | 01/0d/2016 |
      | Total Funds Required        | 2350.00    |
      | Sort Code                   | 13-56-09   |
      | Account Number              | 23568498   |
    Then FSPS Tier 4 general Case Worker tool API provides the following result
      | HTTP Status    | 400                                                |
      | Status code    | 0004                                               |
      | Status message | Parameter error: Invalid end date of 28-day period |

######################### Validation on the Total Funds Required field #########################

  Scenario: The API is not provided with Total Funds Required
    Given A service is consuming Financial Status Service Case Worker API
    When the Financial Status API is invoked with the following
      | Maintenance Period End Date | 01/06/2016 |
      | Total Funds Required        |            |
      | Sort Code                   | 13-56-09   |
      | Account Number              | 23568498   |
    Then FSPS Tier 4 general Case Worker tool API provides the following result
      | HTTP Status    | 400                                           |
      | Status code    | 0004                                          |
      | Status message | Parameter error: Invalid Total Funds Required |

  Scenario: The API provides incorrect Total Funds Required - just 0
    Given A service is consuming Financial Status Service Case Worker API
    When the Financial Status API is invoked with the following
      | Maintenance Period End Date | 01/06/2016 |
      | Total Funds Required        | 0          |
      | Sort Code                   | 13-56-09   |
      | Account Number              | 23568498   |
    Then FSPS Tier 4 general Case Worker tool API provides the following result
      | HTTP Status    | 400                                           |
      | Status code    | 0004                                          |
      | Status message | Parameter error: Invalid Total Funds Required |

  Scenario: The API provides incorrect Total Funds Required - not numbers 0-9 (letters)
    Given A service is consuming Financial Status Service Case Worker API
    When the Financial Status API is invoked with the following
      | Maintenance Period End Date | 01/06/2016 |
      | Total Funds Required        | 23g50.00   |
      | Sort Code                   | 13-56-09   |
      | Account Number              | 23568498   |
    Then FSPS Tier 4 general Case Worker tool API provides the following result
      | HTTP Status    | 400                                           |
      | Status code    | 0004                                          |
      | Status message | Parameter error: Invalid Total Funds Required |

  Scenario: The API provides incorrect Total Funds Required - not numbers 0-9 (negative)
    Given A service is consuming Financial Status Service Case Worker API
    When the Financial Status API is invoked with the following
      | Maintenance Period End Date | 01/06/2016 |
      | Total Funds Required        | -2350.00   |
      | Sort Code                   | 13-56-09   |
      | Account Number              | 23568498   |
    Then FSPS Tier 4 general Case Worker tool API provides the following result
      | HTTP Status    | 400                                           |
      | Status code    | 0004                                          |
      | Status message | Parameter error: Invalid Total Funds Required |

######################### Validation on the Sort Code Field #########################

  # need to double check if correct
  Scenario: The API is not provided with Sort code
    Given A service is consuming Financial Status Service Case Worker API
    When the Financial Status API is invoked with the following
      | Maintenance Period End Date      | 01/06/2016 |
      | Total Funds Required             | 2350.00 |
      | Sort Code                        |    |
      | Account Number                   | 23568498    |
    Then FSPS Tier 4 general Case Worker tool API provides the following result
      | HTTP Status    | 404                                                               |
      | Status code    | 0008                                                              |
      | Status message | Resource not found: /incomeproving/v1/individual//financialstatus |


  Scenario: The API is not provided with Sort Code
    Given A service is consuming Financial Status Service Case Worker API
    When the Financial Status API is invoked with the following
      | Maintenance Period End Date | 01/06/2016 |
      | Total Funds Required        | 2350.00    |
      | Sort Code                   |            |
      | Account Number              | 23568498   |
    Then FSPS Tier 4 general Case Worker tool API provides the following result
      | HTTP Status    | 400                                |
      | Status code    | 0004                               |
      | Status message | Parameter error: Invalid Sort code |

  Scenario: The API provides incorrect Sort Code - mising digits
    Given A service is consuming Financial Status Service Case Worker API
    When the Financial Status API is invoked with the following
      | Maintenance Period End Date | 01/06/2016 |
      | Total Funds Required        | 2350.00    |
      | Sort Code                   | 13-56-0    |
      | Account Number              | 23568498   |
    Then FSPS Tier 4 general Case Worker tool API provides the following result
      | HTTP Status    | 400                                |
      | Status code    | 0004                               |
      | Status message | Parameter error: Invalid Sort code |

  Scenario: The API provides incorrect Sort Code - all 0's
    Given A service is consuming Financial Status Service Case Worker API
    When the Financial Status API is invoked with the following
      | Maintenance Period End Date | 01/06/2016 |
      | Total Funds Required        | 2350.00    |
      | Sort Code                   | 00-00-00   |
      | Account Number              | 23568498   |
    Then FSPS Tier 4 general Case Worker tool API provides the following result
      | HTTP Status    | 400                                |
      | Status code    | 0004                               |
      | Status message | Parameter error: Invalid Sort code |

  Scenario: The API provides incorrect Sort Code - not numbers 0-9
    Given A service is consuming Financial Status Service Case Worker API
    When the Financial Status API is invoked with the following
      | Maintenance Period End Date | 01/06/2016 |
      | Total Funds Required        | 2350.00    |
      | Sort Code                   | 13-56-0q   |
      | Account Number              | 23568498   |
    Then FSPS Tier 4 general Case Worker tool API provides the following result
      | HTTP Status    | 400                                |
      | Status code    | 0004                               |
      | Status message | Parameter error: Invalid Sort code |

######################### Validation on the Account Number Field #########################

  # need to double check if correct
  Scenario: The API is not provided with Account Number
    Given A service is consuming Financial Status Service Case Worker API
    When the Financial Status API is invoked with the following
      | Maintenance Period End Date      | 01/06/2016 |
      | Total Funds Required             | 2350.00 |
      | Sort Code                        | 13-56-09   |
      | Account Number                   |    |
    Then FSPS Tier 4 general Case Worker tool API provides the following result
      | HTTP Status    | 404                                                               |
      | Status code    | 0008                                                              |
      | Status message | Resource not found: /incomeproving/v1/individual//financialstatus |


  Scenario: The API is not provided with Account Number
    Given A service is consuming Financial Status Service Case Worker API
    When the Financial Status API is invoked with the following
      | Maintenance Period End Date | 01/06/2016 |
      | Total Funds Required        | 2350.00    |
      | Sort Code                   | 13-56-09   |
      | Account Number              |            |
    Then FSPS Tier 4 general Case Worker tool API provides the following result
      | HTTP Status    | 400                                |
      | Status code    | 0004                               |
      | Status message | Parameter error: Invalid account number|

  Scenario: The API is not provided with Account Number - too short
    Given A service is consuming Financial Status Service Case Worker API
    When the Financial Status API is invoked with the following
      | Maintenance Period End Date | 01/06/2016 |
      | Total Funds Required        | 2350.00    |
      | Sort Code                   | 13-56-09   |
      | Account Number              | 2356849    |
    Then FSPS Tier 4 general Case Worker tool API provides the following result
      | HTTP Status    | 400                                |
      | Status code    | 0004                               |
      | Status message | Parameter error: Invalid account number |

  Scenario: The API is not provided with Account Number - too long
    Given A service is consuming Financial Status Service Case Worker API
    When the Financial Status API is invoked with the following
      | Maintenance Period End Date | 01/06/2016 |
      | Total Funds Required        | 2350.00    |
      | Sort Code                   | 13-56-09   |
      | Account Number              | 235684988  |
    Then FSPS Tier 4 general Case Worker tool API provides the following result
      | HTTP Status    | 400                                           |
      | Status code    | 0004                                          |
      | Status message | Parameter error: Invalid account number |

  Scenario: The API is not provided with Account Number - all 0's
    Given A service is consuming Financial Status Service Case Worker API
    When the Financial Status API is invoked with the following
      | Maintenance Period End Date | 01/06/2016 |
      | Total Funds Required        | 2350.00    |
      | Sort Code                   | 13-56-09   |
      | Account Number              | 00000000   |
    Then FSPS Tier 4 general Case Worker tool API provides the following result
      | HTTP Status    | 400                                           |
      | Status code    | 0004                                          |
      | Status message | Parameter error: Invalid account number |

  Scenario: The API is not provided with Account Number - not numbers 0-9
    Given A service is consuming Financial Status Service Case Worker API
    When the Financial Status API is invoked with the following
      | Maintenance Period End Date | 01/06/2016 |
      | Total Funds Required        | 2350.00    |
      | Sort Code                   | 13-56-09   |
      | Account Number              | 23568a98   |
    Then FSPS Tier 4 general Case Worker tool API provides the following result
      | HTTP Status    | 400                                           |
      | Status code    | 0004                                          |
      | Status message | Parameter error: Invalid account number |



