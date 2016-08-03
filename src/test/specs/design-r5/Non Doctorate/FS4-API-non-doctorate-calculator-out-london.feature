@wiremock
Feature: Total Funds Required Calculation - Tier 4 (General) Student Non Doctorate out of London (single current account and no dependants)

    Requirement to meet Tier 4 pass

    Required Maintenance threshold regulation to pass this feature file
    Required Maintenance threshold non doctorate not In London = £1015 (the amount for when the student is studying in London)
    Course length - this can be within the period of 1-9 months
    Total tuition fees - total amount of the tuition fees for the course
    Tuition fees already paid -
    Accommodation fees already paid - The maximum amount paid can be £1265

    Required Maintenance threshold calculation to pass this feature file

    Maintenance threshold amount- (Required Maintenance funds non doctorate not In London * course length) + total tuition fees

    Tier 4 (General) Student - non doctorate - out of London, In Country - (£1015 x 6) + (£10,000 - 0 - 0) = £16,090
    Tier 4 (General) Student - non doctorate - out of London, In Country - (£1015 x 9) + (£7,000 - 300 - 500) = £15,335
    Tier 4 (General) Student - non doctorate - out of London, In Country - (£1015 x 2) + (£2,000 - 0 - 300.50)= £4,030

    Scenario: Robs maintenance threshold amount calculated
    He is on a 2 month course
    His total tuition fees are £3,500.50
    He is studying in Nottingham, Nottingham Trent


        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type                    | nondoctorate |
            | In London                       | No           |
            | Course Length                   | 2            |
            | Total tuition fees              | 3500.50      |
            | Tuition fees already paid       | 0            |
            | Accommodation fees already paid | 0            |
        Then The Financial Status API provides the following results:
            | HTTP Status                  | 200     |
            | Threshold  | 5530.50 |

    Scenario: Nick's maintenance threshold amount calculated
    He is on a 9 month course
    His total tuition fees are £9,355.00
    He is studying in Nottingham, Nottingham Trent

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type                    | nondoctorate |
            | In London                       | No           |
            | Course Length                   | 9            |
            | Total tuition fees              | 9355.00      |
            | Tuition fees already paid       | 500          |
            | Accommodation fees already paid | 600.50       |
        Then The Financial Status API provides the following results:
            | HTTP Status                  | 200      |
            | Threshold  | 17389.50 |
