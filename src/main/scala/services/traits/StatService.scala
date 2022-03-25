package services.traits

import models.AuthorBookStat.AuthorBookStats
import services.impls.StatServiceImpl.{
  StatServiceLiveImpl,
  StatServiceLocalImpl
}
import zio.{Function0ToLayerOps, Function2ToLayerOps, UIO, URLayer}

trait StatService {
  def generateStatistics(): UIO[AuthorBookStats]
}

object StatService {

  val live: URLayer[Any, StatServiceLiveImpl] =
    (StatServiceLiveImpl.apply _).toLayer
  val local: URLayer[AuthorService with BookService, StatServiceLocalImpl] =
    (StatServiceLocalImpl.apply _).toLayer
}
