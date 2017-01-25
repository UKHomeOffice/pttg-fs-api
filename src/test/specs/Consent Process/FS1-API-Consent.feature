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
        And the default details are
            | Sort code      | 111111   |
            | Account number | 01078911   |
            | Date of birth  | 1987-03-25 |


    Scenario: 'Initiated' status returned in the Barclays Consent API response

        Given the test data for account 01078911
        When the Consent API is invoked
        Then the Barclays Consent API provides the following response:
            | status      | "INITIATED"                                            |
            | description | "Consent request has been initiated to Account-Holder" |

    Scenario: 'Pending' status returned in the Barclays Consent API response

        When the Consent API is invoked
        Then the Barclays Consent API provides the following response:
            | status      | "PENDING"                               |
            | description | "Awaiting response from Account-Holder" |

    Scenario: 'Success' status returned in the Barclays Consent API response

        When the Consent API is invoked
        Then the Barclays Consent API provides the following response::
            | status      | "SUCCESS                               |
            | description | "Consent received from Account-Holder" |

    Scenario: 'Failure' status returned in the Barclays Consent API response

        When the Consent API is invoked
        Then The Barclays Consent API provides the following response:
            | status      | "FAILURE"                        |
            | description | "Account-Holder refused consent" |

    Scenario:  Account Number does not match the data held at Barclays for that applicant

        Given the Consent API is invoked
        When an account number not found at Barclays
        Then The Barclays Consent API provides the following response:
            | Response Code        | 455           |
            | Response Description | No Data Found |

    Scenario:  Sort Code does not match the data held at Barclays for that applicant

        Given the Consent API is invoked
        When a sort code not found at Barclays
        Then The Barclays Consent API provides the following response:
            | Response Code        | 455           |
            | Response Description | No Data Found |

    Scenario:  Date of birth does not match the data held at Barclays for that applicant

        Given the Consent API is invoked
        When Date of birth is not found at Barclays
        Then The Barclays Consent API provides the following response:
            | Response Code        | 458                    |
            | Response Description | dateOfBirth is invalid |


    Scenario: Valid UK mobile number is not available

        Given the Consent API is invoked
        When Valid UK mobile number is not found at Barclays
        Then he Barclays Consent API provides the following response:
            | Response Code        | 446                      |
            | Response Description | Mobile Number is Invalid |