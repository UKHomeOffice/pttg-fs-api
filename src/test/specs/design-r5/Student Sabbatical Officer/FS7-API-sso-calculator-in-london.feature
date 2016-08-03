Feature: Total Funds Required Calculation - Tier 4 (General) Sabbatical Student Officer In London (single current account and no dependants)

    Acceptance criteria

    Requirement to meet Tier 4 Doctorate passed and not passed

    Inner London - The applicant must show evidence of funds to cover £1,265 for each month remaining of the course up to a maximum of 2 months

    Required Maintenance threshold calculation to pass this feature file
    Threshold =  (Required Maintenance funds doctorate inner London
    borough (£1265) * remaining course length) -  Accommodation fees already paid

    Tier 4 (General) Sudent - sso - London, In Country - (£1265 x 2) - £0 = £2530
    Tier 4 (General) Sudent - sso - London, In Country - (£1265 x 1) - £1000 = £265

    Scenario: Tony's Threshold calculated
    He is on a 1 month doctorate extension
    He hasn't paid any accommodation fees
    He is studying in London at LSE University


        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type                    | sso |
            | In London                       | Yes  |
            | Remaining course length         | 1    |
            | Accommodation fees already paid | 0    |
        Then The Financial Status API provides the following results:
            | HTTP Status                  | 200     |
            | Threshold | 1265.00 |


    Scenario: Shelly's Threshold calculated
    She is on a 2 months doctorate extension
    She has paid £250.00 of her accommodation fees
    She is studying in London at LSE University

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type                    | sso |
            | In London                       | Yes  |
            | Remaining course length         | 2    |
            | Accommodation fees already paid | 0    |
        Then The Financial Status API provides the following results:
            | HTTP Status                  | 200     |
            | Threshold | 2530.00 |
