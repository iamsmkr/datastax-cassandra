package com.iamsmkr.datastax

import java.net.InetSocketAddress

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.CqlSessionBuilder
import scala.jdk.CollectionConverters._

import com.typesafe.config.ConfigFactory

case class CassConfig private(datacenter: String, nodes: Seq[String], port: Int, keyspace: String)

object CassConfig {

  import scala.jdk.CollectionConverters._

  private val config = ConfigFactory.load()

  def apply(): CassConfig = {
    CassConfig(
      config.getString("cassandra.datacenter"),
      config.getStringList("cassandra.host").asScala.toSeq,
      config.getInt("cassandra.port"),
      config.getString("cassandra.keyspace")
    )
  }
}

trait CassConnector {
  private val config: CassConfig = CassConfig()
  private var session: Option[CqlSession] = None

  private def connect(): Unit = {
    val builder: CqlSessionBuilder = CqlSession.builder()
    builder.addContactPoints(config.nodes.map(n => new InetSocketAddress(n, config.port)).asJavaCollection)
      .withLocalDatacenter(config.datacenter)
      .withKeyspace(config.keyspace)

    session = Some(builder.build())
  }

  def getSession: CqlSession = {
    if (session.isDefined) this.session.get
    else {
      connect()
      this.session.get
    }
  }

  def close(): Unit = this.session.get.close()
}
