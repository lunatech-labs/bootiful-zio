package services.traits

import exceptions.AuthorException
import localUtils.datasource.traits.AuthorLocalDB
import models.UniAuthor
import services.impls.AuthorServiceImpl.{
  AuthorServiceLiveImpl,
  AuthorServiceLocalImpl
}
import zio.{Accessible, Function0ToLayerOps, Function1ToLayerOps, IO, URLayer}

trait AuthorService extends CrudServices[UniAuthor, AuthorException] {

  def fetchByEmail(email: String): IO[AuthorException, UniAuthor]
}

object AuthorService extends Accessible[AuthorService] {
  val live: URLayer[Any, AuthorServiceLiveImpl] =
    (AuthorServiceLiveImpl.apply _).toLayer
  val local: URLayer[AuthorLocalDB, AuthorServiceLocalImpl] =
    (AuthorServiceLocalImpl.apply _).toLayer
}
