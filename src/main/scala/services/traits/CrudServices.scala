package services.traits

import exceptions.AppException
import models.AppEntity
import zio.{IO, UIO}

/**
  * This is a base trait that defines simple CRUD operations
  * @tparam A this is a persistable entity
  * @tparam E this is the possible (allowed) exception an endpoint can fail with
  */
trait CrudServices[A <: AppEntity, E <: AppException] {
  def create(value: A): IO[E, A]
  def readOne(entityId: String): IO[E, A]
  def readAll(): UIO[List[A]]
  def update(entityId: String, updateData: A): IO[E, A]
  def deleteById(entityId: String): IO[E, Boolean]
  def delete(entity: A): IO[E, Boolean]
}
