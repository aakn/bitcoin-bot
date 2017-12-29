package bot

import org.scalatestplus.play.PlaySpec

class TradeSpec extends PlaySpec {

  "A trade" must {
    "initialize" in {
      val trade = Trade.init(10, 5)
      trade mustBe Trade(Open, 10, None, 5)
    }

    "tick and close to stop loss" in {
      val trade = Trade.init(10, 5)
      val actual = Trade.tick(4.99).exec(trade)

      actual mustBe Trade(StopLoss, 10, Some(4.99), 5)
    }

    "tick and not close if current price is greater than stop loss" in {
      val trade = Trade.init(10, 5)
      val actual = Trade.tick(5.01).exec(trade)

      actual mustBe Trade(Open, 10, None, 5)
    }

    "close with exit price" in {
      val trade = Trade.init(10, 5)
      val actual = Trade.close(12).exec(trade)

      actual mustBe Trade(Closed, 10, Some(12), 5)
    }
  }

}
