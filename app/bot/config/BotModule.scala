package bot.config

import com.fasterxml.jackson.databind.{ObjectMapper, SerializationFeature}
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.google.inject.AbstractModule

class BotModule extends AbstractModule {
  override def configure(): Unit = {
    val mapper = new ObjectMapper()
    mapper.registerModule(DefaultScalaModule)
    mapper.registerModule(new JodaModule())
    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
    bind(classOf[ObjectMapper]).toInstance(mapper)
  }
}
