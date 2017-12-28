package lib.hystrix

import com.netflix.hystrix.{HystrixCommandGroupKey, HystrixObservableCommand}
import rx.Observable
import rx.lang.scala.subjects.ReplaySubject

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

abstract class HystrixFutureCommand[T](groupKey: HystrixCommandGroupKey)(implicit ec: ExecutionContext)
  extends HystrixObservableCommand[T](groupKey) {

  override def construct(): Observable[T] = {
    val channel = ReplaySubject[T]()

    run().onComplete {
      case Success(v) => {
        channel.onNext(v)
        channel.onCompleted()
      }
      case Failure(t) => {
        channel.onError(t)
        channel.onCompleted()
      }
    }

    channel.asJavaSubject
  }

  def run(): Future[T]

}
