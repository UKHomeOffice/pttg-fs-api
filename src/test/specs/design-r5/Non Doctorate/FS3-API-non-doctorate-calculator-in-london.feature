@wiremock
Feature: Total Funds Required Calculation - Tier 4 (General) Student Non Doctorate In London (single current account and no dependants)

    Requirement to meet Tier 4 pass

    Required Maintenance threshold regulation to pass this feature file
    Required Maintenance threshold non doctorate In London = £1265 (the amount for when the student is studying in London)
    Course length - this can be within the period of 1-9 months
    Total tuition fees - total amount of the tuition fees for the course
    Tuition fees already paid -
    Accommodation fees already paid - The maximum amount paid can be £1265

    Required Maintenance threshold calculation to pass this feature file

    Maintenance threshold amount- (Required Maintenance funds non doctorate In London * course length) + (total tuition fees - tuition fees paid - accommodation fees paid)

    ((£1265 x 6) + (£10,000 - fees paid - accommodation fees))

    Tier 4 (General) Student - non doctorate - London, In Country - (£1265 x 6) + (£10,000 - 0 - 0) = £17,590
    Tier 4 (General) Student - non doctorate - London, In Country - (£1265 x 9) + (£7,000 - 300 - 500) = £17,585
    Tier 4 (General) Student - non doctorate - London, In Country - (£1265 x 2) + (£2,000 - 0 - 300.50) = £4,229.50

    Scenario: Tony's maintenance threshold amount calculated
    He is on a 6 month course
    His total tuition fees are £6,530.12
    He is studying in London at LSE University

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type                    | nondoctorate |
            | In London                       | Yes          |
            | Course Length                   | 6            |
            | Total tuition fees              | 6530.12      |
            | Tuition fees already paid       | 0            |
            | Accommodation fees already paid | 0            |
        Then The Financial Status API provides the following results:
            | HTTP Status                  | 200      |
            | Maintenance Threshold amount | 14120.12 |

    Scenario: Shelly's maintenance threshold amount calculated
    He is on a 9 month course
    His total tuition fees are £12,500.00
    He is studying in London at LSE University

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type                    | nondoctorate |
            | In London                       | Yes          |
            | Course Length                   | 9            |
            | Total tuition fees              | 12500.00     |
            | Tuition fees already paid       | 250.50       |
            | Accommodation fees already paid | 300          |
        Then The Financial Status API provides the following results:
            | HTTP Status                  | 200      |
            | Maintenance Threshold amount | 23334.50 |


    Scenario: Paul's maintenance threshold amount calculated
    He is on a 12 month course
    His total tuition fees are £12,500.00
    He is studying in London at LSE University

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type                    | nondoctorate |
            | In London                       | Yes          |
            | Course Length                   | 9            |
            | Total tuition fees              | 12500.00     |
            | Tuition fees already paid       | 250.50       |
            | Accommodation fees already paid | 575.25       |
        Then The Financial Status API provides the following results:
            | HTTP Status                  | 200      |
            | Maintenance Threshold amount | 23059.25 |


    Scenario: Peter's maintenance threshold amount calculated
    He is on a 120 month course
    His total tuition fees are £12,500.00
    He is studying in London at LSE University

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type                    | nondoctorate |
            | In London                       | Yes          |
            | Course Length                   | 120            |
            | Total tuition fees              | 12500.00     |
            | Tuition fees already paid       | 250.50       |
            | Accommodation fees already paid | 575.25       |
        Then The Financial Status API provides the following results:
            | HTTP Status                  | 200      |
            | Maintenance Threshold amount | 23059.25 |
