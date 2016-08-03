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
            | Student Type                    |     |
            | In London                       | Yes |
            | Course Length                   | 1   |
            | Accommodation fees already paid | 0   |
        Then the service displays the following result
            | HTTP Status    | 400                                                                                    |
            | Status code    | 0000                                                                                   |
            | Status message | Parameter error: Invalid studentType, must be one of [doctorate,nondoctorate,pgdd,sso] |

######################### Validation on the In London field #########################

    Scenario: The API is not provided with In London Yes or No field
        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type                    | pgdd |
            | In London                       |      |
            | Course Length                   | 2    |
            | Accommodation fees already paid | 0    |
        Then the service displays the following result
            | HTTP Status    | 400                                                      |
            | Status code    | 0000                                                     |
            | Status message | Parameter error: Invalid inLondon, must be true or false |

######################### Validation on the Course length field #########################

    Scenario: The API is not provided with the Course length
        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type                    | pgdd |
            | In London                       | Yes  |
            | Course Length                   |      |
            | Accommodation fees already paid | 0    |
        Then the service displays the following result
            | HTTP Status    | 400                                                              |
            | Status code    | 0000                                                             |
            | Status message | Parameter error: Invalid courseLength, must be greater than zero |

    Scenario: The API is provided with incorrect Course Length - not numbers 1-2
        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type                    | pgdd |
            | In London                       | Yes  |
            | Course Length                   | x    |
            | Accommodation fees already paid | 0    |
        Then the service displays the following result
            | HTTP Status    | 400                                              |
            | Status code    | 0000                                             |
            | Status message | Parameter conversion error: Invalid courseLength |

######################### Validation on the Accommodation fees already paid field #########################

    Scenario: The API is not provided with Accommodation fees already paid
        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type                    | pgdd |
            | In London                       | Yes  |
            | Course Length                   | 1    |
            | Accommodation fees already paid |      |
        Then the service displays the following result
            | HTTP Status    | 400                                            |
            | Status code    | 0000                                           |
            | Status message | Parameter error: Invalid accommodationFeesPaid |

    Scenario: The API is provided with incorrect  Accommodation fees already paid - not numbers 1-2
        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type                    | pgdd |
            | In London                       | Yes  |
            | Course Length                   | 1    |
            | Accommodation fees already paid | %%   |
        Then the service displays the following result
            | HTTP Status    | 400                                                       |
            | Status code    | 0000                                                      |
            | Status message | Parameter conversion error: Invalid accommodationFeesPaid |

    Scenario: The API is provided with incorrect  Accommodation fees already paid - less than zero
        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type                    | pgdd |
            | In London                       | Yes  |
            | Course Length                   | 1    |
            | Accommodation fees already paid | -100 |
        Then the service displays the following result
            | HTTP Status    | 400                                            |
            | Status code    | 0000                                           |
            | Status message | Parameter error: Invalid accommodationFeesPaid |

#    Scenario: The API is provided with incorrect  Accommodation fees already paid - >£1,265
#        Given A Service is consuming the FSPS Calculator API
#        When the FSPS Calculator API is invoked with the following
#            | Student Type                    | pgdd       |
#            | The end of 28-day period        | 20/06/2016 |
#            | In London                       | Yes        |
#            | Course length                   | 1          |
#            | Accommodation fees already paid | 3214       |
#
#        Then the service displays the following result
#            | HTTP Status    | 400                                            |
#            | Status code    | 0000                                           |
#            | Status message | Parameter error: Invalid accommodationFeesPaid |
