package com.iamsmkr.datastax

import com.typesafe.config.ConfigFactory

case class CassConfig private(datacenter: String, nodes: Seq[String], port: Int, keyspace: String, numberOfReplicas: Int)

object CassConfig {

  import scala.jdk.CollectionConverters._

  private val config = ConfigFactory.load()

  def apply(): CassConfig = {
    CassConfig(
      config.getString("cassandra.datacenter"),
      config.getStringList("cassandra.host").asScala.toSeq,
      config.getInt("cassandra.port"),
      config.getString("cassandra.keyspace"),
      config.getInt("cassandra.numberOfReplicas")
    )
  }
}
