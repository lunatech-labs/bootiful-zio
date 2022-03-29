package services

import services.impls.{PowerUpOps, RegularOps}
import zio.{Accessible, Function0ToLayerOps, Function1ToLayerOps, UIO, URLayer}

//We define a naive service that just represents the basic math operations
trait BasicMathOps {
  def add(x: Double, y: Double): UIO[Double]
  def subtract(x: Double, y: Double): UIO[Double]
  def divide(x: Double, y: Double): UIO[Double]
  def multiply(x: Double, y: Double): UIO[Double]
}

//In its companion object we define layers as values which we will inject into environments where they are needed
object BasicMathOps extends Accessible[BasicMathOps] {
  val powerUpOps: URLayer[Double, PowerUpOps] = (PowerUpOps.apply _).toLayer
  val regularOps: URLayer[Any, RegularOps] = (RegularOps.apply _).toLayer
}
