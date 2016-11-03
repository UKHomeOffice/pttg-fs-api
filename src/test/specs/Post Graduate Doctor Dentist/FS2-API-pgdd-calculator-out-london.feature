@wiremock
Feature: Total Funds Required Calculation - Tier 4 (General) Student Post Grad Doctor or Dentist out of London (single current account and no dependants)

    Acceptance criteria

    Requirement to meet Tier 4 Doctorate passed and not passed

    Not Inner London - The applicant must show evidence of funds to cover £1,015 for each month remaining of the course up to a maximum of 2 months

    Required Maintenance threshold calculation to pass this feature file
    Threshold =  (Required Maintenance funds doctorate inner London
    borough (£1015) * remaining course length) -  Accommodation fees already paid

    Tier 4 (General) Sudent - pgdd - out of London, In Country - (£1015 x 2) - £0 = £2030
    Tier 4 (General) Sudent - pgdd - out of London, In Country - (£1015 x 1) - £500 = £515

    Scenario: John's Threshold calculated
    He is on a 1 month doctorate extension
    He hasn't paid any accommodation fees
    He is studying out of London at Nottingham University


        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type                    | pgdd     |
            | In London                       | No       |
            | Course start date               | 2016-01-03|
            | Course end date                 | 2016-02-03|
            | Accommodation fees already paid | 0        |
        Then The Financial Status API provides the following results:
            | HTTP Status | 200     |
            | Threshold   | 2030.00 |


    Scenario: Ann's Threshold calculated
    She is on a 2 months doctorate extension
    She has paid £250.00 of her accommodation fees
    She is studying out of London at Nottingham University

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type                    | pgdd     |
            | In London                       | No       |
            | Course start date               | 2016-01-03|
            | Course end date                 | 2016-03-03|
            | Accommodation fees already paid | 250      |
        Then The Financial Status API provides the following results:
            | HTTP Status | 200     |
            | Threshold   | 1780.00 |
