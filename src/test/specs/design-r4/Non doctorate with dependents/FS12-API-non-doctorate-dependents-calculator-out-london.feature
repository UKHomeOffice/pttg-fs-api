Feature: Total Funds Required Calculation - Tier 4 (General) Student Non Doctorate Out of London with dependents(single current account)

    Requirement to meet Tier 4 pass

    Required Maintenance threshold regulation to pass this feature file
    Required Maintenance threshold non doctorate inner London borough = £1015 (the amount for when the student is studying in London)
    £680 per month per dependent and the same course length as a Tier 4 student – in London
    Course length - this can be within the period of 1-9 months
    Total tuition fees - total amount of the tuition fees for the course
    Tuition fees already paid -
    Accommodation fees already paid - The maximum amount paid can be £1015

    Required Maintenance threshold calculation to pass this feature file

    Maintenance threshold amount- (Required Maintenance funds non doctorate not inner London borough * course length) + (required maintenance funds * course length  * number of dependents) + (total tuition fees - tuition fees paid - accommodation fees paid)

    ((£1015 x 6) + (£680 x 6 x 1) + (£10,000 - £0 - £0))

    Tier 4 (General) Student - non doctorate - London, with dependents In Country - (£1015 x 6) + (£680 x 6 x 1) + (£10,000 - £0 - £0) = £20,170
    Tier 4 (General) Student - non doctorate - London, with dependents In Country - (£1015 x 9) + (£680 x 9 x 2) + (£7,000 - £300 - £500) = £33,775
    Tier 4 (General) Student - non doctorate - London, with dependents  In Country - (£1015 x 2) + (£680 x 2 x 3) + (£2,000 - £0 - £300.50) = £7,809.5

    Scenario: Tony's maintenance threshold amount calculated
    He is on a 5 month course
    His total tuition fees are £4550.50
    He has 2 dependents
    He is studying in London at LSE University

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type                    | nondoctorate |
            | Inner London Borough            | No          |
            | Course Length                   | 5            |
            | Total tuition fees              | 4550.50      |
            | Tuition fees already paid       | 0            |
            | Accommodation fees already paid | 0            |
            | Number of dependents            | 2            |
        Then The Financial Status API provides the following results:
            | HTTP Status                  | 200      |
            | Maintenance Threshold amount | 16425.50 |

    Scenario: Shelly's maintenance threshold amount calculated
    He is on a 3 month course
    His total tuition fees are £3750.00
    He has 4 dependents
    He is studying in London at LSE University

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type                    | nondoctorate |
            | Inner London Borough            | No          |
            | Course Length                   | 3            |
            | Total tuition fees              | 3750.00     |
            | Tuition fees already paid       | 250.50       |
            | Accommodation fees already paid | 300          |
            | Number of dependents            | 4            |
        Then The Financial Status API provides the following results:
            | HTTP Status                  | 200     |
            | Maintenance Threshold amount | 14404.50 |


