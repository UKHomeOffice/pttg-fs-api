## Condition Codes ##

    # The API should calculate the condition code using the course type selected and if the applicant has any dependants
    # Course Type can be one of three options which are, Pre-sessional, Main course degree or higher or Main course below degree

################# Initial Main course degree or higher (HEI) - applicant only   #######################

Scenario: Theresa is on an initial 7 months main course at a higher education institute and does not have dependants.

    Given A Service is consuming the FSPS Calculator API
    When the FSPS Calculator API is invoked with the following
        | HEI               | Yes                            |
        | Course type       | Main course degree or higher   |
        | Dependants        | 0                              |
    Then The Financial Status API provides the following result:
        |Condition Code     | Applicant - 2                  |

################# Initial Main course degree or higher (HEI) - with dependants less than 12 months #######################

Scenario: Donald is on an initial 7 months main course at a higher education institute and has 2 dependants.

    Given A Service is consuming the FSPS Calculator API
    When the FSPS Calculator API is invoked with the following
        |  HEI                  | Yes                            |
        |  Course type          | Main course degree or higher   |
        |  Dependants           | 2                              |
        |  Course start date    | 2016-01-03                     |
        |  Course end date      | 2016-07-03                     |
    Then The Financial Status API provides the following results:
        |  Condition Code       | Applicant - 2                  |
                                | Partner - 3                    |
                                | Child - 1                      |

################# Initial Main course degree or higher (HEI) - with dependants 12 months or greater #######################

Scenario: Barack is on an initial 13 months main course at a higher education institute  and has 2 dependants.

    Given A Service is consuming the FSPS Calculator API
    When the FSPS Calculator API is invoked with the following
         |  HEI                  | Yes                            |
         |  Course type          | Main course degree or higher   |
         |  Dependants           | 2                              |
         |  Course start date    | 2016-01-03                     |
         |  Course end date      | 2017-01-03                     |
    Then The Financial Status API provides the following result:
         |  Condition Code       | Applicant - 2                  |
                                 | Partner - 4B                   |
                                 | Child - 1                      |

################# Pre Sessional degree or higher (HEI)  - applicant only  #######################

Scenario: Vladimir is on an initial 7 months pre-sessional at a higher education institute and does not have dependants.

    Given A Service is consuming the FSPS Calculator API
    When the FSPS Calculator API is invoked with the following
        | HEI               | Yes                         |
        | Course type       | Pre-sessional              |
        | Dependants        | 0                          |
        | Course start date | 2016-01-03                 |
        | Course end date   | 2016-10-10                 |
    Then The Financial Status API provides the following result:
        |Condition Code     |    Applicant - 2A          |

################# Pre Sessional degree or higher (HEI) with dependants #######################

Scenario: Hilary is on an initial 7 months pre-sessional course and has 1 dependant.

Given A Service is consuming the FSPS Calculator API
When the FSPS Calculator API is invoked with the following
        | HEI               | Yes                        |
        | Course type       | Pre-sessional              |
        | Dependants        | 1                          |
        | Course start date | 2016-01-03                 |
        | Course end date   | 2016-10-10                 |
Then The Financial Status API provides the following result:
        |Condition Code     |    Applicant - 2A          |
                            |    Partner - 3             |
                            |    Child - 1               |

################# Main course below degree (HEI)  - applicant only  #######################

Scenario: Margaret is on an initial 7 months below degree course at a higher education institute











