package localUtils.utils

import exceptions.AppException
import models.AppEntity
import zio.IO

object LocalDBOps {

  //Saves to local DB and returns an updated result (DB)
  def saveOrThrow[A <: AppEntity, B <: AppException](
    db: Map[String, A],
    data: A,
    exception: => B,
    extraCheck: Option[(A => Boolean, Option[B])] = None
  ): IO[B, Map[String, A]] =
    (db.get(data.entityId), extraCheck) match {
      case (None, None) =>
        IO.succeed(db.updated(data.entityId, data))
      case (None, Some((extraVerification, maybeCustomException))) =>
        if (extraVerification(data)) IO.succeed(db.updated(data.entityId, data))
        else IO.fail(maybeCustomException.getOrElse(exception))
      case _ => IO.fail(exception)
    }

  //Attempt to find data in the DB or fail
  def findOrThrow[A <: AppEntity, B <: AppException](
    db: Map[String, A],
    data: String
  )(exception: => B): IO[B, A] =
    IO.fromOption(db.get(data)).orElseFail(exception)
}
