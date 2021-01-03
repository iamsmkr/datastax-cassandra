package com.iamsmkr.datastax

import java.net.InetSocketAddress

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.CqlSessionBuilder
import scala.jdk.CollectionConverters._

object CassConnector {
  private var session: Option[CqlSession] = None

  private def connect(config: CassConfig): Unit = {
    val builder: CqlSessionBuilder = CqlSession.builder()
    builder.addContactPoints(config.nodes.map(n => new InetSocketAddress(n, config.port)).asJavaCollection)
      .withLocalDatacenter(config.datacenter)

    session = Some(builder.build())
  }

  def getSession(implicit cassConfig: CassConfig): CqlSession = {
    if (session.isDefined) this.session.get
    else {
      connect(cassConfig)
      this.session.get
    }
  }

  def close(): Unit = this.session.get.close()
}
