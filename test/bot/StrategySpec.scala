package bot

import org.joda.time.{DateTime, DateTimeUtils}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.Inside._
import org.scalatestplus.play.PlaySpec

import scala.concurrent.duration._


class StrategySpec extends PlaySpec with BeforeAndAfterEach {

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
      val initial = Strategy.init()
      val cs = candlestick(10)
      val updated = Strategy.tick(cs).exec(initial)

      initial.candlesticks mustBe List()
      updated.candlesticks mustBe List(cs)
    }

    "add candlestick added and discard old candlesticks" in {
      val initial = Strategy.init(discardDuration = 10.minutes)
      val firstCs = candlestick(10)
      val firstUpdate = Strategy.tick(firstCs).exec(initial)

      DateTimeUtils.setCurrentMillisOffset(15.minutes.toMillis)
      val secondCs = candlestick(10)
      val secondUpdate = Strategy.tick(secondCs).exec(firstUpdate)

      initial.candlesticks mustBe List()
      firstUpdate.candlesticks mustBe List(firstCs)
      secondUpdate.candlesticks mustBe List(secondCs)
    }

    "open new trade" in {
      val initial = Strategy.init()
      val transitions = for {
        _ <- Strategy.tick(candlestick(10))
        _ <- Strategy.tick(candlestick(8))
      } yield ()

      val updated = transitions.exec(initial)

      updated.openTrades mustBe List(Trade.init(8, 4))
    }

    "open two trades if simultaneousTrades is set to 2" in {
      val initial = Strategy.init(simultaneousTrades = 2)
      val transitions = for {
        _ <- Strategy.tick(candlestick(10))
        _ <- Strategy.tick(candlestick(8))
        _ <- Strategy.tick(candlestick(6))
      } yield ()

      val updated = transitions.exec(initial)

      updated.openTrades mustBe List(Trade.init(8, 4), Trade.init(6, 3))
    }

    "not open new trade if a trade is already open" in {
      val initial = Strategy.init()
      val transitions = for {
        _ <- Strategy.tick(candlestick(10))
        _ <- Strategy.tick(candlestick(8))
        _ <- Strategy.tick(candlestick(6))
      } yield ()

      val updated = transitions.exec(initial)

      updated.openTrades mustBe List(Trade.init(8, 4))
    }

    "close open trades" in {
      val initial = Strategy.init()
      val transitions = for {
        _ <- Strategy.tick(candlestick(10))
        _ <- Strategy.tick(candlestick(8))
        _ <- Strategy.tick(candlestick(14))
      } yield ()

      val updated = transitions.exec(initial)

      updated.closedTrades mustBe List(Trade(Closed, 8, Some(14), 4))
    }

  }

  private def candlestick(avg: BigDecimal) = Candlestick(DateTime.now(), avg, 0, 0, 0, 0)

  private def candlesticks(avgs: BigDecimal*) = avgs.map(candlestick)

}
