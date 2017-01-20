package uk.gov.digital.ho.proving.financialstatus.domain

import java.time.LocalDate

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import uk.gov.digital.ho.proving.financialstatus.acl.BankService

@Service
class UserConsentStatusChecker @Autowired()(bankService: BankService) {

  def checkUserConsent(accountId: String, sortCode: String, accountNumber: String, dob: LocalDate, userId: String) = {
    val response = bankService.checkUserConsent(accountId, sortCode, accountNumber, dob, userId)
    response
  }

}
