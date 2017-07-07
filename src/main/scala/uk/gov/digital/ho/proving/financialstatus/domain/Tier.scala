package uk.gov.digital.ho.proving.financialstatus.domain

import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.stereotype.Service
import uk.gov.digital.ho.proving.financialstatus.api.configuration.TierTypeException

@Service
class TierChecker @Autowired() (@Value("${tier.2.type}") val t2: String,
                                @Value("${tier.4.type}") val t4: String,
                                @Value("${tier.5.type}") val t5: String) {

  private val TIER_2 = t2
  private val TIER_4 = t4
  private val TIER_5 = t5

  val values = Vector(TIER_2, TIER_4, TIER_5)

  def getTier(tier: String): Tier = {

    tier.toLowerCase() match {
      case TIER_2 => Tier2
      case TIER_4 => Tier4
      case TIER_5 => Tier5
      case _ => throw TierTypeException("Invalid tier: " + tier)
    }
  }

  def getTierName(tier: Tier): String = {
    tier match {
      case Tier2 => TIER_2
      case Tier4 => TIER_4
      case Tier5 => TIER_5
    }
  }
}

sealed trait Tier
case object Tier2 extends Tier
case object Tier4 extends Tier
case object Tier5 extends Tier
