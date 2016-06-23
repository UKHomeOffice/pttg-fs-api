Feature: Total Funds Required Calculation - Tier 4 (General) Student Non Doctorate out of London (single current account and no dependants)

  Requirement to meet Tier 4 pass

  Required Maintenance threshold regulation to pass this feature file
  Required Maintenance threshold non doctorate not inner London borough = £1015 (the amount for when the student is studying in London)
  Course length - this can be within the period of 1-9 months
  Total tuition fees - total amount of the tuition fees for the course

  Required Maintenance threshold calculation to pass this feature file

  Maintenance threshold amount- (Required Maintenance funds non doctorate not inner London borough * course length) + total tuition fees

  Tier 4 (General) Student - non doctorate - out of London, In Country - (£1015 x 6) + £10,000 = £16,090
  Tier 4 (General) Student - non doctorate - out of London, In Country - (£1015 x 9) + £7,000 = £16,1358
  Tier 4 (General) Student - non doctorate - out of London, In Country - (£1015 x 2) + £2,000 = £4,030

  Scenario: Robs maintenance threshold amount calculated
  He is on a 2 month course
  His total tuition fees are £3,500.50
  He is studying in Nottingham, Nottingham Trent


    Given A Service is consuming the FSPS Calculator API
    When the FSPS Calculator API is invoked with the following
      | Inner London Borough     | No        |
      | Course Length            | 2          |
      | Total tuition fees       | 3500.50    |
    Then the service displays the following result
      | HTTP Status                  | 200     |
      | Maintenance Threshold amount | 5530.50 |

  Scenario: Nick's maintenance threshold amount calculated
  He is on a 9 month course
  His total tuition fees are £9,355.00
  He is studying in Nottingham, Nottingham Trent

    Given A Service is consuming the FSPS Calculator API
    When the FSPS Calculator API is invoked with the following
      | Inner London Borough     | No        |
      | Course Length            | 9          |
      | Total tuition fees       | 9355.00    |
    Then the service displays the following result
      | HTTP Status                  | 200      |
      | Maintenance Threshold amount | 18490.00 |
