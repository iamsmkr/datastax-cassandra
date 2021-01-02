package com.iamsmkr.datastax.repository

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.cql.PreparedStatement
import com.datastax.oss.driver.api.querybuilder.QueryBuilder
import com.datastax.oss.driver.api.querybuilder.QueryBuilder.bindMarker
import com.datastax.oss.driver.api.querybuilder.select.Select
import com.iamsmkr.datastax.model.VideosByTag

import scala.jdk.CollectionConverters._

class VideosByTagDao private(session: CqlSession) {

  import VideosByTagDao.Queries._
  import VideosByTagDao.Columns._

  def getAll: List[VideosByTag] = {
    val rs = session.execute(GET_ALL_QUERY.build())

    rs.map { r =>
      VideosByTag(r.getUuid(VIDEO_ID), r.getString(TITLE), r.getInstant(ADDED_DATE), r.getString(TAG))
    }.all().asScala.toList
  }

  def getByTag(tag: String): List[VideosByTag] = {
    val stmt: PreparedStatement = session.prepare(GET_BY_TAG_QUERY.build())
    val rs = session.execute(stmt.bind(tag))

    rs.map { r =>
      VideosByTag(r.getUuid(VIDEO_ID), r.getString(TITLE), r.getInstant(ADDED_DATE), r.getString(TAG))
    }.all().asScala.toList
  }
}

object VideosByTagDao {
  def apply(session: CqlSession): VideosByTagDao = new VideosByTagDao(session)

  private final val TABLE_NAME = "videos_by_tag"

  object Columns {
    final lazy val VIDEO_ID = "video_id"
    final lazy val TITLE = "title"
    final lazy val ADDED_DATE = "added_date"
    final lazy val TAG = "tag"
  }

  object Queries {
    final lazy val GET_ALL_QUERY: Select = QueryBuilder.selectFrom(TABLE_NAME).all()
    final lazy val GET_BY_TAG_QUERY: Select = QueryBuilder.selectFrom(TABLE_NAME).all().whereColumn("tag").isEqualTo(bindMarker())
  }

}
