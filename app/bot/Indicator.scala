package bot

import scala.concurrent.duration.FiniteDuration

object Indicator {

  def movingAverage(period: FiniteDuration)(candlesticks: Seq[Candlestick]): BigDecimal = {
    val discardThreshold = candlesticks.last.date.minusMillis(period.toMillis.toInt)
    val recentPrices = candlesticks
      .dropWhile(_.date.isBefore(discardThreshold))
      .map(_.average)
    recentPrices.sum / recentPrices.length
  }

}
