Tiers 2 & 5 validation

Feature: Validation of the API fields and data

    Background: The API is not provided with Student type field
        Given A Service is consuming the FSPS Calculator API
        And the default details are
            | Main applicant | yes |
            | Dependents     | 0   |

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

######################### Validation on the Main applicant field #########################

    Scenario: The API is not provided with details as to whether there is a main dependant
        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Main applicant |  |
        Then the service displays the following result
            | HTTP Status    | 400                                                         |
            | Status code    | 0004                                                        |
            | Status message | Parameter error: Invalid main applicants, must be yes or no |




