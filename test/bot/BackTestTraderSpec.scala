package bot

import bot.functions.Chart
import common.Matchers._
import org.joda.time.DateTime
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito.{verify, when}
import org.scalatest.mockito.MockitoSugar._
import org.scalatest.{AsyncFlatSpec, OneInstancePerTest}

import scala.concurrent.Future
import scala.concurrent.duration._
import scalaz.State._

class BackTestTraderSpec extends AsyncFlatSpec with OneInstancePerTest {

  private val chart = mock[Chart]
  private val executor = mock[StrategyExecutor]
  private val trader = new BackTestTrader(chart, executor)
  private val cs = candlestick(10)
  private val strategy = Strategy.init()

  when(chart.backTest(eql("USD_BTC"), any(), any(), eql(30.minutes))).thenReturn(Future {
    Stream(cs)
  })
  when(executor.tick(any())).thenReturn(modify[Strategy] { _ => strategy })

  "BackTestTrader.run()" should "call executor.tick once with a stream of one candlestick" in {

    trader.run("USD_BTC", DateTime.now, DateTime.now, 30.minutes)
      .map(s => {
        verify(executor).tick(cs)
        assert(strategy == s)
      })
  }

  private def candlestick(avg: BigDecimal) = Candlestick(DateTime.now, avg, 0, 0, 0, 0)

}
