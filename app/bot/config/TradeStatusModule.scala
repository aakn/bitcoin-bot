package bot.config

import bot.TradeStatus
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.{JsonSerializer, SerializerProvider}

class TradeStatusModule extends SimpleModule {

  addSerializer(classOf[TradeStatus], new TradeStatusSerializer)

  override def getModuleName: String = getClass.getSimpleName

  class TradeStatusSerializer extends JsonSerializer[TradeStatus] {
    def serialize(status: TradeStatus, jGen: JsonGenerator, serializerProvider: SerializerProvider): Unit = {
      jGen.writeString(status.toString)
    }
  }

}
