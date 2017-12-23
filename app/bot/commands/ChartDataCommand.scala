package bot.commands

import javax.inject.Inject

import bot.poloniex.ChartResponse
import com.netflix.hystrix.{HystrixCommand, HystrixCommandGroupKey}
import org.joda.time.DateTime
import play.api.libs.ws.{WSClient, WSResponse}

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}

class ChartDataCommand @Inject()(ws: WSClient)(implicit ec: ExecutionContext) extends HystrixCommand[Seq[ChartResponse]](HystrixCommandGroupKey.Factory.asKey("poloniex")) {

  def run(): Seq[ChartResponse] = {
    val response: Future[WSResponse] = ws.url("https://poloniex.com/public")
      .addQueryStringParameters("command" -> "returnChartData")
      .addQueryStringParameters("currencyPair" -> "USDT_BTC")
      .addQueryStringParameters("start" -> dateTimeToString(new DateTime().minusDays(1)))
      .addQueryStringParameters("end" -> dateTimeToString(new DateTime().plusDays(1)))
      .addQueryStringParameters("period" -> "14400")
      .withRequestTimeout(10.seconds)
      .get()
    val future = response.map {
      r => r.json.as[Seq[ChartResponse]]
    }
    Await.result(future, 10.seconds)
  }

  private def dateTimeToString(dt: DateTime) = {
    (dt.getMillis / 1000).toString
  }
}
