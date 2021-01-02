package com.iamsmkr.datastax.model

import java.time.Instant
import java.util.UUID

import com.datastax.oss.driver.api.core.cql.Row

case class Videos(videoId: UUID, title: String, addedDate: Instant)

object Videos {
  final val TABLE_NAME = "videos"

  import Columns._

  def getVideoFromRow(r: Row): Videos =
    Videos(r.getUuid(VIDEO_ID), r.getString(TITLE), r.getInstant(ADDED_DATE))

  object Columns {
    final lazy val VIDEO_ID = "video_id"
    final lazy val TITLE = "title"
    final lazy val ADDED_DATE = "added_date"
  }

}
