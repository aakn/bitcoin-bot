package bot

import javax.inject.Inject

import bot.functions.Chart
import org.joda.time.DateTime
import play.Logger

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._


class BackTestTrader @Inject()(chart: Chart)(implicit ec: ExecutionContext) {

  def start() = {
    val currencyPair = "USDT_BTC"
    val startDate = new DateTime().minusDays(30)
    val endDate = new DateTime().plusDays(1)
    val period = 30.minutes

    chart.backTest(currencyPair, startDate, endDate, period).map(iterate)
  }

  private def iterate(stream: Stream[Candlestick]) =
    stream.foreach(Logger.info("candlestick {}", _))


}
