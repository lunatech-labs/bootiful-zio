package services

import zio.{URLayer, ZIO, ZLayer}

//In this object we define the programs (effects) that we want to run.
object Program {

  val powerConst: URLayer[Any, Double] = ZLayer.succeed(1.0)

  //Here we have defined a simple program that takes list of doubles and returns its statistics (sum, average, variance)
  val statComputer
    : List[Double] => ZIO[SimpleStatService, Nothing, StatResult] = numbers =>
    for {
      statService <- ZIO.service[SimpleStatService]
      sum <- statService.sum(numbers)
      average <- statService.average(numbers)
      variance <- statService.variance(numbers)
    } yield StatResult(sum, average, variance)

  //Observe here that for this effect to run, we have supplied a layer (SimpleStatService) that depends on (BasicMathOps)
  val regularStatComputer: List[Double] => ZIO[Any, Nothing, StatResult] =
    numbers =>
      statComputer(numbers)
        .provide(SimpleStatService.live, BasicMathOps.regularOps)

  //Also, observe that depending on the layer (BasicMathOps) we have provided, we may need to supply another layer it depends on.
  //For example (BaseMathOps.powerUpOps) depends on a Double, which we have supplied as another layer (powerConst)
  val powerUpComputer: List[Double] => ZIO[Any, Nothing, StatResult] =
    numbers =>
      statComputer(numbers)
        .provide(SimpleStatService.live, BasicMathOps.powerUpOps, powerConst)
}

case class StatResult(sum: Double, average: Double, variance: Double) {
  override def toString: String =
    "Statistics: " + "\n\t" + "Sum: " + sum + "\n\tAverage: " + average + "\n\tVariance: " + variance
}
