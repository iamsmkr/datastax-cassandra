package com.iamsmkr.datastax.repository

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.querybuilder.QueryBuilder
import com.datastax.oss.driver.api.querybuilder.select.Select
import com.iamsmkr.datastax.model.Videos
import scala.jdk.CollectionConverters._

class VideosDao private(session: CqlSession) {

  private final val TABLE_NAME = "videos"

  def getAll: List[Videos] = {
    val select: Select = QueryBuilder.selectFrom(TABLE_NAME).all()
    val rs = session.execute(select.build())

    rs.map { r =>
      Videos(r.getUuid("video_id"), r.getString("title"), r.getInstant("added_date"))
    }.all().asScala.toList
  }
}

object VideosDao {
  def apply(session: CqlSession): VideosDao = {
    new VideosDao(session)
  }
}
