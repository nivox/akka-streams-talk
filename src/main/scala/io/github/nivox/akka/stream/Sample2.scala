package io.github.nivox.akka.stream

import akka.actor._
import akka.stream.scaladsl._
import akka.NotUsed
import scala.concurrent.Await

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object Sample2 extends App {
  implicit val system = ActorSystem("akka-stream-demo")

  val fryingPan1: Flow[BatterScoop, HalfCookedPancake, NotUsed] = {
    Flow[BatterScoop].mapAsync(1) { scoop =>
      println(s"Cooking batter scoop [${scoop.id}] on frying pan 1")
      scoop.cookFor(50.millis)
    }
  }

  val fryingPan2: Flow[HalfCookedPancake, CookedPancake, NotUsed] = {
    Flow[HalfCookedPancake].mapAsync(1) { hcPancake =>
      println(s"Cooking pancake [${hcPancake.id}] on frying pan 2")
      hcPancake.cookFor(25.millis)
    }
  }

  val doneF = Source(1.to(10))
    .map(id => BatterScoop(id))
    .via(fryingPan1.async)
    .via(fryingPan2.async)
    .runWith(Sink.foreach { pancake =>
      println(s"Pancake [${pancake.id}] is done!")
    })

  Await.result(doneF, Duration.Inf)
  system.terminate()
}
