package com.clovellytech.featurerequests.db.domain

import java.util.UUID

final case class Feature(
  userId: Option[UUID],
  title: String,
  description: String
)
