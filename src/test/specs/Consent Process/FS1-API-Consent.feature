Feature: Outgoing API request to the Barclays Consent API and handling the incoming response.

    There are two outgoing requests to Barclays Consent API and these are used to trigger
    (1) the SMS to the applicant
    (2) obtain the applicant consent status (Initiated, Pending, Success, Failure)

    Sort code – 6 digits – sort code of the applicants current account
    Account number – 8 digits – account number of the applicants current account an Account number will be padded with leading zeroes to ensure it to be 8 digits
    Date of Birth of the applicant in DD-MMM-YYYY format
    User ID – the unique identifier of the user

    Background:
        Given A Service is consuming the FSPS Calculator API
        And the service is consuming the Barclays Balances API
#        And the default details are
#            | Sort code      | 111111   |
#            | Account number | 01078911   |
#            | Date of birth  | 1987-03-25 |


    Scenario: 'Initiated' status returned in the Barclays Consent API response

        Given the test data for account 01078914
        When the Consent API is invoked
        And the default details are
            | Sort code      | 111111     |
            | Account number | 01078914   |
            | Date of birth  | 1987-03-25 |
        Then the Barclays Consent API provides the following response:
            | Consent     | INITIATED                                            |
            | Description | Consent request has been initiated to Account-Holder |


    Scenario: 'Pending' status returned in the Barclays Consent API response
        Given the test data for account 01078915
        When the Consent API is invoked
        And the default details are
            | Sort code      | 111111     |
            | Account number | 01078915   |
            | Date of birth  | 1987-03-25 |
        Then the Barclays Consent API provides the following response:
            | Consent     | PENDING                               |
            | Description | Awaiting response from Account-Holder |

    Scenario: 'Success' status returned in the Barclays Consent API response

        Given the test data for account 01078916
        When the Consent API is invoked
        And the default details are
            | Sort code      | 111111     |
            | Account number | 01078916   |
            | Date of birth  | 1987-03-25 |
        Then the Barclays Consent API provides the following response:
            | Consent     | SUCCESS                               |
            | Description | Awaiting response from Account-Holder |

    Scenario: 'Failure' status returned in the Barclays Consent API response

        Given the test data for account 01078917
        When the Consent API is invoked
        And the default details are
            | Sort code      | 111111     |
            | Account number | 01078917   |
            | Date of birth  | 1987-03-25 |
        Then the Barclays Consent API provides the following response:
            | Consent     | FAILURE                        |
            | Description | Account-Holder refused consent |

    Scenario:  Account Number does not match the data held at Barclays for that applicant

        Given the test data for account 01078918
        Given the Consent API is invoked
        When an account number not found at Barclays
        And the default details are
            | Sort code      | 111111     |
            | Account number | 01078918   |
            | Date of birth  | 1987-03-25 |
        Then the Barclays Consent API provides the following response:
            | Response Code        | 404                                                         |
            | Response Description | No records for sort code 111111 and account number 01078918 |

    Scenario:  Sort Code does not match the data held at Barclays for that applicant

        Given the test data for account 22-22-22
        Given the Consent API is invoked
        When a sort code not found at Barclays
        And the default details are
            | Sort code      | 222222     |
            | Account number | 01078918   |
            | Date of birth  | 1987-03-25 |
        Then the Barclays Consent API provides the following response:
            | Response Code        | 404                                                         |
            | Response Description | No records for sort code 222222 and account number 01078918 |

    Scenario:  Date of birth does not match the data held at Barclays for that applicant

        Given the test data for account 01078918
        Given the Consent API is invoked
        When Date of birth is not found at Barclays
        And the default details are
            | Sort code      | 111111     |
            | Account number | 01078918   |
            | Date of birth  | 1987-03-25 |
        Then the Barclays Consent API provides the following response:
            | Response Code        | 404                                                         |
            | Response Description | No records for sort code 111111 and account number 01078918 |


    Scenario: Valid UK mobile number is not available

        Given the test data for account 01078918
        Given the Consent API is invoked
        When Valid UK mobile number is not found at Barclays
        And the default details are
            | Sort code      | 111111     |
            | Account number | 01078918   |
            | Date of birth  | 1987-03-25 |
        Then the Barclays Consent API provides the following response:
            | Response Code        | 404                                                         |
            | Response Description | No records for sort code 111111 and account number 01078918 |
