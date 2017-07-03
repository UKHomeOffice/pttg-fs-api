Feature: Validation of the API fields and data

    Background:
        Given A Service is consuming the FSPS Calculator API
        And the default details are
            | Applicant type | Main |
            | Dependants     | 0    |


######################### Validation on the Dependants field #########################

    Scenario: The API is not provided with the Number of dependants
        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator Tier_Five API is invoked with the following
            | Dependants | -4 |
        Then The Tier_five Financial Status API provides the following validation results:
            | HTTP Status    | 400                                                          |
            | Status code    | 0004                                                         |
            | Status message | Parameter error: Invalid dependants, must be zero or greater |

    Scenario: The API is provided with incorrect Number of dependants - not numbers 0-9
        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator Tier_Five API is invoked with the following
            | Dependants | ^ |
        Then The Tier_five Financial Status API provides the following validation results:
            | HTTP Status    | 400                                            |
            | Status code    | 0002                                           |
            | Status message | Parameter conversion error: Invalid dependants |
####################################################
    Scenario: The API is provided with dependants for a tier 5 youth mobility
        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator Tier_Five API is invoked with the following
            | Dependants   | 2                     |
            | Variant Type | Youth Mobility Scheme |
        Then The Tier_five Financial Status API provides the following validation results:
            | HTTP Status    | 400                                                      |
            | Status code    | 0004                                                     |
            | Status message | Parameter error: Invalid dependants, No dependants allowed |
######################### Validation on the Main applicant field #########################

    Scenario: The API is not provided with details as to whether there is a main dependant
        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator Tier_Five API is invoked with the following
            | Applicant type | xxxMain |
            | Variant type   |      |
        Then The Tier_five Financial Status API provides the following validation results:
            | HTTP Status    | 400                                                                     |
            | Status code    | 0004                                                                    |
            | Status message | Parameter error: Invalid applicantType, must be one of [main,dependant] |




