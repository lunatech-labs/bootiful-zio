package services.impls

import services.{BasicMathOps, SimpleStatService}
import zio.UIO

//This is an implementation of the SimpleStatService which depends on the BasicMathOps. Hence for us to use this implementation,
//we need to make sure that the BasicMathOps is available in the environment.
case class SimpleStatServiceImpl(basicMathOps: BasicMathOps)
    extends SimpleStatService {

  override def variance(values: Seq[Double]): UIO[Double] = {

    def sumTwo(a: UIO[Double], b: UIO[Double]): UIO[Double] =
      a.zipWith(b)((a, b) => basicMathOps.add(a, b)).flatten

    for {
      avg <- average(values)
      diffs = values.map(v => basicMathOps.subtract(v, avg))
      squaredDiffs = diffs.map(_.flatMap(d => basicMathOps.multiply(d, d)))
      sumOfSquaredDiffs = squaredDiffs.foldLeft(UIO.succeed(0.0))(sumTwo)
      variance <- sumOfSquaredDiffs.flatMap(basicMathOps.divide(_, values.size))
    } yield variance
  }

  override def average(values: Seq[Double]): UIO[Double] =
    for {
      sumOfValues <- sum(values)
      avg <- basicMathOps.divide(sumOfValues, values.size)
    } yield avg

  override def sum(values: Seq[Double]): UIO[Double] =
    values.foldLeft(UIO.succeed(0.0))(
      (a, b) => a.flatMap(basicMathOps.add(_, b))
    )

}
