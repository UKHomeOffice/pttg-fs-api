Feature: Validation of the API fields and data

  National Insurance Numbers (NINO) - Format and Security: A NINO is made up of two letters, six numbers and a final letter (which is always A, B, C, or D)
  Date formats: Format should be yyyy-MM-dd

###################################### Section - Validation on the NINO ######################################

  Scenario: The API is not provided with an NINO (National Insurance Number)
    Given A service is consuming the Income Proving TM Family API
    When the Income Proving TM Family API is invoked with the following:
      | NINO                    |            |
      | Application Raised Date | 2015-01-01 |
    Then The Income Proving TM Family API provides the following result:
      | HTTP Status    | 200                |
      | Status code    | 200               |
      | Status message | Resource not found: /incomeproving/v1/individual//financialstatus |

