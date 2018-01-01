package bot

import org.joda.time.DateTime
import org.scalatestplus.play.PlaySpec

import scala.concurrent.duration._

class IndicatorSpec extends PlaySpec {

  "Indicator.movingAverage" should {
    "take average of all prices" in {
      Indicator.movingAverage(15.minutes)(candlesticks(2, 3, 4, 5)) mustBe 3.5
    }

    "take average of last 4 prices" in {
      val oldCandlestick = candlestick(DateTime.now().minusMinutes(16))(1)
      val cs = oldCandlestick :: candlesticks(2, 3, 4, 5)
      Indicator.movingAverage(15.minutes)(cs) mustBe 3.5
    }

  }

  private def candlesticks(averages: BigDecimal*) =
    averages.map(candlestick(DateTime.now())).toList

  private def candlestick(time: DateTime)(avg: BigDecimal) = Candlestick(time, avg, 0, 0, 0, 0)

}
