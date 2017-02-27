Feature: Total Funds Required Calculation - Tier 4 (General) Doctorate Extension Scheme with and without dependants (single current account)

    Continuation or pre-sessional courses do not apply to the Doctorate Extension Scheme route
    Main applicants Required Maintenance period - always 2 months - regardless of course length
    Dependants Required Maintenance period - always 2 months - regardless of course length or leave length

    Applicants Required Maintenance threshold general:  In London - £1265, Out London - £1015
    Dependants Required Maintenance threshold: In London - £845, Out London - £680

    Accommodation fees already paid - The maximum amount paid can be £1265

    Background:
        Given A Service is consuming the FSPS Calculator API
        And the default details are
            | Student Type                    | des |
            | In London                       | Yes |
            | Accommodation fees already paid | 500 |
            | Dependants only                 | No  |

    #Required Maintenance threshold calculation to pass this feature file

    #Maintenance threshold amount = (Required Maintenance threshold general * 2) +
    #((Dependants Required Maintenance threshold * 2)  * number of dependants) - (accommodation fees paid)

    #DES course:
    #12 months: ((£1265 x 2) + (£845 x 2 x 1) - (£50)
    #7 months: ((£1265 x 2) + (845 x 2 x 2) - (£100)
    #1 month: ((£1265 x 2) + (£845 x 2 x 3) - (£100) #### check with devs ####

    #Main course worked examples:

    #12 months: Tier 4 (General) Student - des - In London, with dependents In Country - (£1265 x 2) + (£845 x 2 x 1) - (£50) = £3,325
    #6 months: Tier 4 (General) Student - des - In London, with dependents In Country - (£1265 x 2) + (£845 x 2 x 2) - (£100) = £5,810
    #1 month: Tier 4 (General) Student - des - In London, with dependents In Country - (£1265 x 2) + (£845 x 2 x 3) - (£100) = £7500

    #### DES course ####

    Scenario: John is on a 2 month DES course. John's Threshold calculated

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type | des |
            | Dependants   | 0   |
        Then The Financial Status API provides the following results:
            | HTTP Status | 200     |
            | Threshold   | 2030.00 |

    Scenario: Ann is on a 3 month DES continuation course and has 2 dependants. Ann's Threshold calculated

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type | des |
            | In London    | No  |
            | Dependants   | 2   |
        Then The Financial Status API provides the following results:
            | HTTP Status | 200     |
            | Threshold   | 4250.00 |

    Scenario: Alvin is on a 5 month DES continuation course and has 4 dependants. Alvin's Threshold calculated

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type | des |
            | Dependants   | 4   |
        Then The Financial Status API provides the following results:
            | HTTP Status | 200     |
            | Threshold   | 8790.00 |

    Scenario: Kira is on a 1 month DES continuation course and has 1 dependant. Kira's Threshold calculated

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type | des |
            | Dependants   | 1   |
        Then The Financial Status API provides the following results:
            | HTTP Status | 200     |
            | Threshold   | 3720.00 |

    # DES course - Dependants Only #
    # fixed at 2 months ##

#    Background:
#        Given A Service is consuming the FSPS Calculator API
#        And the default details are
#            | Student Type                    | des |

    Scenario: 2 dependants only application (main applicant is on a 3 month DES continuation course) - Out of London

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type    | des |
            | In London       | No  |
            | Dependants      | 2   |
            | Dependants only | Yes |
        Then The Financial Status API provides the following results:
            | HTTP Status | 200  |
            | Threshold   | 2720 |

    Scenario: 4 dependants only application (main applicant is on a 5 month DES continuation course) - In London

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type    | des |
            | In London       | Yes |
            | Dependants      | 4   |
            | Dependants only | Yes |
        Then The Financial Status API provides the following results:
            | HTTP Status | 200     |
            | Threshold   | 6760.00 |

    Scenario: 1 dependant only application (main applicant is on 1 month DES continuation course) - In London

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type    | des |
            | In London       | Yes |
            | Dependants      | 1   |
            | Dependants only | Yes |
        Then The Financial Status API provides the following results:
            | HTTP Status | 200     |
            | Threshold   | 1690.00 |
