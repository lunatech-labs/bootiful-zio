import services.Program.{powerUpComputer, regularStatComputer}
import zio.Console.printLine
import zio.{Scope, ZEnv, ZIO, ZIOAppArgs, ZIOAppDefault}

object Main extends ZIOAppDefault {

  //Sample data
  val numbers = List(1d, 2d, 3d, 4d, 5d)

  override def run: ZIO[ZEnv with ZIOAppArgs with Scope, Any, Any] = {
    //Here we execute these effects to see their output
    regularStatComputer(numbers)
      .zip(powerUpComputer(numbers))
      .tap {
        case (regular, powerUp) =>
          printLine(s"Regular $regular") *>
            printLine("") *>
            printLine(s"PowerUp $powerUp")
      }
      .exitCode
  }
}
