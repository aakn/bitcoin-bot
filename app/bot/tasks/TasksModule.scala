package bot.tasks

import play.api.inject.{SimpleModule, _}

class TasksModule extends SimpleModule(bind[BotTask].toSelf.eagerly())