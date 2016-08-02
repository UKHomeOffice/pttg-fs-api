@wiremock
Feature: Total Funds Required Calculation - Tier 4 (General) Student Doctorate out of London (single current account and no dependants)

    Acceptance criteria

    Requirement to meet Tier 4 Doctorate passed and not passed

    Not Inner London - The applicant must show evidence of funds to cover £1,015 for each month remaining of the course up to a maximum of 2 months

    Required Maintenance threshold calculation to pass this feature file
    Maintenance threshold amount =  (Required Maintenance funds doctorate outer London
    borough (£1015) * remaining course length) -  Accommodation fees already paid

    Tier 4 (General) Student - doctorate - out of London, In Country - (£1015 x 2) - £0 = £2030
    Tier 4 (General) Student - doctorate - out of London, In Country - (£1015 x 1) - £500 = £515

    Scenario: John's maintenance threshold amount calculated
    He is on a 1 month doctorate extension
    He hasn't paid any accommodation fees
    He is studying out of London at Nottingham University


        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type                    | doctorate |
            | In London                       | No        |
            | Remaining course length         | 1         |
            | Accommodation fees already paid | 0         |
        Then The Financial Status API provides the following results:
            | HTTP Status                  | 200     |
            | Maintenance Threshold amount | 1015.00 |


    Scenario: Ann's maintenance threshold amount calculated
    She is on a 2 months doctorate extension
    She has paid £250.00 of her accommodation fees
    She is studying out of London at Nottingham University

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type                    | doctorate |
            | In London                       | No        |
            | Remaining course length         | 2         |
            | Accommodation fees already paid | 250       |
        Then The Financial Status API provides the following results:
            | HTTP Status                  | 200     |
            | Maintenance Threshold amount | 1780.00 |

    Scenario: Mark's maintenance threshold amount calculated
    He is on a 12 month doctorate extension
    He hasn't paid any accommodation fees
    He is studying out of London at Nottingham University

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type                    | doctorate |
            | In London                       | No        |
            | Remaining course length         | 12        |
            | Accommodation fees already paid | 0         |
        Then The Financial Status API provides the following results:
            | HTTP Status                  | 200     |
            | Maintenance Threshold amount | 2030.00 |
            | Course Lenght                | 2       |

    Scenario: Mary's maintenance threshold amount calculated
    She is on a 8 months doctorate extension
    She has paid £450.00 of her accommodation fees
    She is studying out of London at Nottingham University

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type                    | doctorate |
            | In London                       | No        |
            | Remaining course length         | 21        |
            | Accommodation fees already paid | 450       |
        Then The Financial Status API provides the following results:
            | HTTP Status                  | 200     |
            | Maintenance Threshold amount | 1580.00 |
            | Course Lenght                | 2       |
