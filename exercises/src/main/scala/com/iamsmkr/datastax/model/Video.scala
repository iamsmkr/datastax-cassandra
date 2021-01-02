package com.iamsmkr.datastax.model

import java.time.Instant
import java.util.UUID

import com.datastax.oss.driver.api.core.cql.Row
import com.datastax.oss.driver.api.core.uuid.Uuids

case class Video(videoId: UUID = Uuids.timeBased(), title: String, addedDate: Instant = Instant.now())

object Video {
  final val TABLE_NAME = "videos"

  import Columns._

  def getVideoFromRow(r: Row): Video =
    Video(r.getUuid(VIDEO_ID), r.getString(TITLE), r.getInstant(ADDED_DATE))

  object Columns {
    final lazy val VIDEO_ID = "video_id"
    final lazy val TITLE = "title"
    final lazy val ADDED_DATE = "added_date"
  }

}
