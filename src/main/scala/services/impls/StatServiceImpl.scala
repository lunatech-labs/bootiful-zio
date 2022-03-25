package services.impls

import models.AuthorBookStat.AuthorBookStats
import services.traits.{AuthorService, BookService, StatService}
import zio.UIO

object StatServiceImpl {

  case class StatServiceLiveImpl() extends StatService {
    override def generateStatistics(): UIO[AuthorBookStats] = ???
  }

  case class StatServiceLocalImpl(authorService: AuthorService,
                                  bookService: BookService)
      extends StatService {
    override def generateStatistics(): UIO[AuthorBookStats] = ???
  }
}
