package services.impls

import exceptions.{
  AuthorAlreadyExistException,
  AuthorAlreadyExistWithEmailException,
  AuthorException,
  AuthorNotFoundWithIdException
}
import localUtils.utils.LocalDBOps.{findOrThrow, saveOrThrow}
import localUtils.datasource.traits.AuthorLocalDB
import models.UniAuthor
import services.traits.AuthorService
import zio.{IO, UIO}

object AuthorServiceImpl {

  case class AuthorServiceLiveImpl() extends AuthorService {
    override def create(value: UniAuthor): IO[AuthorException, UniAuthor] = ???
    override def readOne(entityId: String): IO[AuthorException, UniAuthor] = ???
    override def readAll(): UIO[List[UniAuthor]] = ???
    override def update(entityId: String,
                        updateData: UniAuthor): IO[AuthorException, UniAuthor] =
      ???
    override def deleteById(entityId: String): IO[AuthorException, Boolean] =
      ???
    override def delete(entity: UniAuthor): IO[AuthorException, Boolean] = ???

    override def fetchByEmail(email: String): IO[AuthorException, UniAuthor] =
      ???
  }

  case class AuthorServiceLocalImpl(authorLocalDB: AuthorLocalDB)
      extends AuthorService {
    var database: Map[String, UniAuthor] = authorLocalDB.getDB

    val emailValidation: UniAuthor => Boolean = data =>
      database.values.forall(_.email != data.email)

    override def create(value: UniAuthor): IO[AuthorException, UniAuthor] = {
      saveOrThrow(
        database,
        value,
        AuthorAlreadyExistException,
        Some(emailValidation, Some(AuthorAlreadyExistWithEmailException))
      ).map(res => {
        database = res
        value
      })
    }

    override def readOne(entityId: String): IO[AuthorException, UniAuthor] =
      findOrThrow(database, entityId)(AuthorNotFoundWithIdException(entityId))

    override def readAll(): UIO[List[UniAuthor]] =
      UIO.succeed(database.values.toList)

    override def update(
      entityId: String,
      updateData: UniAuthor
    ): IO[AuthorException, UniAuthor] = {
      findOrThrow(database, entityId)(AuthorNotFoundWithIdException(entityId))
        .as {
          database = database.updatedWith(entityId)(_ => Some(updateData))
          updateData
        }
    }

    override def delete(entity: UniAuthor): IO[AuthorException, Boolean] =
      deleteById(entity.entityId)

    override def deleteById(entityId: String): IO[AuthorException, Boolean] = {
      findOrThrow(database, entityId)(AuthorNotFoundWithIdException(entityId))
        .as {
          database = database.removed(entityId)
          !database.contains(entityId)
        }
    }

    override def fetchByEmail(email: String): IO[AuthorException, UniAuthor] =
      IO.fromOption(database.values.find(_.email == email))
        .orElseFail(AuthorNotFoundWithIdException(email))
  }
}
