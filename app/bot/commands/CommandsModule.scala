package bot.commands

import com.google.inject.AbstractModule
import com.google.inject.assistedinject.{Assisted, FactoryModuleBuilder}
import org.joda.time.DateTime

import scala.concurrent.duration.Duration

class CommandsModule extends AbstractModule {
  override def configure(): Unit = {
    install(new FactoryModuleBuilder().build(classOf[ChartDataCommandBuilder]))
  }
}

trait ChartDataCommandBuilder {
  def apply(currencyPair: String, @Assisted("startDate") startDate: DateTime,
            @Assisted("endDate") endDate: DateTime, period: Duration): ChartDataCommand
}

