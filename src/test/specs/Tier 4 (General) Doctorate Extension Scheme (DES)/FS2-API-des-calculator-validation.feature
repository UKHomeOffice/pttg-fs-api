Feature: Validation of the API fields and data

    Student Type - des, general, pgdd or suso (mandatory)
    In London - Yes or No options (mandatory)
    Accommodation fees already paid - Format should not contain commas or currency symbols
    To Date - Format should be yyyy-mm-dd
    From Date - Format should be yyyy-mm-dd
    Dependants - Format should not contain commas


######################### Validation on the Student type field #########################

    Scenario: The API is not provided with Student type field
        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type                    |            |
            | In London                       | Yes        |
            | Accommodation fees already paid | 0          |
            | dependants                      | 1          |
        Then the service displays the following result
            | HTTP Status    | 400                                                                                    |
            | Status code    | 0004                                                                                   |
            | Status message | Parameter error: Invalid studentType, must be one of [des,general,pgdd,suso] |

######################### Validation on the In London field #########################

    Scenario: The API is not provided with In London Yes or No field
        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type                    | des  |
            | In London                       |            |
            | Accommodation fees already paid | 0          |
            | dependants                      | 1          |
        Then the service displays the following result
            | HTTP Status    | 400                                                      |
            | Status code    | 0004                                                     |
            | Status message | Parameter error: Invalid inLondon, must be true or false |

######################### Validation on the Accommodation fees already paid field #########################

    Scenario: The API is not provided with Accommodation fees already paid
        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type                    | des  |
            | In London                       | Yes        |
            | Accommodation fees already paid |            |
            | dependants                      | 1          |
        Then the service displays the following result
            | HTTP Status    | 400                                            |
            | Status code    | 0004                                           |
            | Status message | Parameter error: Invalid accommodationFeesPaid |

    Scenario: The API is provided with incorrect  Accommodation fees already paid - not numbers 1-2
        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type                    | des  |
            | In London                       | Yes        |
            | Accommodation fees already paid | %%         |
            | dependants                      | 1          |
        Then the service displays the following result
            | HTTP Status    | 400                                                       |
            | Status code    | 0002                                                      |
            | Status message | Parameter conversion error: Invalid accommodationFeesPaid |

    Scenario: The API is provided with incorrect Accommodation fees already paid - less than zero
        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type                    | des  |
            | In London                       | Yes        |
            | Accommodation fees already paid | -100       |
            | dependants                      | 1          |
        Then the service displays the following result
            | HTTP Status    | 400                                            |
            | Status code    | 0004                                           |
            | Status message | Parameter error: Invalid accommodationFeesPaid |

        ######################### Validation on the Dependant field #########################

    Scenario: The API is not provided with the Number of dependants
        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type                    | des  |
            | In London                       | Yes        |
            | Accommodation fees already paid | 0          |
            | dependants                      | -7         |
        Then the service displays the following result
            | HTTP Status    | 400                                                          |
            | Status code    | 0004                                                         |
            | Status message | Parameter error: Invalid dependants, must be zero or greater |

    Scenario: The API is provided with incorrect Number of Dependants - not numbers 0-9
        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type                    | des  |
            | In London                       | Yes        |
            | Accommodation fees already paid | 0          |
            | dependants                      | @          |
        Then the service displays the following result
            | HTTP Status    | 400                                            |
            | Status code    | 0002                                           |
            | Status message | Parameter conversion error: Invalid dependants |

