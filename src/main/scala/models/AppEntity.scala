package models

import java.util.UUID

abstract class AppEntity(val entityId: String = UUID.randomUUID().toString)
