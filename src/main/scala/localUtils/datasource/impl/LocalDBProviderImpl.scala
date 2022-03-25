package localUtils.datasource.impl

import localUtils.datasource.traits.{AuthorLocalDB, BookLocalDB}
import models.{Book, UniAuthor}

object LocalDBProviderImpl {

  case class AuthorDBProvider() extends AuthorLocalDB {
    override def getDB: Map[String, UniAuthor] = Map.empty[String, UniAuthor]
  }

  case class BookDBProvider() extends BookLocalDB {
    override def getDB: Map[String, Book] = Map.empty[String, Book]
  }

}
