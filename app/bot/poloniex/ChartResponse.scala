package bot.poloniex

import org.joda.time.DateTime
import play.api.libs.functional.syntax._
import play.api.libs.json._

case class ChartResponse(date: DateTime, weightedAverage: Float)

object ChartResponse {

  private val jodaDateReads = Reads[DateTime](js =>
    js.validate[Long].map[DateTime](dt =>
      new DateTime(dt * 1000)
    )
  )

  implicit val chartResponseJsonFormat: Reads[ChartResponse] = (
    (__ \ "date").read[DateTime](jodaDateReads) and
      (__ \ "weightedAverage").read[Float]

    ) (ChartResponse.apply _)

}
