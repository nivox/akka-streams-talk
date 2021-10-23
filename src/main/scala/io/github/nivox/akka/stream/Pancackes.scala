package io.github.nivox.akka.stream

import akka.actor.ActorSystem
import scala.concurrent.duration._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext


case class BatterScoop(id: Int) {
  def cookFor(duration: FiniteDuration)(implicit system: ActorSystem, ec: ExecutionContext): Future[HalfCookedPancake] = {
    akka.pattern.after(duration, system.scheduler)(
      Future.successful(HalfCookedPancake(id))
    )
  }
}
case class HalfCookedPancake(id: Int) {
   def cookFor(duration: FiniteDuration)(implicit system: ActorSystem, ec: ExecutionContext): Future[CookedPancake] = {
    akka.pattern.after(duration, system.scheduler)(
      Future.successful(CookedPancake(id))
    )
  } 
}
case class CookedPancake(id: Int)

