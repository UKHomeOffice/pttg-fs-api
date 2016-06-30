Feature: Validation of the API fields and data


    Inner London borough - Yes or No options
    Course Length - 1-9 months
    Total tuition fees - Format should not contain commas or currency symbols
    Tuition fees already paid - Format should not contain commas or currency symbols
    Accommodation fees already paid - Format should not contain commas or currency symbols
    To Date - Format should be yyyy-mm-dd
    From Date - Format should be yyyy-mm-dd
    Minimum Funds Required - Format should not contain commas or currency symbols
    Sort code - Format should be three pairs of digits 13-56-09 (always numbers 0-9, no letters and cannot be all 0's)
    Account Number - Format should be 12345678 (always 8 numbers, 0-9, no letters, cannot be all 0's)



      ######################### Validation on the Inner London borough Field #########################

    Scenario: The API is not provided with Inner London borough Yes or No field
        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Inner London Borough            |         |
            | Course Length                   | 6       |
            | Total tuition fees              | 6530.12 |
            | Tuition fees already paid       | 0       |
            | Accommodation fees already paid | 0       |
        Then the service displays the following result
            | HTTP Status    | 400                                           |
            | Status code    | 0000                                          |
            | Status message | Parameter error: Invalid innerLondon |
######################### Validation on the Course Length Field #########################

    Scenario: The API is not provided with the Course length
        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Inner London Borough            | Yes     |
            | Course Length                   |         |
            | Total tuition fees              | 6530.12 |
            | Tuition fees already paid       | 0       |
            | Accommodation fees already paid | 0       |
        Then the service displays the following result
            | HTTP Status    | 400                                    |
            | Status code    | 0000                                   |
            | Status message | Parameter error: Invalid courseLength |


    Scenario: The API is provided with incorrect Course Length - not numbers 0-9
        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Inner London Borough            | Yes     |
            | Course Length                   | x       |
            | Total tuition fees              | 6530.12 |
            | Tuition fees already paid       | 0       |
            | Accommodation fees already paid | 0       |
        Then the service displays the following result
            | HTTP Status    | 400                                    |
            | Status code    | 0000                                   |
            | Status message | Parameter error: Invalid courseLength |

    Scenario: The API is provided with incorrect Course Length - more than 9
        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Inner London Borough            | Yes     |
            | Course Length                   | 10      |
            | Total tuition fees              | 6530.12 |
            | Tuition fees already paid       | 0       |
            | Accommodation fees already paid | 0       |
        Then the service displays the following result
            | HTTP Status    | 400                                    |
            | Status code    | 0000                                   |
            | Status message | Parameter error: Invalid courseLength |


######################### Validation on the Total tuition fees Field #########################

    Scenario: The API is not provided with Total tuition fees
        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Inner London Borough            | Yes |
            | Course Length                   | 9   |
            | Total tuition fees              |     |
            | Tuition fees already paid       | 0   |
            | Accommodation fees already paid | 0   |
        Then the service displays the following result
            | HTTP Status    | 400                                   |
            | Status code    | 0000                                  |
            | Status message | Parameter error: Invalid tuition fees |


    Scenario: The API is provided with incorrect  tuition fees - not numbers 0-9
        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Inner London Borough            | Yes   |
            | Course Length                   | 9     |
            | Total tuition fees              | ????? |
            | Tuition fees already paid       | 0     |
            | Accommodation fees already paid | 0     |
        Then the service displays the following result
            | HTTP Status    | 400                                   |
            | Status code    | 0000                                  |
            | Status message | Parameter error: Invalid tuition fees |


######################### Validation on the Tuition fees already paid Field #########################

    Scenario: The API is not provided with  tuition fees already paid -
        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Inner London Borough            | Yes     |
            | Course Length                   | 6       |
            | Total tuition fees              | 6530.12 |
            | Tuition fees already paid       |         |
            | Accommodation fees already paid | 0       |
        Then the service displays the following result
            | HTTP Status    | 400                                                |
            | Status code    | 0000                                               |
            | Status message | Parameter error: Tuition fees already paid Invalid |

    Scenario: The API is not provided with Tuition fees already paid - not numbers 0-9
        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Inner London Borough            | Yes     |
            | Course Length                   | 6       |
            | Total tuition fees              | 6530.12 |
            | Tuition fees already paid       | xxxxxx  |
            | Accommodation fees already paid | 0       |
        Then the service displays the following result
            | HTTP Status    | 400                                                |
            | Status code    | 0000                                               |
            | Status message | Parameter error: Tuition fees already paid Invalid |

######################### Validation on the Accommodation fees already paid Field #########################

    Scenario: The API is not provided with Accommodation fees already paid
        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Inner London Borough            | Yes     |
            | Course Length                   | 6       |
            | Total tuition fees              | 6530.12 |
            | Tuition fees already paid       | 0       |
            | Accommodation fees already paid |        |
        Then the service displays the following result
            | HTTP Status                  | 200      |
            | Status code    | 0000                                               |
            | Status message | Parameter error: Tuition fees already paid Invalid |

    Scenario: The API is provided with incorrect  Accommodation fees already paid - not numbers 0-9
        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Inner London Borough            | Yes     |
            | Course Length                   | 6       |
            | Total tuition fees              | 6530.12 |
            | Tuition fees already paid       | 0       |
            | Accommodation fees already paid | %%       |
        Then the service displays the following result
            | HTTP Status                  | 400      |
            | Status code    | 0000                                               |
            | Status message | Parameter error: Tuition fees already paid Invalid |
