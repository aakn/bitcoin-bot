package bot

import javax.inject.{Inject, Singleton}

import bot.commands.ChartDataCommand
import bot.poloniex.ChartResponse
import play.Logger

@Singleton
class Trader @Inject()(command: ChartDataCommand) {
  def start() = {
    val response: List[ChartResponse] = command.execute()
    Logger.info("response from poloniex {}", response)

    val lengthOfMA = 10
    val prices: List[BigDecimal] = List(response.head.weightedAverage)
    List("a", "b", "c").foldLeft(0)((l, r) => l + r.length)
    response.tail
      .foldLeft(false, "", prices)((prev, dataPoint) => {
        val (prevTradePlaced, prevTypeOfTrade, prices) = prev
        val currentMovingAverage = prices.sum / prices.size
        val previousPrice = prices.last
        val lastPairPrice = dataPoint.weightedAverage
        val (tradePlaced, typeOfTrade) = trade(prevTradePlaced, prevTypeOfTrade, currentMovingAverage, previousPrice, lastPairPrice)
        val updatedPrices = (prices :+ lastPairPrice).takeRight(lengthOfMA)
        Logger.info("{} Period: {}s {}: {} Moving Average: {}", dataPoint.date, "14400", "USDT_BTC", lastPairPrice, currentMovingAverage)
        (tradePlaced, typeOfTrade, updatedPrices)
      })

  }

  private def trade(tradePlaced: Boolean, typeOfTrade: String, currentMovingAverage: BigDecimal, previousPrice: BigDecimal, lastPairPrice: BigDecimal): (Boolean, String) = {
    if (!tradePlaced) {
      if ((lastPairPrice > currentMovingAverage) && (lastPairPrice < previousPrice)) {
        Logger.info("SELL ORDER")
        (true, "short")
      } else if ((lastPairPrice < currentMovingAverage) && (lastPairPrice > previousPrice)) {
        Logger.info("BUY ORDER")
        (true, "long")
      } else {
        (tradePlaced, typeOfTrade)
      }
    } else if (typeOfTrade == "short" && lastPairPrice < currentMovingAverage) {
      Logger.info("EXIT TRADE")
      (false, "")
    } else if (typeOfTrade == "long" && lastPairPrice > currentMovingAverage) {
      Logger.info("EXIT TRADE")
      (false, "")
    } else {
      (tradePlaced, typeOfTrade)
    }
  }
}
