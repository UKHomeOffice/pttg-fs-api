Feature: Calculation of condition codes for T4 Doctorate Extension Scheme
## Condition Codes ##

    # DES, PGDD and SUSO have set codes regardless of the course type and course length selected

#    SO THAT I can identify the condition codes for a T4 Doctorate extension scheme applicant
#    AS A Caseworker
#    WOULD LIKE The Financial Status Tool to automatically generate a condition code for a T4 Doctorate extension scheme applicant

################# applicant only   #######################

    Scenario: Alvin is a main DES applicant and does not have dependants.

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type     | Des    |
            | Application Type | T4main |
            | Dependants       | 0      |
        Then The Financial Status API provides the following result:
            | Condition Code | 4E |

################# applicant with dependants   #######################

    Scenario: Simon is a main DES applicant and has 2 dependants.

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type     | Des    |
            | Application Type | T4main |
            | Dependants       | 2      |
        Then The Financial Status API provides the following result:
            | Applicant Condition Code | 4E |
            | Partner Condition Code   | 4B |
            | Child Condition Code     | 1  |

################# dependants only   #######################

    Scenario: Theodore is a dependant only DES applicant

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type     | Des         |
            | Application Type | t4dependant |
            | Dependants       | 1           |
        Then The Financial Status API provides the following result:
            | Partner Condition Code | 4B |
            | Child Condition Code   | 1  |
