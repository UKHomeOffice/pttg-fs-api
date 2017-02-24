Feature: Calculation of condition codes for T4 Post Graduate Doctor Dentist
## Condition Codes ##

    # DES, PGDD and SUSO have set codes regardless of the course type and course length selected

#    SO THAT I can identify the condition codes for a T4 Postgraduate Doctor or Dentist applicant
#    AS A Caseworker
#    WOULD LIKE The Financial Status Tool to automatically generate a condition code for a T4 Postgraduate Doctor or Dentist applicant

################# applicant only   #######################

    Scenario: Ryan is a main PGDD applicant and does not have dependants.

        Given A Service is consuming the Condition Code API
        When the Condition Code Tier 4 Other API is invoked with the following
            | Student Type    | pgdd |
            | Dependants only | No   |
            | Dependants      | 0    |
        Then The Financial Status API provides the following results:
            | applicantConditionCode | 2 |

################# applicant with dependants   #######################

    Scenario: Gary is a main PGDD applicant and has 5 dependants.

        Given A Service is consuming the Condition Code API
        When the Condition Code Tier 4 Other API is invoked with the following
            | Student Type    | pgdd |
            | Dependants only | No   |
            | Dependants      | 5    |
        Then The Financial Status API provides the following results:
            | applicantConditionCode | 2  |
            | partnerConditionCode   | 4B |
            | childConditionCode     | 1  |

################# dependant only   #######################

    Scenario: Phil is a dependant only PGDD applicant

        Given A Service is consuming the Condition Code API
        When the Condition Code Tier 4 Other API is invoked with the following
            | Student Type    | pgdd |
            | Dependants only | Yes  |
            | Dependants      | 1    |
        Then The Financial Status API provides the following results:
            | partnerConditionCode | 4B |
            | childConditionCode   | 1  |
