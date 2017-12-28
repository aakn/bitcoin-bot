package lib.hystrix

import com.netflix.hystrix.HystrixObservable

import scala.concurrent.{Future, Promise}

object Futures {

  private class ForPromiseObserver[T](p: Promise[T]) extends rx.Observer[T] {
    def onNext(t: T): Unit = p.trySuccess(t)

    def onError(e: Throwable): Unit = p.tryFailure(e)

    def onCompleted(): Unit = ()
  }

  implicit final class HystrixCommandWithScalaFuture[T](val cmd: HystrixObservable[T]) extends AnyVal {
    def future: Future[T] = {
      val promise = Promise[T]()
      val observer = new ForPromiseObserver(promise)

      cmd.observe().subscribe(observer)

      promise.future
    }
  }

}
