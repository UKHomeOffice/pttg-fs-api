Feature: Total Funds Required Calculation - Continuation Tier 5 General applicant with and without dependants (single current account)

    Main applicants Required Maintenance (where applicant is either Tier 5 'temporary') - £945 #
    Main applicants Required Maintenance (where applicant is Tier 5 'Youth Mobility Scheme') - £1890 #
    Dependants Required Maintenance - £630 (per dependant where applicant is Tier 5 'temporary')
    Dependants can apply without the main applicant on tier 5 temporary #
    Dependants are not allowed in the tier 5 youth mobility scheme #

    Required Maintenance threshold calculation to pass this feature file:

    Main applicant required maintenance + (dependants maintenance maintenance * number of dependants)

    Worked examples:

    Tier 5 temporary workers applicant without dependent (£945 x 1) + (£630 x 0) = £956
    Tier 5 temporary worker applicant with dependant (£945 x 1) + (£630 x 1) = £1575
    Tier 5 temporary worker applicant with 2 dependants (£945 x 1) + (£630 x 2) = £2205
    Tier 5 temporary worker dependant only (£945 x 0) + (£630 x 1) = £630
    Tier 5 temporary worker multiple dependant only (£630 x 3) = £1890
    Tier 5 youth mobility scheme without dependant (£1890 x 1) = £1890 #

    ###### Tier 5 temporary Main Applicant with & without dependants ########

    Scenario: Leo is Tier 5 temporary applicant. Leo's maintenance threshold amount calculated.

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator Tier_Five API is invoked with the following
            | Applicant type | Main      |
            | Variant        | Temporary |
            | Dependants     | 0         |
        Then The Tier_five Financial Status API provides the following results:
            | HTTP Status | 200    |
            | Threshold   | 945.00 |

    Scenario: Fran is Tier 5 Temporary worker applicant wit a dependant. Fran's maintenance threshold amount calculated.

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator Tier_Five API is invoked with the following
            | Applicant type | Main      |
            | Variant type   | Temporary |
            | Dependants     | 1         |
        Then The Tier_five Financial Status API provides the following results:
            | HTTP Status | 200     |
            | Threshold   | 1575.00 |

    Scenario: Karen is Tier 5 temporary worker applicant with 2 dependants. Karen's maintenance threshold amount calculated.

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator Tier_Five API is invoked with the following
            | Applicant type | Main      |
            | Variant        | Temporary |
            | Dependants     | 2         |
        Then The Tier_five Financial Status API provides the following results:
            | HTTP Status | 200     |
            | Threshold   | 2205.00 |


    ######################## Tier 5 Temporary Worker without Main Applicant ################################

    Scenario: Stu is Tier 5 temporary applicant dependant only. Stu's maintenance threshold amount calculated.

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator Tier_Five API is invoked with the following
            | Applicant type | Dependant |
            | Dependants     | 1         |
        Then The Tier_five Financial Status API provides the following results:
            | HTTP Status | 200    |
            | Threshold   | 630.00 |


        ########## Tier 5 temporary without Main Applicant - multiple applicants (x3) #####

    Scenario: Simon, Alvin and Theodore are multiple (x3) Tier 5 dependant only applicants.

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator Tier_Five API is invoked with the following
            | Applicant type | Dependant |
            | Dependants     | 3         |
        Then The Tier_five Financial Status API provides the following results:
            | HTTP Status | 200     |
            | Threshold   | 1890.00 |

        ##################### Tier 5 Main Applicant Youth Mobility Scheme #############################

    @ignore
    Scenario: John Terry is Tier 5 youth mobility scheme applicant. John's maintenance threshold amount calculated.

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator Tier_Five API is invoked with the following
            | Applicant Type | Main  |
            | Variant Type   | Youth |
        Then The Tier_five Financial Status API provides the following results:
            | HTTP Status | 200     |
            | Threshold   | 1890.00 |
