package bot.tasks

import javax.inject.Inject

import akka.actor.{ActorSystem, Cancellable}
import bot.BackTestTrader
import org.joda.time.DateTime
import play.Logger

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class BackTestTask @Inject()(actorSystem: ActorSystem, trader: BackTestTrader)(implicit executionContext: ExecutionContext) {

  def run: Cancellable = actorSystem.scheduler.scheduleOnce(delay = 0.seconds) {
    // the block of code that will be executed
    Logger.info("Executing something...")
    trader.run("USDT_BTC", DateTime.now.minusDays(30), DateTime.now, 30.minutes)
  }

}