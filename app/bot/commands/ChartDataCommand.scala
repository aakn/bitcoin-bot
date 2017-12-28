package bot.commands

import javax.inject.Inject

import bot.poloniex.ChartResponse
import com.netflix.hystrix.{HystrixCommand, HystrixCommandGroupKey}
import org.joda.time.DateTime
import play.api.libs.ws.{WSClient, WSResponse}

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}

class ChartDataCommand @Inject()(ws: WSClient)(implicit ec: ExecutionContext) extends HystrixCommand[List[ChartResponse]](HystrixCommandGroupKey.Factory.asKey("poloniex")) {

  def run(): List[ChartResponse] = {
    val response: Future[WSResponse] = ws.url("https://poloniex.com/public")
      .addQueryStringParameters("command" -> "returnChartData")
      .addQueryStringParameters("currencyPair" -> "USDT_BTC")
      .addQueryStringParameters("start" -> dateTimeToString(new DateTime().minusDays(30)))
      .addQueryStringParameters("end" -> dateTimeToString(new DateTime().plusDays(1)))
      .addQueryStringParameters("period" -> 4.hours.toSeconds.toString)
      .addHttpHeaders("user-agent" -> "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.84 Safari/537.36")
      .addHttpHeaders("Cookie" -> "__cfduid=d8ad9b0167dc8b36d5528867a5c30bfb21509703260; _ga=GA1.2.817619400.1509703267; _gid=GA1.2.408801550.1514101253; POLOSESSID=tj6f45cclc1j405mpg0nl7msu7; cf_clearance=e88e296e2214480455696a547f7be5401fad0c99-1514102613-1800")
      .withRequestTimeout(10.seconds)
      .get()
    val future = response.map {
      r => r.json.as[List[ChartResponse]]
    }
    Await.result(future, 10.seconds)
  }

  private def dateTimeToString(dt: DateTime) = {
    (dt.getMillis / 1000).toString
  }
}
