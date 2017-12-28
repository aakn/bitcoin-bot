package bot.commands

import javax.inject.Inject

import akka.actor.ActorSystem
import com.google.inject.AbstractModule
import com.google.inject.assistedinject.{Assisted, FactoryModuleBuilder}
import org.joda.time.DateTime
import play.libs.concurrent.CustomExecutionContext

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.FiniteDuration

class CommandsModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[CommandExecutionContext])
      .to(classOf[CommandExecutionContextImpl])
    install(new FactoryModuleBuilder().build(classOf[ChartDataCommandBuilder]))
  }
}

trait CommandExecutionContext extends ExecutionContext

class CommandExecutionContextImpl @Inject()(system: ActorSystem)
  extends CustomExecutionContext(system, "my.executor") with CommandExecutionContext


trait ChartDataCommandBuilder {
  def apply(currencyPair: String, @Assisted("startDate") startDate: DateTime,
            @Assisted("endDate") endDate: DateTime, period: FiniteDuration): ChartDataCommand
}

