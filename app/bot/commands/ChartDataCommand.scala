package bot.commands

import javax.inject.Inject

import bot.poloniex.ChartResponse
import com.netflix.hystrix.{HystrixCommand, HystrixCommandGroupKey}
import play.api.libs.ws.{WSClient, WSResponse}

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}

class ChartDataCommand @Inject()(ws: WSClient, ec: ExecutionContext) extends HystrixCommand[ChartResponse](HystrixCommandGroupKey.Factory.asKey("poloniex")) {

  def run(): ChartResponse = {
    val response: Future[WSResponse] = ws.url("https://poloniex.com/public")
      .addQueryStringParameters("command" -> "returnChartData")
      .addQueryStringParameters("currencyPair" -> "USDT_BTC")
      .addQueryStringParameters("start" -> "1500699200")
      .addQueryStringParameters("end" -> "9999999999")
      .addQueryStringParameters("period" -> "14400")
      .get()
    val future = response.map {
      r => r.json.as[ChartResponse]
    }
    Await.result(future, 5000.millis)
  }
}
