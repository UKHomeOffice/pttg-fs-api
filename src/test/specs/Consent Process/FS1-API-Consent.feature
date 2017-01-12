Feature: Outgoing API request to the Barclays Consent API and handling the incoming responses

    There are two outgoing requests to Barclays Consent APi and these are used to trigger (1) the SMS to the applicant and (2) to request the consent status.

    Sort code – 6 digits – sort code of the applicants current account
    Account number – 8 digits – account number of the applicants current account an Account number will be padded with leading zeroes to ensure it to be 8 digits
    Date of Birth of the applicant in DD-MMM-YYYY format
    User ID – the unique identifier of the user


    Background:
        Given A Service is consuming the FSPS Calculator API
        And the default details are
            | Sort code      |             |
            | Account number |             |
            | Date of birth  | DD-MMM-YYYY |
            | User ID        |             |

    Scenario: Outgoing API call and initiated response returned

        Given A Service is consuming the Barclays Consent API
        When the Consent API is invoked
        Then The Barclays Consent API provides the following response:
            | status      | "INITIATED"                                            |
            | description | "Consent request has been initiated to Account-Holder" |

    Scenario: Outgoing API call and pending response returned
        Given A Service is consuming the Barclays Consent API
        When the Consent API is invoked
        Then The Barclays Consent API provides the following response:
            | status      | "PENDING"                               |
            | description | "Awaiting response from Account-Holder" |

    Scenario: Outgoing API call and consent granted response returned
        Given A Service is consuming the Barclays Consent API
        When the Consent API is invoked
        Then The Barclays Consent API provides the following response::
            | status      | "SUCCESS                               |
            | description | "Consent received from Account-Holder" |

    Scenario: Outgoing API call and consent not granted response returned
        Given A Service is consuming the Barclays Consent API
        When the Consent API is invoked
        Then The Barclays Consent API provides the following response:
            | status      | "FAILURE"                        |
            | description | "Account-Holder refused consent" |

    Scenario:  Account Number field does not match between the request API and data held at Barclays for that applicant
        Given A Service is consuming the Barclays Consent API
        And an account number not found at Barclays
        When the Consent API is invoked
        Then The Barclays Consent API provides the following response:
            | Response Code        | 455           |
            | Response Description | No Data Found |


    Scenario:  Sort Code field does not match between the request API and data held at Barclays for that applicant
        Given A Service is consuming the Barclays Consent API
        And a sort code not found at Barclays
        When the Consent API is invoked
        Then The Barclays Consent API provides the following response:
            | Response Code        | 455           |
            | Response Description | No Data Found |


    Scenario:  Date of birth field does not match between the request API and data held at Barclays for that applicant
        Given A Service is consuming the Barclays Consent API
        And  Date of birth not found at Barclays
        When the Consent API is invoked
        Then The Barclays Consent API provides the following response:
            | Response Code        | 458                    |
            | Response Description | dateOfBirth is invalid |
