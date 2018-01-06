package bot

import org.joda.time.DateTime
import org.scalatest.Matchers._
import org.scalatest.OneInstancePerTest
import org.scalatestplus.play.PlaySpec

import scala.concurrent.duration._
import scala.math.BigDecimal.RoundingMode
import scala.math._

class StrategyAnalysisSpec extends PlaySpec with OneInstancePerTest {

  "StrategyAnalysis.init" should {
    "build with a strategy instance" in {
      val cs = candlesticks(1, 2, 3)
      val open = openTrades(5, 6)
      val closed = closedTrades((10, 15), (12, 14), (12, 10))
      val strategy = Strategy(cs, open, closed, 0, 0.minutes, 0)

      val analysis = StrategyAnalysis.build(Charges(0.0015, 0.0025))(strategy)

      analysis.candlesticks mustBe cs
      analysis.trades mustBe open ::: closed
      analysis.profits.seed mustBe 1
      analysis.profits.gross mustBe 5
      val expectedNet: BigDecimal = (15 - 10 - 10 * 0.0025 - 15 * 0.0025) + (14 - 12 - 12 * 0.0025 - 14 * 0.0025) + (10 - 12 - 10 * 0.0025 - 12 * 0.0025)
      analysis.profits.net.doubleValue shouldBe expectedNet.setScale(2, RoundingMode.HALF_UP)
    }
  }

  private def openTrades(prices: BigDecimal*): List[Trade] =
    prices.map(price => Trade(Closed, price, None, DateTime.now, None, 0)).toList


  private def closedTrades(prices: (BigDecimal, BigDecimal)*): List[Trade] =
    prices.map(price => Trade(Closed, price._1, Some(price._2), DateTime.now, Some(DateTime.now), 0)).toList

  private def candlesticks(averages: BigDecimal*) =
    averages.map(Candlestick(DateTime.now, _, 0, 0, 0, 0)).toList
}
