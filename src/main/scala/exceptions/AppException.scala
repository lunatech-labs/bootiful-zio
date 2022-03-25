package exceptions

//Exception classes
sealed trait AppException

/* Author Related Exceptions */
class AuthorException(message: String)
    extends RuntimeException(message)
    with AppException
final case class AuthorNotFoundWithIdException(id: String)
    extends AuthorException(s"Author not found with id $id")
case object AuthorAlreadyExistException
    extends AuthorException("Author already exist")
case object AuthorAlreadyExistWithEmailException
    extends AuthorException("An author already exist with the email passed")

/* Book Related Exceptions */
class BookException(message: String)
    extends RuntimeException(message)
    with AppException
final case class BookNotFoundWithIdException(id: String)
    extends BookException(s"Book not found with id $id")
case object BookAlreadyExistException
    extends BookException("Book already exist")
