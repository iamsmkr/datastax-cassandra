package com.iamsmkr.datastax.repository

import com.datastax.oss.driver.api.core.{CqlIdentifier, CqlSession}
import com.datastax.oss.driver.api.core.cql.ResultSet
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder
import com.iamsmkr.datastax.CassConfig

class KillrVideoDao private(session: CqlSession) {
  private def createKeyspace(implicit config: CassConfig): ResultSet = {
    val ks = SchemaBuilder.createKeyspace(config.keyspace)
      .ifNotExists()
      .withSimpleStrategy(config.numberOfReplicas)

    session.execute(ks.build())
  }

  private def useKeyspace(implicit config: CassConfig): ResultSet = {
    session.execute("USE " + CqlIdentifier.fromCql(config.keyspace))
  }

}

object KillrVideoDao {
  def apply(session: CqlSession)(implicit cassConfig: CassConfig): KillrVideoDao = {
    val killrVideoDao = new KillrVideoDao(session)
    killrVideoDao.createKeyspace
    killrVideoDao.useKeyspace
    killrVideoDao
  }
}
