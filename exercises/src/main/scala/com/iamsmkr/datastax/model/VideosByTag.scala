package com.iamsmkr.datastax.model

import java.time.Instant
import java.util.UUID

import com.datastax.oss.driver.api.core.cql.Row

case class VideosByTag(videoId: UUID, title: String, addedDate: Instant, tag: String)

object VideosByTag {

  final val TABLE_NAME = "videos_by_tag"

  import Columns._

  def getVideoByTagFromRow(r: Row): VideosByTag =
    VideosByTag(r.getUuid(VIDEO_ID), r.getString(TITLE), r.getInstant(ADDED_DATE), r.getString(TAG))

  object Columns {
    final lazy val VIDEO_ID = "video_id"
    final lazy val TITLE = "title"
    final lazy val ADDED_DATE = "added_date"
    final lazy val TAG = "tag"
  }

}
