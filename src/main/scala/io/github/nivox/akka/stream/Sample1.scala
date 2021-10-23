package io.github.nivox.akka.stream

import akka.Done
import akka.actor._
import akka.stream._
import akka.stream.scaladsl._
import scala.concurrent.duration._
import scala.concurrent.Future
import akka.NotUsed
import scala.concurrent.Await

object Sample1 extends App {
  implicit val system: ActorSystem = ActorSystem("akka-stream-demo")

  val source: Source[String, NotUsed] =
    Source.repeat("Akka stream for the win!")

  val flow: Flow[String, Char, NotUsed] = Flow[String].zipWithIndex
    .map { case (s, idx) => " " * idx.toInt + s }
    .take(3)
    .mapConcat(_.toList :+ '\n')
    .throttle(1, 50.millis, 1, ThrottleMode.shaping)

  val sink: Sink[Char, Future[Done]] = Sink.foreach(print)

  val blueprint: RunnableGraph[Future[Done]] =
    source.via(flow).toMat(sink)(Keep.right)

  val doneF: Future[Done] = blueprint.run()
  Await.result(doneF, Duration.Inf)
  system.terminate()
}

object Sample1Inferred extends App {
  implicit val system: ActorSystem = ActorSystem("akka-stream-demo")

  val source = Source.repeat("Akka stream for the win!")

  val flow = Flow[String].zipWithIndex
    .map { case (s, idx) => " " * idx.toInt + s }
    .take(3)
    .mapConcat(_.toList :+ '\n')
    .throttle(1, 50.millis, 1, ThrottleMode.shaping)

  val sink = Sink.foreach(print)

  val blueprint = source.via(flow).toMat(sink)(Keep.right)

  val doneF = blueprint.run()
  Await.result(doneF, Duration.Inf)
  system.terminate()
}
