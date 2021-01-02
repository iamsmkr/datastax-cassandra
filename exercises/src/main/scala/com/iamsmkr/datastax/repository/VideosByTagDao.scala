package com.iamsmkr.datastax.repository

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.querybuilder.QueryBuilder
import com.datastax.oss.driver.api.querybuilder.select.Select
import com.iamsmkr.datastax.model.VideosByTag
import scala.jdk.CollectionConverters._

class VideosByTagDao private(session: CqlSession) {
  private final val TABLE_NAME = "videos_by_tag"

  def getAll: List[VideosByTag] = {
    val select: Select = QueryBuilder.selectFrom(TABLE_NAME).all()
    val rs = session.execute(select.build())

    rs.map { r =>
      VideosByTag(r.getUuid("video_id"), r.getString("title"), r.getInstant("added_date"), r.getString("tag"))
    }.all().asScala.toList
  }
}

object VideosByTagDao {
  def apply(session: CqlSession): VideosByTagDao = new VideosByTagDao(session)
}
