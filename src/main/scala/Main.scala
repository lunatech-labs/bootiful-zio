import exceptions.AuthorException
import localUtils.datasource.traits.AuthorLocalDB
import models.UniAuthor
import services.traits.AuthorService
import zio._
import zio.Console.{printLine, readLine}

import java.io.IOException
import scala.util.{Failure, Success, Try}

object Main extends ZIOAppDefault {
  val safeGetAge: ZIO[Console, IOException, Int] = {
    for {
      _ <- printLine("Please enter an author age")
      authorAgeStr <- readLine
      authorAge = Try(authorAgeStr.toInt) match {
        case Failure(_)     => 0
        case Success(value) => value
      }
    } yield authorAge
  }

  val addAuthorToLocalDB
    : UniAuthor => ZIO[AuthorService, AuthorException, UniAuthor] = author =>
    AuthorService(_.create(author))

  val fetchAllAuthorsFromLocalDB: ZIO[AuthorService, Nothing, List[UniAuthor]] =
    AuthorService(_.readAll())

  val sampleAuthorFromLogProgram
    : ZIO[AuthorService with Console, Exception, UniAuthor] =
    for {
      _ <- printLine("Please enter an author name")
      authorName <- readLine
      _ <- printLine("Please enter an author email")
      authorMail <- readLine
      _ <- printLine("Please enter an author country")
      authorCountry <- readLine
      authorAge <- safeGetAge
      author = UniAuthor(authorName, authorMail, authorAge, authorCountry)
      _ <- printLine(s"Data entered has been converted to ${author.toString}")
    } yield author

  val fetchByEmail: String => ZIO[AuthorService, AuthorException, UniAuthor] =
    email => AuthorService(_.fetchByEmail(email))

  val verifyProgram: ZIO[AuthorService with Console,
                         Exception,
                         (UniAuthor, UniAuthor, List[UniAuthor])] =
    for {
      author <- sampleAuthorFromLogProgram
      saveAuthor <- addAuthorToLocalDB(author)
      _ <- printLine(s"Author saved to the database ${saveAuthor.toString}")
      fetchedAuthors <- fetchAllAuthorsFromLocalDB
      _ <- printLine(
        s"Authors fetched from the database ${fetchedAuthors.toString}"
      )
      fetchByEmail <- fetchByEmail(author.email)
    } yield (saveAuthor, fetchByEmail, fetchedAuthors)

  override def run: ZIO[ZEnv with ZIOAppArgs with Scope, Any, Any] =
    //I have written this just to do a naive test that the local implementation we have written works just fine, although we have to persis this to a real DB
    ZIO
      .iterate(1)(_ <= 2)(
        iterations =>
          verifyProgram
            .map {
              case (savedAuthor, fetchByEmail, authorsFromDB) =>
                assert(authorsFromDB.nonEmpty)
                assert(authorsFromDB.size == iterations)
                assert(savedAuthor == fetchByEmail)

                iterations + 1
          }
      )
      .provide(Console.live, AuthorService.local, AuthorLocalDB.localDB)

}
