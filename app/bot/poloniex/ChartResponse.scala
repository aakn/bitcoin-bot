package bot.poloniex

import play.api.libs.json.{Json, OFormat}

case class ChartResponse(date: String, weightedAverage: BigDecimal)

object ChartResponse {
  implicit val chartResponseJsonFormat: OFormat[ChartResponse] = Json.format[ChartResponse]
}
