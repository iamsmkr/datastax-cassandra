package com.iamsmkr.datastax

import com.datastax.oss.driver.api.core.CqlSession
import com.iamsmkr.datastax.model.Video
import com.iamsmkr.datastax.repository.{KillrVideoDao, VideosByTagDao, VideosDao}

import scala.util.Using

object KillrVideoApp extends App {
  implicit val config: CassConfig = CassConfig()

  Using(CassConnector.getSession) { session: CqlSession =>
    KillrVideoDao(session).useKeyspace

    val videosDao = VideosDao(session)

    videosDao.getAll.foreach(println)
    println

    val video = Video(title = "Cassandra Awesome")
    println(videosDao.insert(video))
    println

    println(videosDao.getById(video.videoId))
    println

    println(videosDao.deleteById(video.videoId))
    println

    val videosByTagDao = VideosByTagDao(session)

    videosByTagDao.getAll.foreach(println)
    println

    videosByTagDao.getByTag("datastax").foreach(println)
    println
  }
}
