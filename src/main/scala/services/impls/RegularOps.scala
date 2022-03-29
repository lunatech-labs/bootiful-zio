package services.impls

import services.BasicMathOps
import zio.UIO

//This is a naive implementation of BasicMathOps (It does the regular math operations)
case class RegularOps() extends BasicMathOps {

  override def add(x: Double, y: Double): UIO[Double] = UIO.succeed(x + y)

  override def subtract(x: Double, y: Double): UIO[Double] = UIO.succeed(x - y)

  override def divide(x: Double, y: Double): UIO[Double] = UIO.succeed {
    if (y == 0) 0
    else x / y
  }

  override def multiply(x: Double, y: Double): UIO[Double] = UIO.succeed(x * y)
}
