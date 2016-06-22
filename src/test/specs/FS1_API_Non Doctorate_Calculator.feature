Feature: Total Funds Required Calculation - Tier 4 (General) Student Non Doctorate In London (single current account and no dependants)

  Requirement to meet Tier 4 pass

  Required Maintenance threshold regulation to pass this feature file
  Required Maintenance threshold non doctorate inner London borough = £1265 (the amount for when the student is studying in London)
  Course length - this can be within the period of 1-9 months
  Total tuition fees - total amount of the tuition fees for the course

  Required Maintenance threshold calculation to pass this feature file

  Maintenance threshold amount- (Required Maintenance funds non doctorate inner London borough * course length) + total tuition fees

  Tier 4 (General) Student - non doctorate - London, In Country - (£1265 x 6) + £10,000 = £17,590
  Tier 4 (General) Student - non doctorate - London, In Country - (£1265 x 9) + £7,000 = £18,385
  Tier 4 (General) Student - non doctorate - London, In Country - (£1265 x 2) + £2,000 = £4,530

    Scenario: Tony's maintenance threshold amount calculated
    He is on a 6 month course
    His total tuition fees are £6,530.12
    He is studying in London at LSE University

        Given A service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Inner London Borough | Yes     |
            | Course Length        | 6       |
            | Total tuition fees   | 6530.12 |
        Then the service displays the following result
            | HTTP Status                  | 200      |
            | Maintenance Threshold amount | 14120.12 |

    Scenario: Shelly's maintenance threshold amount calculated
    He is on a 9 month course
    His total tuition fees are £12,500.00
    He is studying in London at LSE University

        Given A service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Inner London Borough | Yes      |
            | Course Length        | 9        |
            | Total tuition fees   | 12500.00 |
        Then the service displays the following result
            | HTTP Status                  | 200      |
            | Maintenance Threshold amount | 23885.00 |
