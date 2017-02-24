Feature: Calculation of condition codes for T4 Doctorate Extension Scheme
## Condition Codes ##

    # DES, PGDD and SUSO have set codes regardless of the course type and course length selected

#    SO THAT I can identify the condition codes for a T4 Doctorate extension scheme applicant
#    AS A Caseworker
#    WOULD LIKE The Financial Status Tool to automatically generate a condition code for a T4 Doctorate extension scheme applicant

################# applicant only   #######################

    Scenario: Alvin is a main DES applicant and does not have dependants.

        Given A Service is consuming the Condition Code API
        When the Condition Code Tier 4 Other API is invoked with the following
            | Student Type    | des |
            | Dependants only | No  |
            | Dependants      | 0   |
        Then The Financial Status API provides the following results:
            | applicantConditionCode | 4E |

################# applicant with dependants   #######################

    Scenario: Simon is a main DES applicant and has 2 dependants.

        Given A Service is consuming the Condition Code API
        When the Condition Code Tier 4 Other API is invoked with the following
            | Student Type    | des |
            | Dependants only | No  |
            | Dependants      | 2   |
        Then The Financial Status API provides the following results:
            | applicantConditionCode | 4E |
            | partnerConditionCode   | 4B |
            | childConditionCode     | 1  |

################# dependants only   #######################

    Scenario: Theodore is a dependant only DES applicant

        Given A Service is consuming the Condition Code API
        When the Condition Code Tier 4 Other API is invoked with the following
            | Student Type    | des |
            | Dependants only | Yes |
            | Dependants      | 1   |
        Then The Financial Status API provides the following results:
            | partnerConditionCode | 4B |
            | childConditionCode   | 1  |
