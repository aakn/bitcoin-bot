package bot.commands

import com.netflix.hystrix.HystrixCommandGroupKey

object GroupKeys {
  def poloniex(): HystrixCommandGroupKey = () => "poloniex"
}