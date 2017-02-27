Feature: Validation of the API fields and data

    Student Type - des, general, pgdd or suso (mandatory)
    Course Type - pre-sessional or main (mandatory)
    In London - Yes or No options (mandatory)
    Course Length - 1-9 months
    Original Course Start Date - Yes or No options (mandatory when Continuation field response is 'yes')
    Accommodation fees already paid - Format should not contain commas or currency symbols
    To Date - Format should be yyyy-mm-dd
    From Date - Format should be yyyy-mm-dd


    Background: The API is not provided with Student type field
        Given A Service is consuming the FSPS Calculator API
        And the default details are
            | Student Type                    | general    |
            | Course Type                     | main       |
            | In London                       | Yes        |
            | Course start date               | 2016-01-03 |
            | Course end date                 | 2016-02-03 |
            | Original course start date      | 2015-12-03 |
            | Dependents                      | 1          |
            | Total tuition fees              | 3500.50    |
            | Tuition fees already paid       | 0          |
            | Accommodation fees already paid | 0          |
            | Dependants only                 | No         |

######################### Validation on the Student type field #########################

    Scenario: The API is not provided with Student type field
        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type |  |
        Then the service displays the following result
            | HTTP Status    | 400                                                                          |
            | Status code    | 0004                                                                         |
            | Status message | Parameter error: Invalid studentType, must be one of [des,general,pgdd,suso] |

######################### Validation on the In London field #########################

    Scenario: The API is not provided with In London Yes or No field
        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | In London |  |
        Then the service displays the following result
            | HTTP Status    | 400                                                      |
            | Status code    | 0004                                                     |
            | Status message | Parameter error: Invalid inLondon, must be true or false |

######################### Validation on the Course length field #########################

    Scenario: The API is not provided with the Course start date
        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Course start date |  |
        Then the service displays the following result
            | HTTP Status    | 400                                                 |
            | Status code    | 0004                                                |
            | Status message | Parameter conversion error: Invalid courseStartDate |

    Scenario: The API is not provided with the Course end date
        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Course end date |  |
        Then the service displays the following result
            | HTTP Status    | 400                                               |
            | Status code    | 0004                                              |
            | Status message | Parameter conversion error: Invalid courseEndDate |

    Scenario: The API is provided with incorrect Course Length - not numbers 1-9
        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Course end date | x |
        Then the service displays the following result
            | HTTP Status    | 400                                               |
            | Status code    | 0002                                              |
            | Status message | Parameter conversion error: Invalid courseEndDate |

######################### Validation on the Accommodation fees already paid field #########################

    Scenario: The API is not provided with Accommodation fees already paid
        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Accommodation fees already paid |  |
        Then the service displays the following result
            | HTTP Status    | 400                                            |
            | Status code    | 0004                                           |
            | Status message | Parameter error: Invalid accommodationFeesPaid |

    Scenario: The API is provided with incorrect  Accommodation fees already paid - not numbers 0-9
        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Accommodation fees already paid | %% |
        Then the service displays the following result
            | HTTP Status    | 400                                                       |
            | Status code    | 0002                                                      |
            | Status message | Parameter conversion error: Invalid accommodationFeesPaid |

    Scenario: The API is provided with incorrect  Accommodation fees already paid - less than zero
        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Accommodation fees already paid | -100 |
        Then the service displays the following result
            | HTTP Status    | 400                                            |
            | Status code    | 0004                                           |
            | Status message | Parameter error: Invalid accommodationFeesPaid |

######################### Validation on the Course type field #########################

    Scenario: The API is not provided with Course type field
        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Course Type |  |
        Then the service displays the following result
            | HTTP Status    | 400                                                                      |
            | Status code    | 0004                                                                     |
            | Status message | Parameter error: Invalid courseType, must be one of [main,pre-sessional] |

######################### Validation on the Dependants field #########################

    Scenario: The API is not provided with the Number of dependants
        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | dependants | -4 |
        Then the service displays the following result
            | HTTP Status    | 400                                                          |
            | Status code    | 0004                                                         |
            | Status message | Parameter error: Invalid dependants, must be zero or greater |

    Scenario: The API is provided with incorrect Number of dependants - not numbers 0-9
        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | dependants | ^ |
        Then the service displays the following result
            | HTTP Status    | 400                                            |
            | Status code    | 0002                                           |
            | Status message | Parameter conversion error: Invalid dependants |


######################### Validation on the Original course start date field #########################

    Scenario: The API provided with Original course start date that is not before the course start date
        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Original course start date | 2018-01-01 |
        Then the service displays the following result
            | HTTP Status    | 400                                                                          |
            | Status code    | 0004                                                                         |
            | Status message | Parameter error: Original course start date must be before course start date |

    Scenario: The API is provided with incorrect Course Length - not numbers 1-9
        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Course end date | x |
        Then the service displays the following result
            | HTTP Status    | 400                                               |
            | Status code    | 0002                                              |
            | Status message | Parameter conversion error: Invalid courseEndDate |

