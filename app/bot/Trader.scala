package bot

import javax.inject.{Inject, Singleton}

import bot.commands.ChartDataCommandBuilder
import lib.hystrix.Futures._
import org.joda.time.DateTime
import play.Logger

import scala.concurrent.duration._

@Singleton
class Trader @Inject()(command: ChartDataCommandBuilder) {
  def start() = {
    // input parameters
    val currencyPair = "USDT_BTC"
    val period = 30.minutes
    val startDate = new DateTime().minusDays(30)
    val endDate = new DateTime().plusDays(1)

    command(currencyPair, startDate, endDate, period).future.map {
      response =>
        Logger.info("number of data points from poloniex {}", response.length.toString)

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
            Logger.info("{} Period: {}s {}: {} Moving Average: {}", dataPoint.date, period, currencyPair, lastPairPrice, currentMovingAverage)
            (tradePlaced, typeOfTrade, updatedPrices)
          })

        Logger.info("DONE")
    }
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
