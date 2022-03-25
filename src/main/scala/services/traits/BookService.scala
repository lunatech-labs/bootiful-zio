package services.traits

import exceptions.BookException
import localUtils.datasource.traits.BookLocalDB
import models.Book
import services.impls.BookServiceImpl.{
  BookServiceLiveImpl,
  BookServiceLocalImpl
}
import zio.{Accessible, Function0ToLayerOps, Function1ToLayerOps, URLayer}

trait BookService extends CrudServices[Book, BookException]

object BookService extends Accessible[BookService] {
  val live: URLayer[Any, BookServiceLiveImpl] =
    (BookServiceLiveImpl.apply _).toLayer
  val local: URLayer[BookLocalDB, BookServiceLocalImpl] =
    (BookServiceLocalImpl.apply _).toLayer
}
