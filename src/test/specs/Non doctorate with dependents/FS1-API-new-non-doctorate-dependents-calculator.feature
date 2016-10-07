Feature: Total Funds Required Calculation - New Tier 4 (General) Student Non Doctorate with dependents(single current account)

    Applicants Required Maintenance period - Course length (capped to 9 months)
    Dependants Required Maintenance period  - Course length + 2 months (capped to 9 months)
    Course length - this can be 7+ months ##this reflects the October 2016 policy change
    Total tuition fees - total amount of the tuition fees for the course
    Tuition fees already paid - total amount of tuition fees already paid
    Accommodation fees already paid - The maximum amount paid can be £1265

    Required Maintenance threshold calculation to pass this feature file

    Maintenance threshold amount- (Required Maintenance threshold non doctorate In London * Course length) + ((Dependants Required Maintenance threshold In London * Dependants Required Maintenance period)  * number of dependants) + (total tuition fees - tuition fees paid - accommodation fees paid)

    *********************************** In-London Business logic and Scenario ***********************************

Requirement to meet Tier 4 pass (New application only)

        Applicants Required Maintenance threshold non doctorate In London - £1265
        Dependants Required Maintenance threshold In London - £845

    ((£1265 x 7) + (845 x (7+2) x 1) + (£10,000 - 0 - 0))

    Tier 4 (General) Student - non doctorate - In London, with dependents In Country - (£1265 x 7) + (£845 x (7+2) x 1) + (£10,000 - £0 - £0) = £26,460
    Tier 4 (General) Student - non doctorate - In London, with dependents In Country - (£1265 x 8) + (£845 x (8+2) x 2) + (£7,000 - £300 - £500) = £31,530 (dependant require maintenance period capped at 9 months)
    Tier 4 (General) Student - non doctorate - In London, with dependents In Country - (£1265 x 9) + (£845 x (9+2) x 3) + (£2,000 - £0 - £300.50) = £35,899.50 (dependant require maintenance period capped at 9 months)

    Scenario: Tony's maintenance threshold amount calculated


        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type                    | nondoctorate |
            | In London                       | Yes          |
            | Course Length                   | 7            |
            | Total tuition fees              | 6530.75      |
            | Tuition fees already paid       | 0            |
            | Accommodation fees already paid | 0            |
            | Number of dependants            | 1            |
        Then The Financial Status API provides the following results:
            | HTTP Status                  | 200      |
            | Threshold | 22990.75 |

    Scenario: Shelly's maintenance threshold amount calculated

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type                    | nondoctorate |
            | In London                       | Yes          |
            | Course Length                   | 8            |
            | Total tuition fees              | 12500.00     |
            | Tuition fees already paid       | 250.50       |
            | Accommodation fees already paid | 300          |
            | Number of dependants            | 3            |
        Then The Financial Status API provides the following results:
            | HTTP Status                  | 200      |
            | Threshold | 44884.50 |

    Scenario: Rajinder's maintenance threshold amount calculated

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type                    | nondoctorate |
            | In London                       | Yes          |
            | Course Length                   | 9            |
            | Total tuition fees              | 2500.00      |
            | Tuition fees already paid       | 0            |
            | Accommodation fees already paid | 300          |
            | Number of dependants            | 2            |
        Then The Financial Status API provides the following results:
            | HTTP Status                  | 200      |
            | Threshold | 28795.00 |


        *********************************** Out-London Business logic and Scenario ***********************************

    Requirement to meet Tier 4 pass (new application only)

     Applicants Required Maintenance threshold non doctorate Out London - £1015
     Dependants Required Maintenance threshold Out London - £680

    ((£1015 x 7) + (680 x (7+2) x 1) + (£10,000 - 0 - 0))

    Tier 4 (General) Student - non doctorate - Out London, with dependents In Country - (£1015 x 7) + (£680 x (7+2) x 1) + (£10,000 - £0 - £0) = £23,225
    Tier 4 (General) Student - non doctorate - Out London, with dependents In Country - (£1015 x 8) + (£680 x (8+2) x 2) + (£7,000 - £300 - £500) = £26,560 (dependant require maintenance period capped at 9 months)
    Tier 4 (General) Student - non doctorate - Out London, with dependents In Country - (£1015 x 9) + (£680 x (9+2) x 3) + (£2,000 - £0 - £300.50) = £29,194.50 (dependant require maintenance period capped at 9 months)


    Scenario: Greg's maintenance threshold amount calculated


        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type                    | nondoctorate |
            | In London                       | No           |
            | Course Length                   | 7            |
            | Total tuition fees              | 4550.50      |
            | Tuition fees already paid       | 0            |
            | Accommodation fees already paid | 0            |
            | Number of dependants            | 2            |
        Then The Financial Status API provides the following results:
            | HTTP Status                  | 200      |
            | Threshold| 23895.50 |

    Scenario: Mitchell's maintenance threshold amount calculated


        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type                    | nondoctorate |
            | In London                       | No           |
            | Course Length                   | 8            |
            | Total tuition fees              | 3750.00      |
            | Tuition fees already paid       | 250.50       |
            | Accommodation fees already paid | 300          |
            | Number of dependants            | 4            |
        Then The Financial Status API provides the following results:
            | HTTP Status                  | 200      |
            | Threshold | 35,799.50 |


    Scenario: Miriam's maintenance threshold amount calculated


        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Student Type                    | nondoctorate |
            | In London                       | No           |
            | Course Length                   | 9            |
            | Total tuition fees              | 750.00       |
            | Tuition fees already paid       | 150.00       |
            | Accommodation fees already paid | 1265.00         |
            | Number of dependants            | 1            |
        Then The Financial Status API provides the following results:
            | HTTP Status                  | 200      |
            | Threshold | 14590.00 |
