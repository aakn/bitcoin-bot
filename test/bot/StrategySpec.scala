package bot

import org.joda.time.{DateTime, DateTimeUtils}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.Inside._
import org.scalatestplus.play.PlaySpec

import scala.concurrent.duration._

class StrategySpec extends PlaySpec with BeforeAndAfterEach {

  override def beforeEach(): Unit = {
    DateTimeUtils.setCurrentMillisFixed(1500000000000L)
  }

  override def afterEach() {
    DateTimeUtils.setCurrentMillisSystem()
  }

  "Strategy.init" must {
    "initialize with defaults" in {
      val strategy = Strategy.init()
      inside(strategy) { case Strategy(candlesticks, openTrades, closedTrades, _, _, _) =>
        candlesticks mustBe List()
        openTrades mustBe List()
        closedTrades mustBe List()
      }
    }
  }

  "Strategy.tick" must {
    "add candlestick" in {
      val executor = new StrategyExecutor
      val initial = Strategy.init()
      val cs = candlestick(10)
      val updated = executor.tick(cs).exec(initial)

      initial.candlesticks mustBe List()
      updated.candlesticks mustBe List(cs)
    }

    "add candlestick added and discard old candlesticks" in {
      val executor = new StrategyExecutor
      val initial = Strategy.init(discardDuration = 10.minutes)
      val firstCs = candlestick(10)
      val firstUpdate = executor.tick(firstCs).exec(initial)

      DateTimeUtils.setCurrentMillisOffset(15.minutes.toMillis)
      val secondCs = candlestick(10)
      val secondUpdate = executor.tick(secondCs).exec(firstUpdate)

      initial.candlesticks mustBe List()
      firstUpdate.candlesticks mustBe List(firstCs)
      secondUpdate.candlesticks mustBe List(secondCs)
    }

    "open new trade" in {
      val executor = new StrategyExecutor
      val initial = Strategy.init()
      val transitions = for {
        _ <- executor.tick(candlestick(10))
        _ <- executor.tick(candlestick(8))
      } yield ()

      val updated = transitions.exec(initial)

      inside(updated.openTrades.head) { case Trade(state, entryPrice, _, entryTime, _, stopLoss) =>
        state mustBe Open
        entryPrice mustBe 8
        stopLoss mustBe 4
        entryTime mustBe DateTime.now()
      }
    }

    "open two trades if simultaneousTrades is set to 2" in {
      val executor = new StrategyExecutor
      val initial = Strategy.init(simultaneousTrades = 2)
      val transitions = for {
        _ <- executor.tick(candlestick(10))
        _ <- executor.tick(candlestick(8))
        _ <- executor.tick(candlestick(6))
      } yield ()

      val updated = transitions.exec(initial)

      updated.openTrades.size mustBe 2
      inside(updated.openTrades.head) { case Trade(state, entryPrice, _, _, _, stopLoss) =>
        state mustBe Open
        entryPrice mustBe 8
        stopLoss mustBe 4
      }
      inside(updated.openTrades(1)) { case Trade(state, entryPrice, _, _, _, stopLoss) =>
        state mustBe Open
        entryPrice mustBe 6
        stopLoss mustBe 3
      }
    }

    "not open new trade if a trade is already open" in {
      val executor = new StrategyExecutor
      val initial = Strategy.init()
      val transitions = for {
        _ <- executor.tick(candlestick(10))
        _ <- executor.tick(candlestick(8))
        _ <- executor.tick(candlestick(6))
      } yield ()

      val updated = transitions.exec(initial)

      updated.openTrades.size mustBe 1
      inside(updated.openTrades.head) { case Trade(state, entryPrice, _, _, _, _) =>
        state mustBe Open
        entryPrice mustBe 8
      }
    }

    "close open trades" in {
      val executor = new StrategyExecutor
      val initial = Strategy.init()
      val transitions = for {
        _ <- executor.tick(candlestick(10))
        _ <- executor.tick(candlestick(8))
        _ <- executor.tick(candlestick(14))
      } yield ()

      val updated = transitions.exec(initial)

      updated.openTrades mustBe empty
      inside(updated.closedTrades.head) { case Trade(state, entryPrice, exitPrice, _, _, _) =>
        state mustBe Closed
        entryPrice mustBe 8
        exitPrice must contain(14)
      }
    }

    "handle stop losses" in {
      val executor = new StrategyExecutor
      val initial = Strategy.init()
      val transitions = for {
        _ <- executor.tick(candlestick(10))
        _ <- executor.tick(candlestick(8))
        _ <- executor.tick(candlestick(6))
        _ <- executor.tick(candlestick(4))
      } yield ()

      val updated = transitions.exec(initial)

      updated.openTrades mustBe empty
      inside(updated.closedTrades.head) { case Trade(state, entryPrice, exitPrice, _, _, _) =>
        state mustBe StopLoss
        entryPrice mustBe 8
        exitPrice must contain(4)
      }
    }

  }

  private def candlestick(avg: BigDecimal) = Candlestick(DateTime.now(), avg, 0, 0, 0, 0)

}
