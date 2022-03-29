package services

import services.impls.SimpleStatServiceImpl
import zio.{Accessible, Function1ToLayerOps, UIO, URLayer}

//We define a naive statistics service that can be used in our application.
trait SimpleStatService {
  def average(values: Seq[Double]): UIO[Double]
  def sum(values: Seq[Double]): UIO[Double]
  def variance(values: Seq[Double]): UIO[Double]
}

//We define a layer as a value which can be supplied to environments where they are needed.
object SimpleStatService extends Accessible[SimpleStatService] {
  val live: URLayer[BasicMathOps, SimpleStatServiceImpl] =
    (SimpleStatServiceImpl.apply _).toLayer
}
