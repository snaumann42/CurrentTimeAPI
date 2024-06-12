package com.naumann

import com.naumann.DAO.UtcTime
import io.undertow.Undertow
import upickle.default._
import utest._

import scala.util.matching.Regex


object ApiTests extends TestSuite{
  def withServer[T](example: cask.main.Main)(f: String => T): T = {
    val server = Undertow.builder
      .addHttpListener(8081, "localhost")
      .setHandler(example.defaultHandler)
      .build
    server.start()
    val res =
      try f("http://localhost:8081")
      finally server.stop()
    res
  }

  val tests = Tests {
    test("get /time format test") - withServer(CurrentTimeAPI) { host =>
      val dateTimeFormat: Regex = """^(\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}Z)$""".r

      // Without optional query parameter
      val success = requests.get(s"$host/time")
      val utcTime = read[UtcTime](success.text())
      assert(dateTimeFormat.matches(utcTime.currentTime))
      success.statusCode ==> 200

      // With optional query parameter
      val success2 = requests.get(s"$host/time?timeZone=-05:00")
      val utcTime2 = read[UtcTime](success2.text())
      assert(dateTimeFormat.matches(utcTime2.currentTime))
      success2.statusCode ==> 200
    }

    test("incorrect calls get error codes") - withServer(CurrentTimeAPI) { host =>
      //incorrect route
      requests.get(s"$host/nonExistingRoute", check = false).statusCode ==> 404

      //incorrect query parameter
      requests.get(s"$host/time?time=-05:00", check = false).statusCode ==> 400

      //invalid time zone
      requests.get(s"$host/time?timeZone=WrongTimeZone", check = false).statusCode ==> 422
    }
  }
}
