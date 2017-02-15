## Condition Codes ##

    # DES, PGDD and SUSO have set codes regardless of the course type and course length selected

SO THAT I can identify the condition codes for a T4 Postgraduate Doctor or Dentist applicant
AS A Caseworker
WOULD LIKE The Financial Status Tool to automatically generate a condition code for a T4 Postgraduate Doctor or Dentist applicant

################# applicant only   #######################

Scenario: Ryan is a main DES applicant and does not have dependants.

    Given A Service is consuming the FSPS Calculator API
    When the FSPS Calculator API is invoked with the following
        | Student Type       | pgdd                        |
        | Application Type   | T4main                      |
        | Dependants         | 0                           |
    Then The Financial Status API provides the following result:
        | Condition Code     | Applicant - 2               |

################# applicant with dependants   #######################

Scenario: Gary is a main DES applicant and has 5 dependants.

    Given A Service is consuming the FSPS Calculator API
    When the FSPS Calculator API is invoked with the following
        | Student Type       | pgdd                        |
        | Application Type   | T4main                      |
        | Dependants         | 5                           |
    Then The Financial Status API provides the following result:
        | Condition Code     | Applicant - 2               |
                             | Partner - 4B                |
                             | Child - 1                   |

################# dependant only   #######################

Scenario: Phil is a dependant only DES applicant

    Given A Service is consuming the FSPS Calculator API
    When the FSPS Calculator API is invoked with the following
        | Student Type       | pgdd                        |
        | Application Type   | t4dependant                 |
        | Dependants         | 1                           |
    Then The Financial Status API provides the following result:
        | Condition Code     | Partner - 4B                |
                             | Child - 1                   |
