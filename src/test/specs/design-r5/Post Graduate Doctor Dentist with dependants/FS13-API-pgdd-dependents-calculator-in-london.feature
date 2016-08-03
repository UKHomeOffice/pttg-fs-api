Feature: Total Funds Required Calculation - Tier 4 (General) Student Post Grad Doctor or Dentist In London with dependents(single current account)

    Requirement to meet Tier 4 pass

    Required Maintenance threshold regulation to pass this feature file
    Required Maintenance threshold non doctorate In London = £1265 (the amount for when the student is studying in London)
    £845 per month per dependent and the same course length as a Tier 4 student – in London
    Course length - this can be within the period of 1-2 months
    Accommodation fees already paid - The maximum amount paid can be £1265

    Required Maintenance threshold calculation to pass this feature file
    Maintenance threshold amount =  (Required Maintenance funds doctorate inner London
    borough (£1265) * remaining course length) + (required dependant maintenance funds * course length  * number of dependants) -  Accommodation fees already paid

    Tier 4 (General) Sudent - pgdd - London, In Country - (£1265 x 2) + (£845 x 2 x 1) - £0 = £4220
    Tier 4 (General) Sudent - pgdd - London, In Country - (£1265 x 1) + (£845 x 1 x 1) - £1000 = £1,110

    Scenario: Tony's maintenance threshold amount calculated
    He is on a 1 month course
    He has 1 dependent
    No accommodation fees has been paid
    He is studying in London at LSE University

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student type                    | pgdd |
            | In London                       | Yes  |
            | Course Length                   | 1    |
            | Accommodation fees already paid | 0    |
            | Number of dependants            | 1    |
        Then The Financial Status API provides the following results:
            | HTTP Status                  | 200     |
            | Maintenance threshold amount | 2110.00 |

    Scenario: Shelly's maintenance threshold amount calculated
    He is on a 2 month course
    He has 3 dependents
    He has pad £250.50 for his accommodation fees
    He is studying in London at LSE University

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student type                    | pgdd   |
            | In London                       | Yes    |
            | Course length                   | 2      |
            | Accommodation fees already paid | 250.50 |
            | Number of dependants            | 3      |
        Then The Financial Status API provides the following results:
            | HTTP Status                  | 200     |
            | Maintenance threshold amount | 7349.50 |

