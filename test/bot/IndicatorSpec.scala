package bot

import org.joda.time.DateTime
import org.scalatestplus.play.PlaySpec

class IndicatorSpec extends PlaySpec {

  "Indicator.movingAverage" should {
    "take average of all prices" in {
      Indicator.movingAverage(10)(buildCandleStickWithAverage(2, 3, 4, 5)) == 3.5
    }

    "take average of last 4 prices" in {
      Indicator.movingAverage(4)(buildCandleStickWithAverage(1, 2, 3, 4, 5)) == 3.5
    }

  }

  private def buildCandleStickWithAverage(averages: BigDecimal*) =
    averages.map(Candlestick(DateTime.now(), _, 0, 0, 0, 0))

}
