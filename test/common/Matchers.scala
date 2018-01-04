package common

import org.mockito.ArgumentMatchers

object Matchers {

  def eql[T](value: T): T = ArgumentMatchers.eq(value)

}
