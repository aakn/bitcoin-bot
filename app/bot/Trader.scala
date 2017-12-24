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

    val prices: List[BigDecimal] = List(response.head.weightedAverage)
    val tradePlaced = false
    response.tail
      .foreach(dataPoint => {
        val currentMovingAverage = prices.sum / prices.size
        val previousPrice = prices.last
        val lastPairPrice = dataPoint.weightedAverage
        if (!tradePlaced) {
          if ((lastPairPrice > currentMovingAverage) && (lastPairPrice < previousPrice)) {

          } else if ((lastPairPrice < currentMovingAverage) && (lastPairPrice > previousPrice)) {

          }
        }
      })

  }
}
