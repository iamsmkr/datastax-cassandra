package com.iamsmkr.datastax

import com.datastax.oss.driver.api.core.CqlSession
import com.iamsmkr.datastax.repository.{VideosByTagDao, VideosDao}

import scala.util.Using

object KillrVideoApp extends App with CassConnector {
  Using(getSession) { session: CqlSession =>
    val videosDao = VideosDao(session)

    videosDao.getAll.foreach(println)
    println

    val videosByTagDao = VideosByTagDao(session)

    videosByTagDao.getAll.foreach(println)
    println

    videosByTagDao.getByTag("datastax").foreach(println)
    println
  }
}
