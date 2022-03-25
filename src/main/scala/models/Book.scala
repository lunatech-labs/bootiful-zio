package models

case class Book(title: String, ownedBy: Author, authors: List[Author])
    extends AppEntity
