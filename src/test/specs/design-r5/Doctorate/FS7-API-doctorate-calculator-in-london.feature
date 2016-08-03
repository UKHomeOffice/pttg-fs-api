Feature: Total Funds Required Calculation - Tier 4 (General) Student Doctorate In London (single current account and no dependants)

    Acceptance criteria

    Requirement to meet Tier 4 Doctorate passed and not passed

    Inner London - The applicant must show evidence of funds to cover £1,265 for 2 months (2,530)

    Required Maintenance threshold calculation to pass this feature file
    Maintenance threshold amount =  (Required Maintenance funds doctorate inner London
    borough (£1265) x 2) -  Accommodation fees already paid

    Tier 4 (General) Student - doctorate - London, In Country - (£1265 x 2) - £0 = £2530


    Scenario: Shelly's maintenance threshold amount calculated
    She is on a doctorate extension
    She has paid £0.00 of her accommodation fees
    She is studying in London at LSE University

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type                    | doctorate |
            | In London                       | Yes       |
            | Remaining course length         | 2         |
            | Accommodation fees already paid | 0         |
        Then The Financial Status API provides the following results:
            | HTTP Status                  | 200     |
            | Threshold  | 2530.00 |
