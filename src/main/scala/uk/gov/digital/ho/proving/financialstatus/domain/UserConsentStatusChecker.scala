package uk.gov.digital.ho.proving.financialstatus.domain

import java.time.LocalDate

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import uk.gov.digital.ho.proving.financialstatus.bank.BarclaysBankService

@Service
class UserConsentStatusChecker @Autowired()(barclaysBankService: BarclaysBankService) {

  def checkUserConsent(account: Account, dob: LocalDate, userId: String) = {
    barclaysBankService.checkUserConsent(account, dob, userId)
  }

}
