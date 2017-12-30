package bot

object Indicator {

  def movingAverage(recency: Int)(candlesticks: Seq[Candlestick]): BigDecimal = {
    val recentPrices = candlesticks.takeRight(recency).map(_.average)
    recentPrices.sum / recentPrices.length
  }

}
