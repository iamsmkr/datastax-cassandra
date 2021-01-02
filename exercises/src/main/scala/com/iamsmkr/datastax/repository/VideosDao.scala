package com.iamsmkr.datastax.repository

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.cql.SimpleStatement
import com.datastax.oss.driver.api.querybuilder.QueryBuilder
import com.iamsmkr.datastax.model.Videos
import com.iamsmkr.datastax.model.Videos.{TABLE_NAME, getVideoFromRow}
import com.iamsmkr.datastax.repository.VideosDao.Queries._

import scala.jdk.CollectionConverters._

class VideosDao private(session: CqlSession) {

  def getAll: List[Videos] = {
    val rs = session.execute(GET_ALL_QUERY)
    rs.map(getVideoFromRow).all().asScala.toList
  }
}

object VideosDao {
  def apply(session: CqlSession): VideosDao = new VideosDao(session)

  object Queries {
    final lazy val GET_ALL_QUERY: SimpleStatement =
      QueryBuilder.selectFrom(TABLE_NAME).all().build()
  }

}
