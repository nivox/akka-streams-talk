package io.github.nivox.akka.stream

import akka.stream.stage.GraphStage
import akka.stream.FlowShape
import akka.stream.Attributes
import akka.stream.stage.GraphStageLogic
import akka.stream.Inlet
import akka.stream.Outlet
import akka.stream.stage.InHandler
import akka.stream.stage.OutHandler

class CustomStage[T] extends GraphStage[FlowShape[T, (T, Int)]] {
  val in: Inlet[T] = Inlet("CustomStage.in")
  val out: Outlet[(T, Int)] = Outlet("CustomStage.out")

  override def shape: FlowShape[T, (T, Int)] = FlowShape(in, out)

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic = new GraphStageLogic(shape) {
    var counter: Int = 0

    setHandler(in, new InHandler {
      override def onPush(): Unit = {
          val input = grab(in)
          val output = (input, counter)
          counter += 1
          push(out, output)
      }
    })

    setHandler(out, new OutHandler {
      override def onPull(): Unit = pull(in)
    })
  }
}
