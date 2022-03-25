package models

sealed trait Author

case class UniAuthor(name: String, email: String, age: Int, country: String)
    extends AppEntity
    with Author //In this case, an author is related to Books, although the relationship is only visible from the book side

case class BiAuthor(name: String,
                    email: String,
                    age: Int,
                    country: String,
                    books: List[Book])
    extends AppEntity
    with Author //In this case an author is related to Books, and the relationship is captured in both entity
