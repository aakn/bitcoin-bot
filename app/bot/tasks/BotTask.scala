package bot.tasks

import javax.inject.Inject

import akka.actor.{ActorSystem, Cancellable}
import bot.Trader
import bot.commands.ChartDataCommand
import play.Logger

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class BotTask @Inject()(actorSystem: ActorSystem, trader: Trader)(implicit executionContext: ExecutionContext) {

  def run: Cancellable = actorSystem.scheduler.scheduleOnce(delay = 0.seconds) {
    // the block of code that will be executed
    Logger.info("Executing something...")
    trader.start()

  }

}