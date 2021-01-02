package com.iamsmkr.datastax

import com.datastax.oss.driver.api.core.CqlSession
import com.iamsmkr.datastax.repository.VideosDao

import scala.util.Using

object KillrVideoApp extends App with CassConnector {
  Using(getSession) { session: CqlSession =>
    val videosDao = VideosDao(session)
    videosDao.getAll.foreach(println)
  }
}
