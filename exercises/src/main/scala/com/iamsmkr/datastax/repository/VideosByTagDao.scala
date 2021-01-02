package com.iamsmkr.datastax.repository

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.cql.{PreparedStatement, SimpleStatement}
import com.datastax.oss.driver.api.querybuilder.QueryBuilder
import com.datastax.oss.driver.api.querybuilder.QueryBuilder.bindMarker
import com.iamsmkr.datastax.model.VideosByTag
import com.iamsmkr.datastax.model.VideosByTag.{TABLE_NAME, getVideoByTagFromRow}

import scala.jdk.CollectionConverters._

class VideosByTagDao private(session: CqlSession) {

  import VideosByTagDao.Queries._

  def getAll: List[VideosByTag] = {
    val rs = session.execute(GET_ALL_QUERY)
    rs.map(getVideoByTagFromRow).all().asScala.toList
  }

  def getByTag(tag: String): List[VideosByTag] = {
    val stmt: PreparedStatement = session.prepare(GET_BY_TAG_QUERY)
    val rs = session.execute(stmt.bind(tag))

    rs.map(getVideoByTagFromRow).all().asScala.toList
  }
}

object VideosByTagDao {
  def apply(session: CqlSession): VideosByTagDao = new VideosByTagDao(session)

  object Queries {
    final lazy val GET_ALL_QUERY: SimpleStatement =
      QueryBuilder.selectFrom(TABLE_NAME).all().build()

    final lazy val GET_BY_TAG_QUERY: SimpleStatement =
      QueryBuilder.selectFrom(TABLE_NAME).all().whereColumn("tag").isEqualTo(bindMarker()).build()
  }

}
