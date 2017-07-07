package uk.gov.digital.ho.proving.financialstatus.api.configuration

case class TierTypeException(message: String) extends Exception(message)
case class ApplicantTypeException(message: String) extends IllegalArgumentException(message)
case class VariantTypeException(message: String) extends Exception(message)
case class DependantsException(message: String) extends IllegalArgumentException(message)
