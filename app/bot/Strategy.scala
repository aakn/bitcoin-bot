package bot

import org.joda.time.DateTime

import scala.concurrent.duration.{FiniteDuration, _}
import scalaz.State
import scalaz.State._

case class Strategy(candlesticks: List[Candlestick], openTrades: List[Trade], closedTrades: List[Trade], simultaneousTrades: Int, discardDuration: FiniteDuration, stopLossThreshold: BigDecimal)


object Strategy {
  def init(simultaneousTrades: Int = 1, discardDuration: FiniteDuration = 6.hours, stopLossThreshold: BigDecimal = 0.5): Strategy =
    Strategy(List(), List(), List(), simultaneousTrades, discardDuration, stopLossThreshold)

  def tick(candlestick: Candlestick): State[Strategy, Unit] = modify[Strategy] { s =>
    def stateTransitions = for {
      _ <- addCandlestick(candlestick)
      _ <- closeTrades()
      _ <- openNewTrade()
    } yield ()

    stateTransitions.exec(s)
  }

  private def addCandlestick(candlestick: Candlestick): State[Strategy, Unit] = modify[Strategy] { s =>
    val threshold = DateTime.now().minusMillis(s.discardDuration.toMillis.toInt)
    val candlesticks = (s.candlesticks :+ candlestick).dropWhile(_.date.isBefore(threshold))
    s.copy(candlesticks = candlesticks)
  }

  private def closeTrades(): State[Strategy, Unit] = modify[Strategy] { s =>
    val currentPrice = s.candlesticks.last.average
    val indicated = Indicator.movingAverage(10)(s.candlesticks)

    val (toBeClosed, openTrades) =
      if (currentPrice > indicated) {
        s.openTrades
          .partition(currentPrice > _.entryPrice)
      } else (List(), s.openTrades)
    val closedTrades = s.closedTrades ::: toBeClosed.map(Trade.close(currentPrice).exec(_))

    s.copy(openTrades = openTrades, closedTrades = closedTrades)
  }

  private def openNewTrade(): State[Strategy, Unit] = modify[Strategy] { s =>
    val currentPrice = s.candlesticks.last.average
    val indicated = Indicator.movingAverage(10)(s.candlesticks)

    val openTrades =
      if (s.openTrades.lengthCompare(s.simultaneousTrades) < 0 && currentPrice < indicated) {
        s.openTrades :+ Trade.init(currentPrice, currentPrice * s.stopLossThreshold)
      } else s.openTrades

    s.copy(openTrades = openTrades)
  }
}
