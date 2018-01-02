package bot

import javax.inject.Inject

import bot.functions.Chart
import org.joda.time.DateTime

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}


class BackTestTrader @Inject()(chart: Chart)(implicit ec: ExecutionContext) {

  def run(): Future[Strategy] = {
    val currencyPair = "USDT_BTC"
    val startDate = new DateTime().minusDays(30)
    val endDate = new DateTime().plusDays(1)
    val interval = 30.minutes

    val strategy = Strategy.init(discardDuration = 31.days)

    chart.backTest(currencyPair, startDate, endDate, interval)
      .map(iterate(strategy))
  }

  private def iterate(strategy: Strategy)(stream: Stream[Candlestick]): Strategy =
    stream.foldLeft(strategy)((s, cs) => Strategy.tick(cs).exec(s))

}
