package bot

import scala.math.BigDecimal.RoundingMode

case class StrategyAnalysis(candlesticks: List[Candlestick], trades: List[Trade], profits: Profits)

case class Profits(seed: BigDecimal, gross: BigDecimal, net: BigDecimal)

case class Charges(maker: BigDecimal, taker: BigDecimal)

object StrategyAnalysis {

  def build(charges: Charges)(strategy: Strategy): StrategyAnalysis = {
    val trades = strategy.openTrades ::: strategy.closedTrades
    val gross = strategy.closedTrades.map(t => t.exitPrice.get - t.entryPrice).sum
    val net = strategy.closedTrades
      .map(t => (t.exitPrice.get, t.entryPrice, t.exitPrice.get * charges.taker, t.entryPrice * charges.taker))
      .map { case (exit, entry, exitFee, entryFee) => exit - entry - exitFee - entryFee }.sum
    val seed = strategy.candlesticks.head.average
    val round = (value: BigDecimal) => value.setScale(2, RoundingMode.HALF_UP)
    StrategyAnalysis(strategy.candlesticks, trades, Profits(round(seed), round(gross), round(net)))
  }
}