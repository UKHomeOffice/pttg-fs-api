Feature: Total Funds Required Calculation - Tier 4 (General) Student Non Doctorate out of London with dependents(single current account)

    Requirement to meet Tier 4 pass

    Required Maintenance threshold regulation to pass this feature file
    Required Maintenance threshold non doctorate inner London borough = £1015 (the amount for when the student is studying in London)
    £680 per month per dependent and the same course length as a Tier 4 student – in London
    Course length - this can be within the period of 1-2 months
    Accommodation fees already paid - The maximum amount paid can be £1265

    Required Maintenance threshold calculation to pass this feature file
    Maintenance threshold amount =  (Required Maintenance funds doctorate not inner London
    borough (£1015 * remaining course length) + (required maintenance funds * course length  * number of dependents) -  Accommodation fees already paid

    Tier 4 (General) Student - doctorate - London, In Country - (£1015 x 2) + (£680 x 2 x 1) - £0 = £3390
    Tier 4 (General) Student - doctorate - London, In Country - (£1015 x 1) + (£680 x 1 x 1) - £1000 = £695

    Scenario: Tony's maintenance threshold amount calculated
    He is on a 1 month course
    He has 3 dependents
    No accommodation fees has been paid
    He is studying in Leeds

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type                    | doctorate |
            | Inner London Borough            | Yes       |
            | Course Length                   | 1         |
            | Accommodation fees already paid | 0         |
            | Number of dependents            | 3         |
        Then The Financial Status API provides the following results:
            | HTTP Status                  | 200     |
            | Maintenance Threshold amount | 3055.00 |

    Scenario: Adam's maintenance threshold amount calculated
    He is on a 1 month course
    He has 1 dependents
    He has pad £100 for his accommodation fees
    He is studying in Nottingham

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type                    | doctorate |
            | Inner London Borough            | Yes       |
            | Course Length                   | 1         |
            | Accommodation fees already paid | 100.00    |
            | Number of dependents            | 1         |
        Then The Financial Status API provides the following results:
            | HTTP Status                  | 200     |
            | Maintenance Threshold amount | 1595.00 |


