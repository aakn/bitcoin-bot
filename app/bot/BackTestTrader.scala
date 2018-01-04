package bot

import javax.inject.Inject

import bot.functions.Chart
import org.joda.time.DateTime

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}


class BackTestTrader @Inject()(chart: Chart, executor: StrategyExecutor)(implicit ec: ExecutionContext) {

  def run(currencyPair: String, startDate: DateTime, endDate: DateTime, interval: Duration): Future[StrategyAnalysis] = {

    val strategy = Strategy.init(discardDuration = 31.days)

    chart.backTest(currencyPair, startDate, endDate, interval)
      .map(iterate(strategy))
      .map(StrategyAnalysis.build(Charges(0.0015, 0.0025)))
  }

  private def iterate(strategy: Strategy)(stream: Stream[Candlestick]): Strategy =
    stream.foldLeft(strategy)((s, cs) => executor.tick(cs).exec(s))

}
