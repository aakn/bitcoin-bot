package bot.poloniex

import org.joda.time.DateTime
import play.api.libs.functional.syntax._
import play.api.libs.json._

case class Point(date: DateTime, average: BigDecimal, open: BigDecimal, close: BigDecimal, high: BigDecimal, low: BigDecimal)

object Point {

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

  implicit val chartResponseJsonFormat: Reads[Point] = (
    (JsPath \ "date").read[DateTime](jodaDateReads) and
      (JsPath \ "weightedAverage").read[BigDecimal](bigDecimalReads) and
      (JsPath \ "open").read[BigDecimal](bigDecimalReads) and
      (JsPath \ "close").read[BigDecimal](bigDecimalReads) and
      (JsPath \ "high").read[BigDecimal](bigDecimalReads) and
      (JsPath \ "low").read[BigDecimal](bigDecimalReads)
    ) (Point.apply _)

}
