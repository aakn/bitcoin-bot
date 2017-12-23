package bot.commands

import javax.inject.Inject

import akka.actor.ActorSystem
import com.google.inject.AbstractModule
import play.libs.concurrent.CustomExecutionContext

import scala.concurrent.ExecutionContext

class CommandsModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[CommandExecutionContext])
      .to(classOf[CommandExecutionContextImpl])
  }
}

trait CommandExecutionContext extends ExecutionContext

class CommandExecutionContextImpl @Inject()(system: ActorSystem)
  extends CustomExecutionContext(system, "my.executor") with CommandExecutionContext

