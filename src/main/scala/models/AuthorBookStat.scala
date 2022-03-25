package models

//A naive stat could be of the form, cause we don't really care
case class AuthorBookStat(author: Author,
                          involvedIn: List[Book],
                          owns: List[Book])

object AuthorBookStat {
  type AuthorBookStats = List[AuthorBookStat]
}
