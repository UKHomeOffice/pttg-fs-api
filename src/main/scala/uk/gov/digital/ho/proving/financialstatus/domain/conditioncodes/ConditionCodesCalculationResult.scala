package uk.gov.digital.ho.proving.financialstatus.domain.conditioncodes

case class ConditionCodesCalculationResult(applicant: Option[ApplicantConditionCode],
                                           partner: Option[PartnerConditionCode],
                                           child: Option[ChildConditionCode])

sealed trait ConditionCode
case class ApplicantConditionCode(value: String) extends ConditionCode
case class PartnerConditionCode(value: String) extends ConditionCode
case class ChildConditionCode(value: String) extends ConditionCode
