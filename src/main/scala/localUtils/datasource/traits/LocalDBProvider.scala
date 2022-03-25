package localUtils.datasource.traits

import localUtils.datasource.impl.LocalDBProviderImpl.{
  AuthorDBProvider,
  BookDBProvider
}
import models.{AppEntity, Book, UniAuthor}
import zio.{Accessible, Function0ToLayerOps, URLayer}

trait LocalDBProvider[A <: AppEntity] {
  def getDB: Map[String, A]
}

trait AuthorLocalDB extends LocalDBProvider[UniAuthor]
trait BookLocalDB extends LocalDBProvider[Book]

object AuthorLocalDB extends Accessible[AuthorLocalDB] {
  val localDB: URLayer[Any, AuthorDBProvider] = {
    (AuthorDBProvider.apply _).toLayer
  }
}

object BookLocalDB extends Accessible[BookLocalDB] {
  val localDB: URLayer[Any, BookDBProvider] = (BookDBProvider.apply _).toLayer
}
