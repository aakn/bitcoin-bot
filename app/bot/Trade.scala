package bot

import scalaz.State

sealed trait TradeStatus

case object Open extends TradeStatus

case object Closed extends TradeStatus

case object StopLoss extends TradeStatus

case class Trade(status: TradeStatus, entryPrice: BigDecimal, exitPrice: Option[BigDecimal], stopLoss: BigDecimal) {

  def pretty(): String = {
    if (status == Closed || status == StopLoss) {
      val profitOrLossString =
        if (exitPrice.get > entryPrice) "Profit"
        else "Loss"

      s"$toString Profit: $profitOrLossString ${exitPrice.get - entryPrice}"
    }
    else toString
  }
}

object Trade {
  def init(currentPrice: BigDecimal, stopLoss: BigDecimal) =
    Trade(Open, currentPrice, None, stopLoss)

  def tick(price: BigDecimal): State[Trade, Unit] = State.modify[Trade] { s =>
    if (price < s.stopLoss) s.copy(exitPrice = Some(price), status = StopLoss)
    else s
  }

  def close(price: BigDecimal): State[Trade, Unit] = State.modify[Trade] {
    _.copy(exitPrice = Some(price), status = Closed)
  }
}
