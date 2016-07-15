Feature: Total Funds Required Calculation - Tier 4 (General) Student Non Doctorate In London with dependents(single current account)

    Requirement to meet Tier 4 pass

    Required Maintenance threshold regulation to pass this feature file
    Required Maintenance threshold non doctorate inner London borough = £1265 (the amount for when the student is studying in London)
    £845 per month per dependent and the same course length as a Tier 4 student – in London
    Course length - this can be within the period of 1-9 months
    Total tuition fees - total amount of the tuition fees for the course
    Tuition fees already paid -
    Accommodation fees already paid - The maximum amount paid can be £1265

    Required Maintenance threshold calculation to pass this feature file

    Maintenance threshold amount- (Required Maintenance funds non doctorate inner London borough * course length) + (required maintenance funds * course length (845 x 6 x 1) * number of dependents) + (total tuition fees - tuition fees paid - accommodation fees paid)

    ((£1265 x 6) + (845 x 6 x 1) + (£10,000 - 0 - 0))

    Tier 4 (General) Student - non doctorate - London, with dependents In Country - (£1265 x 6) + (845 x 6 x 1) + (£10,000 - 0 - 0) = £22,660
    Tier 4 (General) Student - non doctorate - London, with dependents In Country - (£1265 x 9) + (845 x 9 x 2) + (£7,000 - 300 - 500) = £32,795
    Tier 4 (General) Student - non doctorate - London, with dependents  In Country - (£1265 x 2) + (845 x 2 x 3) + (£2,000 - 0 - 300.50) = £9,299.5

    Scenario: Tony's maintenance threshold amount calculated
    He is on a 6 month course
    His total tuition fees are £6,530.12
    He has 1 dependent
    He is studying in London at LSE University

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type                    | nondoctorate |
            | Inner London Borough            | Yes          |
            | Course Length                   | 6            |
            | Total tuition fees              | 6530.75      |
            | Tuition fees already paid       | 0            |
            | Accommodation fees already paid | 0            |
            | Number of dependents            | 1            |
        Then The Financial Status API provides the following results:
            | HTTP Status                  | 200      |
            | Maintenance Threshold amount | 19190.75 |

    Scenario: Shelly's maintenance threshold amount calculated
    He is on a 9 month course
    His total tuition fees are £12,500.00
    He has 3 dependents
    He is studying in London at LSE University

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type                    | nondoctorate |
            | Inner London Borough            | Yes          |
            | Course Length                   | 9            |
            | Total tuition fees              | 12500.00     |
            | Tuition fees already paid       | 250.50       |
            | Accommodation fees already paid | 300          |
            | Number of dependents            | 3            |
        Then The Financial Status API provides the following results:
            | HTTP Status                  | 200     |
            | Maintenance Threshold amount | 46149.5 |


