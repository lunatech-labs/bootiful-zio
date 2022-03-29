package services.impls

import services.BasicMathOps
import zio.UIO

//This is another implementation of the BasicMathOps trait, but this time, it is dependent on a Double value to be in scope, it must be used
case class PowerUpOps(power: Double) extends BasicMathOps {

  override def add(x: Double, y: Double): UIO[Double] =
    UIO.succeed(powerUp(x + y))

  override def subtract(x: Double, y: Double): UIO[Double] =
    UIO.succeed(powerUp(x - y))

  override def divide(x: Double, y: Double): UIO[Double] = UIO.succeed {
    if (y == 0) 0
    else powerUp(x / y)
  }

  override def multiply(x: Double, y: Double): UIO[Double] =
    UIO.succeed(powerUp(x * y))

  def powerUp(value: Double): Double = math.pow(value, power)
}
