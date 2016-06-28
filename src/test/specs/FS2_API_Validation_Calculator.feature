Feature: Total Funds Required Calculation - Validation

    Inner London borough - Yes or No options
    Course Length - 1-9 months
    Total tuition fees - Format should not contain commas or currency symbols
    Tuition fees already paid - Format should not contain commas or currency symbols
    Accommodation fees already paid - Format should not contain commas or currency symbols


  ######################### Validation on the Inner London borough Field #########################

  Scenario: The API is not provided with Inner London borough Yes or No field
      Given a Service is consuming Financial Status API
      When the FSPS Calculator API is invoked with the following
          | Inner London borough            |            |
          | Course Length                   | 9          |
          | Total tuition fees              | 9755.50    |
          | Tuition fees already paid       | 500        |
          | Accommodation fees already paid | 250.50     |
      Then FSPS Tier four general Case Worker tool API provides the following result
          | HTTP Status    | 400                                           |
          | Status code    | 0000                                          |
          | Status message | Parameter error: Invalid Inner London borough |

  ######################### Validation on the Course Length Field #########################

  Scenario: The API is not provided with the Course length
      Given a Service is consuming Financial Status API
      When the Financial Status API is invoked with the following:
          | Inner London borough            | Yes        |
          | Course Length                   |            |
          | Total tuition fees              | 9755.50    |
          | Tuition fees already paid       | 500        |
          | Accommodation fees already paid | 250.50     |
      Then FSPS Tier four general Case Worker tool API provides the following result
          | HTTP Status    | 400                                    |
          | Status code    | 0000                                   |
          | Status message | Parameter error: Invalid Course length |

  Scenario: The API is provided with incorrect Course Length - not numbers 0-9
      Given a Service is consuming Financial Status API
      When the Financial Status API is invoked with the following:
          | Inner London borough            | Yes        |
          | Course Length                   |            |
          | Total tuition fees              | 9755.50    |
          | Tuition fees already paid       | 500        |
          | Accommodation fees already paid | 250.50     |
      Then FSPS Tier four general Case Worker tool API provides the following result
          | HTTP Status    | 400                                    |
          | Status code    | 0000                                   |
          | Status message | Parameter error: Invalid Course length |

  Scenario: The API is provided with incorrect Course Length - more than 9
      Given caseworker is using the financial status service ui
      When the financial status check is performed with
          | Inner London borough            | Yes        |
          | Course Length                   | 12         |
          | Total tuition fees              | 9755.50    |
          | Tuition fees already paid       | 500        |
          | Accommodation fees already paid | 250.50     |
      Then FSPS Tier four general Case Worker tool API provides the following result
          | HTTP Status    | 400                                    |
          | Status code    | 0000                                   |
          | Status message | Parameter error: Invalid Course length |

  ######################### Validation on the Total tuition fees Field #########################

  Scenario: The API is not provided with Total tuition fees
      Given caseworker is using the financial status service ui
      When the financial status check is performed with
          | Inner London borough            | Yes        |
          | Course Length                   | 9          |
          | Total tuition fees              |            |
          | Tuition fees already paid       | 500        |
          | Accommodation fees already paid | 250.50     |
      Then FSPS Tier four general Case Worker tool API provides the following result
          | HTTP Status    | 400                                                      |
          | Status code    | 0000                                                     |
          | Status message | Parameter error: Invalid Tuition fees for the first year |


  Scenario: The API is provided with incorrect  tuition fees - not numbers 0-9
      Given caseworker is using the financial status service ui
      When the financial status check is performed with
          | Inner London borough            | Yes        |
          | Course Length                   | 9          |
          | Total tuition fees              | !           |
          | Tuition fees already paid       | 500        |
          | Accommodation fees already paid | 250.50     |
      Then FSPS Tier four general Case Worker tool API provides the following result
          | HTTP Status    | 400                                                      |
          | Status code    | 0000                                                     |
          | Status message | Parameter error: Invalid Tuition fees for the first year |

  ######################### Validation on the Tuition fees already paid Field #########################

  Scenario: The API is not provided with  tuition fees already paid -
      Given caseworker is using the financial status service ui
      When the financial status check is performed with
          | Inner London borough            | Yes        |
          | Course Length                   | 12         |
          | Total tuition fees              | 9755.50    |
          | Tuition fees already paid       |            |
          | Accommodation fees already paid | 250.50     |
      Then FSPS Tier four general Case Worker tool API provides the following result
          | HTTP Status    | 400                                                |
          | Status code    | 0000                                               |
          | Status message | Parameter error: Invalid Tuition fees already paid |

  Scenario: The API is not provided with Tuition fees already paid - not numbers 0-9
      Given caseworker is using the financial status service ui
      When the financial status check is performed with
          | Inner London borough            | Yes        |
          | Course Length                   | 12         |
          | Total tuition fees              | 9755.50    |
          | Tuition fees already paid       | w          |
          | Accommodation fees already paid | 250.50     |
      Then FSPS Tier four general Case Worker tool API provides the following result
          | HTTP Status    | 400                                                |
          | Status code    | 0000                                               |
          | Status message | Parameter error: Invalid Tuition fees already paid |

  ######################### Validation on the Accommodation fees already paid Field #########################

  Scenario: The API is not provided with Accommodation fees already paid
      Given caseworker is using the financial status service ui
      When the financial status check is performed with
          | Inner London borough            | Yes        |
          | Course Length                   | 12         |
          | Total tuition fees              | 9755.50    |
          | Tuition fees already paid       | 2500       |
          | Accommodation fees already paid |            |
      Then FSPS Tier four general Case Worker tool API provides the following result
          | HTTP Status    | 400                                                      |
          | Status code    | 0000                                                     |
          | Status message | Parameter error: Invalid Accommodation fees already paid |

  Scenario: The API is provided with incorrect  Accommodation fees already paid - not numbers 0-9
      Given caseworker is using the financial status service ui
      When the financial status check is performed with
          | Inner London borough            | Yes        |
          | Course Length                   | 12         |
          | Total tuition fees              | 9755.50    |
          | Tuition fees already paid       | 2500       |
          | Accommodation fees already paid | @           |
      Then FSPS Tier four general Case Worker tool API provides the following result
          | HTTP Status    | 400                                                      |
          | Status code    | 0000                                                     |
          | Status message | Parameter error: Invalid Accommodation fees already paid |

