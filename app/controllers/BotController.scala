package controllers

import javax.inject._

import bot.BackTestTrader
import bot.tasks.BotTask
import com.fasterxml.jackson.databind.ObjectMapper
import org.joda.time.DateTime
import play.Logger
import play.api.mvc.{ControllerComponents, _}

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try


@Singleton
class BotController @Inject()(botTask: BotTask, backTestTrader: BackTestTrader, mapper: ObjectMapper, cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  private val validIntervals = List(5.minutes, 15.minutes, 30.minutes, 2.hours, 4.hours, 1.day)

  def start() = Action {
    Logger.info("Starting the bot")

    botTask.run
    Accepted
  }

  def backTest(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    val (defaultStartDate, defaultEndDate, defaultInterval) = (DateTime.now.minusDays(3), DateTime.now, 30.minutes)
    val currencyPair = request.getQueryString("currency-pair").getOrElse("USDT_BTC")
    val startDate: DateTime = parseDate(request.getQueryString("start-date"), defaultStartDate)
    val endDate = parseDate(request.getQueryString("end-date"), defaultEndDate)
    val interval = request.getQueryString("interval")
      .map(in => Try(Duration(in)).getOrElse(defaultInterval))
      .getOrElse(defaultInterval)

    if (!validIntervals.contains(interval)) {
      Future {
        BadRequest("Interval has to be one of " + validIntervals)
      }
    } else {
      Logger.info("Back testing the bot for {}, from {} to {} with every {}", currencyPair, startDate, endDate, interval)
      backTestTrader.run(currencyPair, startDate, endDate, interval)
        .map(res => {
          Ok(mapper.writeValueAsString(res)).as(JSON)
        })
    }

  }

  private def parseDate(date: Option[String], default: DateTime): DateTime = {
    Logger.info("parsing date {}", date)
    Try(date.map(DateTime.parse)
      .getOrElse(default))
      .getOrElse(default)
  }
}
