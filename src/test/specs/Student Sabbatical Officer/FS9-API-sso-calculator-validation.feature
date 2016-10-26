Feature: Validation of the API fields and data

    In London - Yes or No options (mandatory)
    Course Length - 1-2 months
    Accommodation fees already paid - Format should not contain commas or currency symbols
    To Date - Format should be yyyy-mm-dd
    From Date - Format should be yyyy-mm-dd

######################### Validation on the Student type field #########################

    Scenario: The API is not provided with Student type field
        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type                    |          |
            | In London                       | Yes      |
            | Course start date               | 2016-1-3 |
            | Course end date                 | 2016-2-3 |
            | Accommodation fees already paid | 0        |
        Then the service displays the following result
            | HTTP Status    | 400                                                                                    |
            | Status code    | 0004                                                                                   |
            | Status message | Parameter error: Invalid studentType, must be one of [doctorate,nondoctorate,pgdd,sso] |

######################### Validation on the In London field #########################

    Scenario: The API is not provided with In London Yes or No field
        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type                    | sso      |
            | In London                       |          |
            | Course start date               | 2016-1-3 |
            | Course end date                 | 2016-3-3 |
            | Accommodation fees already paid | 0        |
        Then the service displays the following result
            | HTTP Status    | 400                                                      |
            | Status code    | 0004                                                     |
            | Status message | Parameter error: Invalid inLondon, must be true or false |


######################### Validation on the Accommodation fees already paid field #########################

    Scenario: The API is not provided with Accommodation fees already paid
        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type                    | sso      |
            | In London                       | Yes      |
            | Course start date               | 2016-1-3 |
            | Course end date                 | 2016-2-3 |
            | Accommodation fees already paid |          |
        Then the service displays the following result
            | HTTP Status    | 400                                            |
            | Status code    | 0004                                           |
            | Status message | Parameter error: Invalid accommodationFeesPaid |



