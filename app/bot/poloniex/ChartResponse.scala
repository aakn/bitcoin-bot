package bot.poloniex

import org.joda.time.DateTime
import play.api.libs.functional.syntax._
import play.api.libs.json._

case class ChartResponse(date: DateTime, weightedAverage: BigDecimal)

object ChartResponse {

  private val jodaDateReads = Reads[DateTime](js =>
    js.validate[Long].map[DateTime](dt =>
      new DateTime(dt * 1000)
    )
  )

  private val bigDecimalReads = Reads[BigDecimal](js =>
    js.validate[Double].map[BigDecimal](dt =>
      BigDecimal.valueOf(dt)
    )
  )

  implicit val chartResponseJsonFormat: Reads[ChartResponse] = (
    (__ \ "date").read[DateTime](jodaDateReads) and
      (__ \ "weightedAverage").read[BigDecimal](bigDecimalReads)

    ) (ChartResponse.apply _)

}
