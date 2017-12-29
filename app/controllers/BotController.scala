package controllers

import javax.inject._

import bot.tasks.{BackTestTask, BotTask}
import play.api.Logger
import play.api.mvc.{AbstractController, ControllerComponents}

@Singleton
class BotController @Inject()(botTask: BotTask, backTestTask: BackTestTask, cc: ControllerComponents) extends AbstractController(cc) {

  def start() = Action {
    Logger.info("Starting the bot")


    botTask.run
    Accepted
  }

  def backTest() = Action {
    Logger.info("Back testing the bot")

    backTestTask.run
    Accepted
  }
}
