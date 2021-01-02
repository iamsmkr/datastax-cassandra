package com.iamsmkr.datastax.model

import java.time.Instant
import java.util.UUID

case class VideosByTag(videoId: UUID, title: String, addedDate: Instant, tag: String)
