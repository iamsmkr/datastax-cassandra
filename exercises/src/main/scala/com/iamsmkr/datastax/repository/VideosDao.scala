package com.iamsmkr.datastax.repository

import java.util.UUID

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.`type`.DataTypes
import com.datastax.oss.driver.api.core.cql.{BoundStatement, PreparedStatement, SimpleStatement}
import com.datastax.oss.driver.api.querybuilder.{QueryBuilder, SchemaBuilder}
import com.datastax.oss.driver.api.querybuilder.QueryBuilder.bindMarker
import com.iamsmkr.datastax.model.Video
import com.iamsmkr.datastax.model.Video.Columns._
import com.iamsmkr.datastax.model.Video.{TABLE_NAME, getVideoFromRow}
import com.iamsmkr.datastax.repository.VideosDao.Queries._

import scala.jdk.CollectionConverters._

class VideosDao private(session: CqlSession) {

  private def createTable(): Unit = {
    val tbl = SchemaBuilder.createTable(TABLE_NAME).ifNotExists()
      .withPartitionKey(VIDEO_ID, DataTypes.TIMEUUID)
      .withColumn(TITLE, DataTypes.TEXT)
      .withColumn(ADDED_DATE, DataTypes.TIMESTAMP)

    session.execute(tbl.build())
  }

  def getAll: List[Video] = {
    val rs = session.execute(GET_ALL_QUERY)
    rs.map(getVideoFromRow).all().asScala.toList
  }

  def getById(uuid: UUID): Video = {
    val stmt = session.prepare(GET_BY_ID_QUERY).bind(uuid)
    val row = session.execute(stmt).one()
    getVideoFromRow(row)
  }

  def insert(video: Video): UUID = {
    val preparedStatement: PreparedStatement = session.prepare(INSERT_QUERY)

    val boundStatement: BoundStatement = preparedStatement.bind()
      .setUuid(0, video.videoId)
      .setString(1, video.title)
      .setInstant(2, video.addedDate)

    session.execute(boundStatement)
    video.videoId
  }

  def deleteById(uuid: UUID): UUID = {
    val stmt = session.prepare(DELETE_QUERY).bind(uuid)
    session.execute(stmt)
    uuid
  }
}

object VideosDao {
  def apply(session: CqlSession): VideosDao = {
    val videosDao = new VideosDao(session)
    videosDao.createTable()
    videosDao
  }

  object Queries {
    final lazy val GET_ALL_QUERY: SimpleStatement =
      QueryBuilder.selectFrom(TABLE_NAME).all().build()

    final lazy val GET_BY_ID_QUERY =
      QueryBuilder.selectFrom(TABLE_NAME).all()
        .whereColumn(VIDEO_ID).isEqualTo(bindMarker()).build()

    final lazy val INSERT_QUERY: SimpleStatement =
      QueryBuilder.insertInto(TABLE_NAME)
        .value(VIDEO_ID, bindMarker())
        .value(TITLE, bindMarker())
        .value(ADDED_DATE, bindMarker()).build()

    final lazy val DELETE_QUERY: SimpleStatement =
      QueryBuilder.deleteFrom(TABLE_NAME)
        .whereColumn(VIDEO_ID).isEqualTo(bindMarker()).build()
  }

}
