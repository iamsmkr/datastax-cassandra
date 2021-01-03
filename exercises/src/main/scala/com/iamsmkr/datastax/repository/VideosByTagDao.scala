package com.iamsmkr.datastax.repository

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.`type`.DataTypes
import com.datastax.oss.driver.api.core.cql.{PreparedStatement, SimpleStatement}
import com.datastax.oss.driver.api.querybuilder.{QueryBuilder, SchemaBuilder}
import com.datastax.oss.driver.api.querybuilder.QueryBuilder.bindMarker
import com.iamsmkr.datastax.model.VideoByTag
import com.iamsmkr.datastax.model.VideoByTag.Columns._
import com.iamsmkr.datastax.model.VideoByTag.{TABLE_NAME, getVideoByTagFromRow}
import com.iamsmkr.datastax.repository.VideosByTagDao.Queries._

import scala.jdk.CollectionConverters._

class VideosByTagDao private(session: CqlSession) {

  private def createTable(): Unit = {
    val tbl = SchemaBuilder.createTable(TABLE_NAME).ifNotExists()
      .withPartitionKey(TAG, DataTypes.TEXT)
      .withClusteringColumn(ADDED_DATE, DataTypes.TIMESTAMP)
      .withClusteringColumn(VIDEO_ID, DataTypes.TIMEUUID)
      .withColumn(TITLE, DataTypes.TEXT)

    session.execute(tbl.build())
  }

  def getAll: List[VideoByTag] = {
    val rs = session.execute(GET_ALL_QUERY)
    rs.map(getVideoByTagFromRow).all().asScala.toList
  }

  def getByTag(tag: String): List[VideoByTag] = {
    val stmt: PreparedStatement = session.prepare(GET_BY_TAG_QUERY)
    val rs = session.execute(stmt.bind(tag))

    rs.map(getVideoByTagFromRow).all().asScala.toList
  }
}

object VideosByTagDao {
  def apply(session: CqlSession): VideosByTagDao = {
    val videosByTagDao = new VideosByTagDao(session)
    videosByTagDao.createTable()
    videosByTagDao
  }

  object Queries {
    final lazy val GET_ALL_QUERY: SimpleStatement =
      QueryBuilder.selectFrom(TABLE_NAME).all().build()

    final lazy val GET_BY_TAG_QUERY: SimpleStatement =
      QueryBuilder.selectFrom(TABLE_NAME).all().whereColumn("tag").isEqualTo(bindMarker()).build()
  }

}
