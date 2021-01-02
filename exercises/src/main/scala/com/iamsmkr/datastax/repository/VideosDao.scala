package com.iamsmkr.datastax.repository

import java.time.Instant
import java.util.UUID

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.cql.SimpleStatement
import com.datastax.oss.driver.api.querybuilder.QueryBuilder
import com.datastax.oss.driver.api.querybuilder.QueryBuilder.bindMarker
import com.iamsmkr.datastax.model.Video
import com.iamsmkr.datastax.model.Video.Columns._
import com.iamsmkr.datastax.model.Video.{TABLE_NAME, getVideoFromRow}
import com.iamsmkr.datastax.repository.VideosDao.Queries._

import scala.jdk.CollectionConverters._

class VideosDao private(session: CqlSession) {

  def getAll: List[Video] = {
    val rs = session.execute(GET_ALL_QUERY)
    rs.map(getVideoFromRow).all().asScala.toList
  }

  def insert(video: Video): UUID = {
    val stmt = session.prepare(INSERT_QUERY)

    val boundStmt = stmt.bind()
      .setUuid(0, video.videoId)
      .setString(1, video.title)
      .setInstant(2, video.addedDate)

    session.execute(boundStmt)
    video.videoId
  }
}

object VideosDao {
  def apply(session: CqlSession): VideosDao = new VideosDao(session)

  object Queries {
    final lazy val GET_ALL_QUERY: SimpleStatement =
      QueryBuilder.selectFrom(TABLE_NAME).all().build()

    final lazy val INSERT_QUERY: SimpleStatement =
      QueryBuilder.insertInto(TABLE_NAME)
        .value(VIDEO_ID, bindMarker())
        .value(TITLE, bindMarker())
        .value(ADDED_DATE, bindMarker()).build()
  }
}
