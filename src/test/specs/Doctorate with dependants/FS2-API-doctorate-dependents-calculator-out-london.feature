Feature: Total Funds Required Calculation - Tier 4 (General) Student Doctorate out of London with dependents(single current account)

    Requirement to meet Tier 4 pass

    Required Maintenance threshold regulation to pass this feature file
    Required Maintenance threshold non doctorate out of London borough = £1015 for 2 months (the amount for when the student is studying in London)
    £680 per month for 2 months per dependent and the same course length as a Tier 4 student – out of London
    Accommodation fees already paid - The maximum amount paid can be £1265

    Required Maintenance threshold calculation to pass this feature file
    Maintenance threshold amount =  (Required Maintenance funds doctorate not inner London
    borough (£1015 * 2) + (required dependant maintenance funds * 2  * number of dependants) -  Accommodation fees already paid

    Tier 4 (General) Student - doctorate - out of London, In Country - (£1015 x 2) + (£680 x 2 x 1) - £0 = £3390

    Scenario: Tony's maintenance threshold amount calculated
    He is on a 2 month course
    He has 3 dependents
    No accommodation fees has been paid
    He is studying in Leeds

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student type                    | doctorate |
            | In London                       | No        |
            | Accommodation fees already paid | 0         |
            | dependants            | 3         |
        Then The Financial Status API provides the following results:
            | HTTP Status                  | 200  |
            | Threshold | 6110.00 |

