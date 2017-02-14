## Condition Codes ##

    # The API should calculate the condition code using the course type selected, the course institution selected and if the applicant has any dependants
    # Course Type can be one of three options which are (1) Pre-sessional (2) Main course degree or higher or (3) Main course below degree
    # Course institution can be one of two options - Recognised body or HEI (higher education institution) or Other institution
    # Applicants (with no dependants) at recognised body or HEI and with a course type of Main course degree or higher or Main course below degree will always be 2
    # Applicants (with no dependants) at a other institution and with a course type of Main course degree or higher or Main course below degree will always be 3
    # Course length is taken into account when generating a code for an applicant with dependants at a Recognised body or HEI (higher education institution)
    # Pre sessional and main course below degree level - course length is not taken in to account when generating the condition code
    # Dependant Only applications will need their own condition codes (e.g. Partner 3, Child 1)
    # DES, PGDD and SUSO have set codes regardless of the course type selected

SO THAT I can identify the condition codes for a visa applicant
AS A Caseworker
WOULD LIKE The Financial Status Tool to automatically generate a condition for an applicant

################# Main course degree or higher at HEI - applicant only   #######################

Scenario: Theresa is on an 7 month main course degree or higher at a higher education institute and does not have dependants.

    Given A Service is consuming the FSPS Calculator API
    When the FSPS Calculator API is invoked with the following
        | Course Institution | Recognised body or HEI         |
        | Course type        | Main course degree or higher   |
        | Dependants         | 0                              |
    Then The Financial Status API provides the following result:
        |Condition Code      | Applicant - 2                  |

################# Main course degree or higher at HEI - with dependants - less than 12 months #######################

Scenario: Donald is on an 7 month main course degree or higher at a higher education institute and has 2 dependants.

    Given A Service is consuming the FSPS Calculator API
    When the FSPS Calculator API is invoked with the following
        |  Course Institution | Recognised body or HEI         |
        |  Course type        | Main course degree or higher   |
        |  Dependants         | 2                              |
        |  Course start date  | 2016-01-03                     |
        |  Course end date    | 2016-07-03                     |
    Then The Financial Status API provides the following results:
        |  Condition Code     | Applicant - 2                  |
                              | Partner - 3                    |
                              | Child - 1                      |

################# Main course degree or higher at HEI - with dependants - 12 months or greater #######################

Scenario: Barack is on a 13 month main course degree or higher at a higher education institute and has 2 dependants.

Given A Service is consuming the FSPS Calculator API
When the FSPS Calculator API is invoked with the following
        |  Course Institution   | Recognised body or HEI         |
        |  Course type          | Main course degree or higher   |
        |  Dependants           | 2                              |
        |  Course start date    | 2016-01-03                     |
        |  Course end date      | 2017-01-03                     |
Then The Financial Status API provides the following result:
        |  Condition Code       | Applicant - 2                  |
                                | Partner - 4B                   |
                                | Child - 1                      |

################# Pre Sessional course at HEI - applicant only  #######################

Scenario: Vladimir is on a 7 month pre-sessional at a higher education institute and does not have dependants.

    Given A Service is consuming the FSPS Calculator API
    When the FSPS Calculator API is invoked with the following
        | Course Institution | Recognised body or HEI     |
        | Course type        | Pre-sessional              |
        | Dependants         | 0                          |
        | Course start date  | 2016-01-03                 |
        | Course end date    | 2016-10-10                 |
    Then The Financial Status API provides the following result:
        |Condition Code      |    Applicant - 2A          |

################# Pre Sessional course at HEI with dependants #######################

Scenario: Hilary is on a 7 month pre-sessional course and has 1 dependant.

    Given A Service is consuming the FSPS Calculator API
    When the FSPS Calculator API is invoked with the following
        | Course Institution | Recognised body or HEI    |
        | Course type        | Pre-sessional             |
        | Dependants         | 1                         |
        | Course start date  | 2016-01-03                |
        | Course end date    | 2016-10-10                |
    Then The Financial Status API provides the following result:
        |Condition Code      | Applicant - 2A            |
                             | Partner - 3               |
                             | Child - 1                 |

################# Main course below degree at HEI - applicant only  #######################

Scenario: Margaret is on a 7 month below degree course at a higher education institute and does not have dependants

    Given A Service is consuming the FSPS Calculator API
    When the FSPS Calculator API is invoked with the following
        | Course Institution | Recognised body or HEI    |
        | Course Type        | Main Course below degree  |
        | Dependants         | 0                         |
        | Course start date  | 2016-01-03                |
        | Course end date    | 2016-10-10                |
    Then The Financial Status API provides the following result:
        |Condition Code     |    Applicant - 2A          |

################# Main course below degree at HEI - with dependants  #######################

Scenario: Bernard is on a 7 month below degree course at a higher education institute and has 2 dependants

    Given A Service is consuming the FSPS Calculator API
    When the FSPS Calculator API is invoked with the following
        | Course Institution | Recognised body or HEI     |
        | Course Type        | Main Course below degree   |
        | Dependants         | 2                          |
        | Course start date  | 2016-01-03                 |
        | Course end date    | 2016-10-10                 |
    Then The Financial Status API provides the following result:
        |Condition Code     |    Applicant - 2A          |
                            |    Partner - 3             |
                            |    Child - 1               |

################# Main course degree or higher at Other Institution - applicant only #######################

Scenario: Angela is on a 7 month main course degree or higher at a other institution and has no dependants

    Given A Service is consuming the FSPS Calculator API
    When the FSPS Calculator API is invoked with the following
            | Course Institution | Other Institution              |
            | Course Type        | Main Course degree or higher   |
            | Dependants         | 0                              |
            | Course start date  | 2016-01-03                     |
            | Course end date    | 2016-10-10                     |
    Then The Financial Status API provides the following result:
            |Condition Code      |    Applicant - 3               |

################# Main course degree or higher at Other Institution - with dependants - less than 12 months #######################

Scenario: Boris is on a  7 month main course degree or higher at a other institution and has 1 dependants

    Given A Service is consuming the FSPS Calculator API
    When the FSPS Calculator API is invoked with the following
            | Course Institution | Other Institution             |
            | Course Type        | Main Course degree or higher  |
            | Dependants         | 1                             |
            | Course start date  | 2016-01-03                    |
            | Course end date    | 2016-10-10                    |
    Then The Financial Status API provides the following result:
            |Condition Code      |    Applicant - 3              |
                                 |    Partner - 3                |
                                 |    Child - 1                  |

################# Main course degree or higher at Other Institution - with dependants - greater than 12 months #######################

Scenario: Bashar is on a 13 months main course degree or higher at a other institution and has 2 dependants

    Given A Service is consuming the FSPS Calculator API
    When the FSPS Calculator API is invoked with the following
            | Course Institution | Other Institution             |
            | Course Type        | Main Course degree or higher  |
            | Dependants         | 2                             |
            | Course start date  | 2016-01-03                    |
            | Course end date    | 2017-01-03                    |
    Then The Financial Status API provides the following result:
            |Condition Code      |    Applicant - 3              |
                                 |    Partner - 4B               |
                                 |    Child - 1                  |

################# Pre Sessional course at Other Institution - applicant only  #######################

Scenario: Winston is on a 7 month pre-sessional at a other institution and does not have dependants

    Given A Service is consuming the FSPS Calculator API
    When the FSPS Calculator API is invoked with the following
        | Course Institution | Other Institution             |
        | Course Type        | Pre-sessional                 |
        | Dependants         | 0                             |
        | Course start date  | 2016-01-03                    |
        | Course end date    | 2016-10-10                    |
    Then The Financial Status API provides the following result:
        |Condition Code      | Applicant - 3                 |


 ################# Pre Sessional course at Other Institution - with dependants #######################

Scenario: Ronald is on a 7 month pre-sessional at a other institution and has 1 dependant

    Given A Service is consuming the FSPS Calculator API
    When the FSPS Calculator API is invoked with the following
        | Course Institution | Other Institution             |
        | Course Type        | Pre-sessional                 |
        | Dependants         | 1                             |
        | Course start date  | 2016-01-03                    |
        | Course end date    | 2016-10-10                    |
    Then The Financial Status API provides the following result:
        | Condition Code     |    Applicant - 3              |
                             |    Partner - 3                |
                             |    Child - 1                  |

################# Main course below degree at Other Institution - applicant only  #######################

Scenario: Narendra is on a 7 month below degree course at a other institution and does not have dependants

    Given A Service is consuming the FSPS Calculator API
    When the FSPS Calculator API is invoked with the following
        | Course Institution | Other Institution              |
        | Course Type        | Main Course below degree       |
        | Dependants         | 0                              |
        | Course start date  | 2016-01-03                     |
        | Course end date    | 2016-10-10                     |
    Then The Financial Status API provides the following result:
        |Condition Code      | Applicant - 3                  |


################# Main course below degree at Other Institution - with dependants  #######################

Scenario: Joko is on a 7 month below degree course at a other institution and has 1 dependant

    Given A Service is consuming the FSPS Calculator API
    When the FSPS Calculator API is invoked with the following
        | Course Institution | Other Institution             |
        | Course Type        | Main Course below degree      |
        | Dependants         | 1                             |
        | Course start date  | 2016-01-03                    |
        | Course end date    | 2016-10-10                    |
    Then The Financial Status API provides the following result:
        | Condition Code     |    Applicant - 3              |
                             |    Partner - 3                |

################# Main course degree or higher at HEI - Dependant only application - less than 12 months  #######################

Scenario: Xavi is a dependant only application with the main applicant on an 7 month main course degree or higher at a higher education institute

    Given A Service is consuming the FSPS Calculator API
    When the FSPS Calculator API is invoked with the following
        |  Course Institution | Recognised body or HEI         |
        |  Course type        | Main course degree or higher   |
        |  Dependants         | 1                              |
        |  Course start date  | 2016-01-03                     |
        |  Course end date    | 2016-07-03                     |
        | Dependants only     | Yes                            |
Then The Financial Status API provides the following results:
        |  Condition Code     | Partner - 3                    |
                              | Child - 1                      |
