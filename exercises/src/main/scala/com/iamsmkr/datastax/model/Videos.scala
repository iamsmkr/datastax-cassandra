package com.iamsmkr.datastax.model

import java.time.Instant
import java.util.UUID

case class Videos(videoId: UUID, title: String, addedDate: Instant)
