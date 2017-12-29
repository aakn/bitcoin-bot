package bot.functions

import javax.inject.Inject

import bot.commands.ChartDataCommandBuilder
import bot.poloniex.Candlestick
import lib.hystrix.Futures._
import org.joda.time.DateTime

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{ExecutionContext, Future}

class Chart @Inject()(command: ChartDataCommandBuilder)(implicit ec: ExecutionContext) {

  def backTest(currencyPair: String, startDate: DateTime, endDate: DateTime, period: FiniteDuration): Future[Stream[Candlestick]] =
    command(currencyPair, startDate, endDate, period).future
      .map(_.toStream)

}
