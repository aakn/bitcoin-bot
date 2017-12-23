package bot.tasks

import javax.inject.Inject

import akka.actor.{ActorSystem, Cancellable}
import play.api._

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext

class BotTask @Inject()(actorSystem: ActorSystem)(implicit executionContext: ExecutionContext) {

  def run: Cancellable = actorSystem.scheduler.scheduleOnce(delay = 0.seconds) {
    // the block of code that will be executed
    Logger.info("Executing something...")
  }

}