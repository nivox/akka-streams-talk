package io.github.nivox.akka.stream

import akka.actor._
import akka.stream.scaladsl._
import akka.NotUsed

import scala.concurrent.duration._
import akka.stream.FlowShape
import scala.concurrent.Await

import scala.concurrent.ExecutionContext.Implicits.global
import akka.stream.Attributes

object Sample3 extends App {

  implicit val system = ActorSystem("akka-stream-demo")

  def fryingPanFlow(
      fryingPanId: Int
  ): Flow[BatterScoop, CookedPancake, NotUsed] = {
    Flow[BatterScoop].mapAsync(1) { scoop =>
      for {
        hcPancacke <- {
          println(
            s"Cooking batter [${scoop.id}] on frying pan [${fryingPanId}]"
          )
          scoop.cookFor(50.millis)
        }
        cookedPancake <- {
          println(
            s"Cooking pancake [${hcPancacke.id}] on frying pan [${fryingPanId}]"
          )
          hcPancacke.cookFor(25.millis)
        }
      } yield cookedPancake
    }
  }

  val cookFlow: Flow[BatterScoop, CookedPancake, NotUsed] = {
    Flow.fromGraph(GraphDSL.create() { implicit builder =>
      import GraphDSL.Implicits._

      val dispatchBatter = builder.add(Balance[BatterScoop](2))
      val retrievePancakes = builder.add(Merge[CookedPancake](2))

      dispatchBatter.outlets.zip(retrievePancakes.inlets).zipWithIndex.map {
        case ((batterOut, pancakeIn), idx) =>
          batterOut ~> fryingPanFlow(idx).async.addAttributes(
            Attributes.inputBuffer(initial = 1, max = 1)
          ) ~> pancakeIn
      }

      FlowShape(dispatchBatter.in, retrievePancakes.out)
    })
  }

  val doneF = Source(1.to(10))
    .map(id => BatterScoop(id))
    .via(cookFlow)
    .runWith(Sink.foreach { pancake =>
      println(s"Pancake [${pancake.id}] is done!")
    })

  Await.result(doneF, Duration.Inf)
  system.terminate()
}
