package bot

import org.joda.time.DateTime

import scalaz.State

sealed trait TradeStatus

case object Open extends TradeStatus

case object Closed extends TradeStatus

case object StopLoss extends TradeStatus

case class Trade(status: TradeStatus, entryPrice: BigDecimal, exitPrice: Option[BigDecimal],
                 entryTime: DateTime, exitTime: Option[DateTime], stopLoss: BigDecimal) {

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
  def init(price: BigDecimal, time: DateTime, stopLoss: BigDecimal) =
    Trade(Open, price, None, time, None, stopLoss)

  def tick(price: BigDecimal, time: DateTime): State[Trade, Unit] = State.modify[Trade] { s =>
    if (price <= s.stopLoss) s.copy(exitPrice = Some(price), exitTime = Some(time), status = StopLoss)
    else s
  }

  def close(price: BigDecimal, time: DateTime): State[Trade, Unit] = State.modify[Trade] {
    _.copy(exitPrice = Some(price), exitTime = Some(time), status = Closed)
  }
}
