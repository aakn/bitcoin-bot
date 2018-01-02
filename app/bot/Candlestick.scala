package bot

import org.joda.time.DateTime
import play.api.libs.functional.syntax._
import play.api.libs.json._

case class Candlestick(date: DateTime, average: BigDecimal, open: BigDecimal, close: BigDecimal, high: BigDecimal, low: BigDecimal)

object Candlestick {

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

  val candlestickReads: Reads[Candlestick] = (
    (JsPath \ "date").read[DateTime](jodaDateReads) and
      (JsPath \ "weightedAverage").read[BigDecimal](bigDecimalReads) and
      (JsPath \ "open").read[BigDecimal](bigDecimalReads) and
      (JsPath \ "close").read[BigDecimal](bigDecimalReads) and
      (JsPath \ "high").read[BigDecimal](bigDecimalReads) and
      (JsPath \ "low").read[BigDecimal](bigDecimalReads)
    ) (Candlestick.apply _)

  val candlestickWrites: Writes[Candlestick] = (cs: Candlestick) => Json.obj(
    "average" -> cs.average,
    "date" -> cs.date.toDateTimeISO.toString()
  )

  implicit val candlestickFormat: Format[Candlestick] = Format(candlestickReads, candlestickWrites)
}
