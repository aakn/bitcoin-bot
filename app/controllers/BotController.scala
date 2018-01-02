package controllers

import javax.inject._

import bot.BackTestTrader
import bot.tasks.BotTask
import com.fasterxml.jackson.databind.ObjectMapper
import play.api.Logger
import play.api.mvc.{ControllerComponents, _}

import scala.concurrent.ExecutionContext

@Singleton
class BotController @Inject()(botTask: BotTask, backTestTrader: BackTestTrader, mapper: ObjectMapper, cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def start() = Action {
    Logger.info("Starting the bot")


    botTask.run
    Accepted
  }

  def backTest(): Action[AnyContent] = Action.async {
    Logger.info("Back testing the bot")

    backTestTrader.run()
      .map(res => Ok(mapper.writeValueAsString(res)).as(JSON))
  }
}
