package services.impls

import exceptions.{
  BookAlreadyExistException,
  BookException,
  BookNotFoundWithIdException
}
import localUtils.utils.LocalDBOps.{findOrThrow, saveOrThrow}
import localUtils.datasource.traits.BookLocalDB
import models.Book
import services.traits.BookService
import zio.{IO, UIO}

object BookServiceImpl {

  case class BookServiceLiveImpl() extends BookService {
    override def create(value: Book): IO[BookException, Book] = ???
    override def readOne(entityId: String): IO[BookException, Book] = ???
    override def readAll(): UIO[List[Book]] = ???
    override def update(entityId: String,
                        updateData: Book): IO[BookException, Book] = ???
    override def deleteById(entityId: String): IO[BookException, Boolean] = ???
    override def delete(entity: Book): IO[BookException, Boolean] = ???
  }

  case class BookServiceLocalImpl(bookLocalDB: BookLocalDB)
      extends BookService {
    var database: Map[String, Book] = bookLocalDB.getDB

    override def create(value: Book): IO[BookException, Book] =
      saveOrThrow(database, value, BookAlreadyExistException)
        .map(res => {
          database = res
          value
        })

    override def readOne(entityId: String): IO[BookException, Book] =
      findOrThrow(database, entityId)(BookNotFoundWithIdException(entityId))

    override def readAll(): UIO[List[Book]] =
      UIO.succeed(database.values.toList)

    override def update(entityId: String,
                        updateData: Book): IO[BookException, Book] =
      findOrThrow(database, entityId)(BookNotFoundWithIdException(entityId))
        .as {
          database = database.updatedWith(entityId)(_ => Some(updateData))
          updateData
        }

    override def delete(entity: Book): IO[BookException, Boolean] =
      deleteById(entity.entityId)

    override def deleteById(entityId: String): IO[BookException, Boolean] =
      findOrThrow(database, entityId)(BookNotFoundWithIdException(entityId))
        .as {
          database = database.removed(entityId)
          !database.contains(entityId)
        }
  }
}
