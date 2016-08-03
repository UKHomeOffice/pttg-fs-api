@wiremock
Feature: Total Funds Required Calculation - Tier 4 (General) Student Doctorate out of London (single current account and no dependants)

    Acceptance criteria

    Requirement to meet Tier 4 Doctorate passed and not passed

    Not Inner London - The applicant must show evidence of funds to cover £1,015 for 2 months (£2030)

    Required Maintenance threshold calculation to pass this feature file
    Maintenance threshold amount =  (Required Maintenance funds doctorate outer London
    borough (£1015) x 2) -  Accommodation fees already paid

    Tier 4 (General) Student - doctorate - out of London, In Country - (£1015 x 2) - £0 = £2030


    Scenario: Ann's maintenance threshold amount calculated
    She is on a  doctorate extension
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

