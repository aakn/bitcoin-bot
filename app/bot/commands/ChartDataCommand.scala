package bot.commands

import javax.inject.Inject

import bot.poloniex.ChartResponse
import com.netflix.hystrix.{HystrixCommand, HystrixCommandGroupKey}
import org.joda.time.DateTime
import play.Logger
import play.api.libs.ws.{WSClient, WSCookie}

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext}

class ChartDataCommand @Inject()(ws: WSClient)(implicit ec: ExecutionContext) extends HystrixCommand[List[ChartResponse]](HystrixCommandGroupKey.Factory.asKey("poloniex")) {

  def run(): List[ChartResponse] = {

    val url = ws.url("https://poloniex.com/public")
      .addQueryStringParameters("command" -> "returnChartData")
      .addQueryStringParameters("currencyPair" -> "USDT_BTC")
      .addQueryStringParameters("start" -> dateTimeToString(new DateTime().minusDays(1)))
      .addQueryStringParameters("end" -> dateTimeToString(new DateTime().plusDays(1)))
      .addQueryStringParameters("period" -> "14400")
      .addCookies()
    Logger.info("request url {}, {}", url.url, url.queryString)
    val future = url
      .withRequestTimeout(10.seconds)
      .get()
      .map {
        response => response.json.as[List[ChartResponse]]
      }
    Await.result(future, 10.seconds)
  }

  private def dateTimeToString(dt: DateTime): String = {
    (dt.getMillis / 1000).toString
  }
}
