Feature: Validation of the API fields and data

    Mandatory fields:
    In London - Yes or No options (mandatory)
    Course length - 1-9 months, Format should not contain commas
    Accommodation fees already paid - Format should not contain commas or currency symbols
    To Date - Format should be yyyy-mm-dd
    From Date - Format should be yyyy-mm-dd
    Dependant - Format should not contain commas

######################### Validation on the Student type field #########################

    Scenario: The API is not provided with Stydent type field
        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type                    |         |
            | In London                       | Yes     |
            | Course Length                   | 1       |
            | Total tuition fees              | 3500.50 |
            | Tuition fees already paid       | 0       |
            | Accommodation fees already paid | 0       |
            | Number of dependants            | 1       |
        Then the service displays the following result
            | HTTP Status    | 400                                                                                |
            | Status code    | 0000                                                                               |
            | Status message | Parameter error: Invalid studentType, must be one of [doctorate,nondoctorate,pgdd] |

######################### Validation on the In London field #########################

    Scenario: The API is not provided with In London Yes or No field
        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type                    | nondoctorate |
            | In London                       |              |
            | Course Length                   | 2            |
            | Total tuition fees              | 3500.50      |
            | Tuition fees already paid       | 0            |
            | Accommodation fees already paid | 0            |
            | Number of dependants            | 1            |
        Then the service displays the following result
            | HTTP Status    | 400                                             |
            | Status code    | 0000                                            |
            | Status message | Parameter error: Invalid inLondon, must be true or false |

######################### Validation on the Course length field #########################

    Scenario: The API is not provided with the Course length
        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type                    | nondoctorate |
            | In London                       | Yes          |
            | Course Length                   |              |
            | Total tuition fees              | 3500.50      |
            | Tuition fees already paid       | 0            |
            | Accommodation fees already paid | 0            |
            | Number of dependants            | 1            |
        Then the service displays the following result
            | HTTP Status    | 400                                                                       |
            | Status code    | 0000                                                                      |
            | Status message | Parameter error: Invalid courseLength, must be greater than zero |

    Scenario: The API is provided with incorrect Course Length - not numbers 1-9
        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type                    | nondoctorate |
            | In London                       | Yes          |
            | Course Length                   | x            |
            | Total tuition fees              | 3500.50      |
            | Tuition fees already paid       | 0            |
            | Accommodation fees already paid | 0            |
            | Number of dependants            | 1            |
        Then the service displays the following result
            | HTTP Status    | 400                                              |
            | Status code    | 0000                                             |
            | Status message | Parameter conversion error: Invalid courseLength |


######################### Validation on the Accommodation fees already paid field #########################

    Scenario: The API is not provided with Accommodation fees already paid
        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type                    | nondoctorate |
            | In London                       | Yes          |
            | Course Length                   | 1            |
            | Total tuition fees              | 3500.50      |
            | Tuition fees already paid       | 0            |
            | Accommodation fees already paid |              |
            | Number of dependants            | 1            |
        Then the service displays the following result
            | HTTP Status    | 400                                            |
            | Status code    | 0000                                           |
            | Status message | Parameter error: Invalid accommodationFeesPaid |

    Scenario: The API is provided with incorrect  Accommodation fees already paid - not numbers 0-9
        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type                    | nondoctorate |
            | In London                       | Yes          |
            | Course Length                   | 1            |
            | Total tuition fees              | 3500.50      |
            | Tuition fees already paid       | 0            |
            | Accommodation fees already paid | %%           |
            | Number of dependants            | 1            |
        Then the service displays the following result
            | HTTP Status    | 400                                                       |
            | Status code    | 0000                                                      |
            | Status message | Parameter conversion error: Invalid accommodationFeesPaid |

    Scenario: The API is provided with incorrect  Accommodation fees already paid - less than zero
        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type                    | nondoctorate |
            | The end of 28-day period        | 20/06/2016   |
            | In London                       | Yes          |
            | Course Length                   | 1            |
            | Total tuition fees              | 3500.50      |
            | Tuition fees already paid       | 0            |
            | Accommodation fees already paid | -100         |
            | Number of dependants            | 1            |
        Then the service displays the following result
            | HTTP Status    | 400                                            |
            | Status code    | 0000                                           |
            | Status message | Parameter error: Invalid accommodationFeesPaid |

    Scenario: The API is provided with incorrect  Accommodation fees already paid - >£1,265
        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type                    | nondoctorate |
            | The end of 28-day period        | 20/06/2016   |
            | In London                       | Yes          |
            | Course length                   | 1            |
            | Total tuition fees              | 3500.50      |
            | Tuition fees already paid       | 0            |
            | Accommodation fees already paid | 2660         |
            | Number of dependants            | 1            |
        Then the service displays the following result
            | HTTP Status    | 400                                            |
            | Status code    | 0000                                           |
            | Status message | Parameter error: Invalid accommodationFeesPaid |


        ######################### Validation on the Dependant field #########################

    Scenario: The API is not provided with the Number of dependants
        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type                    | nondoctorate |
            | The end of 28-day period        | 20/06/2016   |
            | In London                       | Yes          |
            | Course Length                   | 9            |
            | Total tuition fees              | 3500.50      |
            | Tuition fees already paid       | 0            |
            | Accommodation fees already paid | 0            |
            | Number of dependants            | -4           |
        Then the service displays the following result
            | HTTP Status    | 400                                                          |
            | Status code    | 0000                                                         |
            | Status message | Parameter error: Invalid dependants, must be zero or greater |

    Scenario: The API is provided with incorrect Number of dependants - not numbers 0-9
        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type                    | nondoctorate |
            | The end of 28-day period        | 20/06/2016   |
            | In London                       | Yes          |
            | Course Length                   | 8            |
            | Total tuition fees              | 3500.50      |
            | Tuition fees already paid       | 0            |
            | Accommodation fees already paid | 0            |
            | Number of dependants            | ^            |
        Then the service displays the following result
            | HTTP Status    | 400                                            |
            | Status code    | 0000                                           |
            | Status message | Parameter conversion error: Invalid dependants |
