package bot

import org.joda.time.DateTime
import org.scalatestplus.play.PlaySpec

class TradeSpec extends PlaySpec {

  val (t1, t2) = (DateTime.now().minusHours(1), DateTime.now())

  "A trade" must {
    "initialize" in {
      val trade = Trade.init(10, t1, 5)
      trade mustBe Trade(Open, 10, None, t1, None, 5)
    }

    "tick and close to stop loss" in {
      val trade = Trade.init(10, t1, 5)
      val actual = Trade.tick(5, t2).exec(trade)

      actual mustBe Trade(StopLoss, 10, Some(5), t1, Some(t2), 5)
    }

    "tick and not close if current price is greater than stop loss" in {
      val trade = Trade.init(10, t1, 5)
      val actual = Trade.tick(5.01, t2).exec(trade)

      actual mustBe Trade(Open, 10, None, t1, None, 5)
    }

    "close with exit price" in {
      val trade = Trade.init(10, t1, 5)
      val actual = Trade.close(12, t2).exec(trade)

      actual mustBe Trade(Closed, 10, Some(12), t1, Some(t2), 5)
    }
  }

}
