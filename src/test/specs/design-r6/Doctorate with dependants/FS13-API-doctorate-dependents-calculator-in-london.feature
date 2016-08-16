Feature: Total Funds Required Calculation - Tier 4 (General) Student Doctorate In London with dependents(single current account)

    Requirement to meet Tier 4 pass

    Required Maintenance threshold regulation to pass this feature file
    Required Maintenance threshold doctorate In London = £1265 for 2 months (the amount for when the student is studying in London)
    £845 per month for 2 months per dependent and the same course length as a Tier 4 student – in London
    Accommodation fees already paid - The maximum amount paid can be £1265

    Required Maintenance threshold calculation to pass this feature file
    Maintenance threshold amount =  (Required Maintenance funds doctorate inner London
    borough (£1265) x 2) + (required dependant maintenance funds * 2 * number of dependants) -  Accommodation fees already paid

    Tier 4 (General) Student - doctorate - London, In Country - (£1265 x 2) + (£845 x 2 x 1) - £0 = £4220


    Scenario: Shelly's maintenance threshold amount calculated
    He is on a 2 month course
    He has 3 dependents
    He has pad £250.50 for his accommodation fees
    He is studying in London at LSE University

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student type                    | doctorate |
            | In London                       | Yes       |
            | Accommodation fees already paid | 250.50    |
            | Number of dependants            | 3         |
        Then The Financial Status API provides the following results:
            | HTTP Status                  | 200     |
            | Threshold| 7349.50 |

