@wiremock
Feature: Total Funds Required Calculation - Tier 4 (General) Student Post Graduate Doctor Dentist In and Out of London (single current account and no dependants)

    Acceptance criteria

    Requirement to meet Tier 4 Doctorate passed and not passed

    Not Inner London - The applicant must show evidence of funds to cover £1,015 for each month remaining of the course up to a maximum of 2 months

    Required Maintenance threshold calculation to pass this feature file
    Threshold =  (Required Maintenance funds PGDD (Maintenance Funds required) * (remaining course length) -  (Accommodation fees already paid)

    Tier 4 (General) Sudent - pgdd - In London, In Country - (£1265 x 2) - £0 = £2530
    Tier 4 (General) Sudent - pgdd - In London, In Country - (£1265 x 1) - £1000 = £265

    Tier 4 (General) Sudent - pgdd - Out London, In Country - (£1015 x 2) - £0 = £2030
    Tier 4 (General) Sudent - pgdd - Out London, In Country - (£1015 x 1) - £500 = £515

 ##### In London ####

    Scenario: Tony's maintenance threshold amount calculated
    He is on a 1 month doctorate extension
    He hasn't paid any accommodation fees
    He is studying in London at LSE University


        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type                    | pgdd       |
            | In London                       | Yes        |
            | Course start date               | 2016-01-03 |
            | Course end date                 | 2016-02-03 |
            | Accommodation fees already paid | 0          |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 2530.00    |
            | Leave end date | 2016-03-03 |

    Scenario: Shelly's maintenance threshold amount calculated
    She is on a 2 months doctorate extension
    She has paid £250.00 of her accommodation fees
    She is studying in London at LSE University

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type                    | pgdd       |
            | In London                       | Yes        |
            | Course start date               | 2016-01-03 |
            | Course end date                 | 2016-03-03 |
            | Accommodation fees already paid | 0          |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 2530.00    |
            | Course Length  | 2          |
            | Leave end date | 2016-04-03 |

### Out London ###

    Scenario: John's Threshold calculated
    He is on a 1 month doctorate extension
    He hasn't paid any accommodation fees
    He is studying out of London at Nottingham University

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type                    | pgdd       |
            | In London                       | No         |
            | Course start date               | 2016-01-03 |
            | Course end date                 | 2016-02-03 |
            | Accommodation fees already paid | 0          |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 2030.00    |
            | Leave end date | 2016-03-03 |

    Scenario: Ann's Threshold calculated
    She is on a 2 months doctorate extension
    She has paid £250.00 of her accommodation fees
    She is studying out of London at Nottingham University

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type                    | pgdd       |
            | In London                       | No         |
            | Course start date               | 2016-01-03 |
            | Course end date                 | 2016-05-03 |
            | Accommodation fees already paid | 250        |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 1780.00    |
            | Course Length  | 2          |
            | Leave end date | 2016-06-03 |
