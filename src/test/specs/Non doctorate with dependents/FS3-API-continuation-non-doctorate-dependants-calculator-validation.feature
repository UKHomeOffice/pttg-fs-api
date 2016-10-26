Feature: Validation of the API fields and data

    Course start date - (mandatory)
    Course end date - (mandatory)
    Continuation course end date - (optional)
    Course length - 0-9 months

    Background:
        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type                    | nondoctorate |
            | In London                       | Yes          |
            | Total tuition fees              | 3500.50      |
            | Tuition fees already paid       | 0            |
            | Accommodation fees already paid | 0            |
            | Number of dependants            | 1            |

######################### Validation on the Course start date field #########################

    Scenario: The API is not provided with the Course start date
        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Course start date            |            |
            | Course end date              | 2016-04-01|
            | Continuation end date | 2016-08-01|
        Then the service displays the following result
            | HTTP Status    | 400                                                        |
            | Status code    | 0004                                                       |
            | Status message | Parameter error: Invalid course Start date, must be a date |

    Scenario: The API is provided with a Course start date that is after the course end date
        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Course start date            | 2016-05-01|
            | Course end date              | 2016-04-01|
            | Continuation end date | 2016-08-01|
        Then the service displays the following result
            | HTTP Status    | 400                                                                                  |
            | Status code    | 0004                                                                                 |
            | Status message | Parameter error: Invalid course Start date, must be earlier than the course end date |

    Scenario: The API is provided with incorrect Course start date - not a date
        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Course start date               | x            |
            | Course end date                 | 2016-04-01  |
            | Continuation end date    | 2016-06-01  |
        Then the service displays the following result
            | HTTP Status    | 400                                                                   |
            | Status code    | 0002                                                                  |
            | Status message | Parameter conversion error: Invalid course start date, must be a date |

######################### Validation on the Course end date field #########################

    Scenario: The API is not provided with the Course end date
        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Course start date            | 2016-02-01|
            | Course end date              |            |
            | Continuation end date | 2016-08-01|
        Then the service displays the following result
            | HTTP Status    | 400                                                      |
            | Status code    | 0004                                                     |
            | Status message | Parameter error: Invalid course End date, must be a date |

    Scenario: The API is provided with a Course end date that is after the Contiuation course end date
        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Course start date            | 2016-05-01|
            | Course end date              | 2016-10-01|
            | Continuation end date | 2016-08-01|
        Then the service displays the following result
            | HTTP Status    | 400                                                                                             |
            | Status code    | 0004                                                                                            |
            | Status message | Parameter error: Invalid course End date, must be earlier than the Continuation course end date |

    Scenario: The API is provided with incorrect Course end date - not a date
        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Course start date               | 2016-02-01  |
            | Course end date                 | x            |
            | Continuation end date    | 2016-06-01  |
        Then the service displays the following result
            | HTTP Status    | 400                                                                 |
            | Status code    | 0002                                                                |
            | Status message | Parameter conversion error: Invalid course End date, must be a date |

 ######################### Validation on the Continuation course end date field #########################

    Scenario: The API is provided with a Course end date that is after the Contiuation course end date
        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Course start date            | 2016-05-01|
            | Course end date              | 2016-10-01|
            | Continuation end date | 2016-02-01|
        Then the service displays the following result
            | HTTP Status    | 400                                                                                             |
            | Status code    | 0004                                                                                            |
            | Status message | Parameter error: Invalid course Continuation course end date, must be after the Course end date |

    Scenario: The API is provided with incorrect Continuation course start date - not a date
        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Course start date               | 2016-02-01  |
            | Course end date                 | 2016-04-01  |
            | Continuation end date    | x            |
        Then the service displays the following result
            | HTTP Status    | 400                                                                                 |
            | Status code    | 0002                                                                                |
            | Status message | Parameter conversion error: Invalid Continuation course start date, must be a date |



