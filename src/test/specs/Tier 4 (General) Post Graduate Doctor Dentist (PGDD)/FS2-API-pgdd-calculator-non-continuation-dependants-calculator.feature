Feature: Total Funds Required Calculation - Tier 4 (General) Student Post Grad Doctor or Dentist In & Out London with dependents(single current account)

    Requirement to meet Tier 4 pass

    Required Maintenance threshold regulation to pass this feature file
    Required Maintenance threshold non doctorate In London = £1265 (the amount for when the student is studying in London)
    £845 per month per dependent and the same course length as a Tier 4 student – in London
    Course length - this can be within the period of 1-2 months
    Accommodation fees already paid - The maximum amount paid can be £1265

    Required Maintenance threshold calculation to pass this feature file
    Threshold =  (Required Maintenance funds doctorate inner London
    borough (£1265) * remaining course length) + (required dependant maintenance funds * course length  * number of dependants) -  Accommodation fees already paid

    Tier 4 (General) Sudent - pgdd - London, In Country - (£1265 x 2) + (£845 x 2 x 1) - £0 = £4220
    Tier 4 (General) Sudent - pgdd - London, In Country - (£1265 x 1) + (£845 x 1 x 1) - £1000 = £1,110

    Tier 4 (General) Sudent - pgdd - out of London, In Country - (£1015 x 2) + (£680 x 2 x 1) - £0 = £3390
    Tier 4 (General) Sudent - pgdd - out of London, In Country - (£1015 x 1) + (£680 x 1 x 1) - £1000 = £695

### In London ###

    Scenario: Tony's Threshold calculated
    He is on a 1 month course
    He has 1 dependent
    No accommodation fees has been paid
    He is studying in London at LSE University

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student type                    | pgdd       |
            | In London                       | Yes        |
            | Course start date               | 2016-01-03 |
            | Course end date                 | 2016-02-03 |
            | Accommodation fees already paid | 0          |
            | dependants                      | 1          |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 4220.00    |
            | Leave end date | 2016-03-03 |


    Scenario: Shelly's Threshold calculated
    He is on a 2 month course
    He has 3 dependents
    He has pad £250.50 for his accommodation fees
    He is studying in London at LSE University

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student type                    | pgdd       |
            | In London                       | Yes        |
            | Course start date               | 2016-01-03 |
            | Course end date                 | 2016-03-03 |
            | Accommodation fees already paid | 250.50     |
            | dependants                      | 3          |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 7349.50    |
            | Course Length  | 2          |
            | Leave end date | 2016-04-03 |

#### Out London ####

    Scenario: Tony's Threshold calculated
    He is on a 1 month course
    He has 3 dependents
    No accommodation fees has been paid
    He is studying in Leeds

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student type                    | pgdd       |
            | In London                       | No         |
            | Course start date               | 2016-01-03 |
            | Course end date                 | 2016-02-03 |
            | Accommodation fees already paid | 0          |
            | dependants                      | 3          |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 6110.00    |
            | Leave end date | 2016-03-03 |

    Scenario: Adam's Threshold calculated
    He is on a 9 month course
    He has 1 dependents
    He has pad £100 for his accommodation fees
    He is studying in Nottingham

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student type                    | pgdd       |
            | In London                       | No         |
            | Course start date               | 2016-01-03 |
            | Course end date                 | 2016-08-03 |
            | Accommodation fees already paid | 100.00     |
            | dependants                      | 1          |
        Then The Financial Status API provides the following results:
            | HTTP Status    | 200        |
            | Threshold      | 3290.00    |
            | Course Length  | 2          |
            | Leave end date | 2016-09-03 |
