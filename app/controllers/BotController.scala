package controllers

import javax.inject._

import bot.tasks.BotTask
import play.api.Logger
import play.api.mvc.{AbstractController, ControllerComponents}

@Singleton
class BotController @Inject()(botTask: BotTask, cc: ControllerComponents) extends AbstractController(cc) {

  def start() = Action {
    Logger.info("Starting the bot")


    botTask.run
    Accepted
  }
}
