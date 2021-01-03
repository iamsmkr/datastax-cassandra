package com.iamsmkr.datastax

import com.datastax.oss.driver.api.core.CqlSession
import com.iamsmkr.datastax.model.Video
import com.iamsmkr.datastax.repository.{KillrVideoDao, VideosByTagDao, VideosDao}

import scala.util.Using

class KillrVideoApp(videosDao: VideosDao, videosByTagDao: VideosByTagDao) {
  def readWriteCass(implicit cassConfig: CassConfig): Unit = {
    videosDao.getAll.foreach(println)
    println

    val video = Video(title = "Cassandra Awesome")
    println(videosDao.insert(video))
    println

    println(videosDao.getById(video.videoId))
    println

    println(videosDao.deleteById(video.videoId))
    println

    videosByTagDao.getAll.foreach(println)
    println

    videosByTagDao.getByTag("datastax").foreach(println)
    println
  }
}

object KillrVideoApp {
  def main(args: Array[String]): Unit = {
    implicit val config: CassConfig = CassConfig()

    Using(CassConnector.getSession) { session: CqlSession =>
      KillrVideoDao(session)

      val killrVideoApp = new KillrVideoApp(
        VideosDao(session),
        VideosByTagDao(session)
      )

      killrVideoApp.readWriteCass
    }
  }

}
