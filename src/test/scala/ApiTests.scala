package com.naumann

import com.naumann.DAO.UtcTime
import io.undertow.Undertow
import upickle.default._
import utest._

import java.time.temporal.ChronoUnit
import java.time.{ZoneId, ZonedDateTime}
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

      // With optional query parameter (ZoneOffset support)
      val success2 = requests.get(s"$host/time?timeZone=-03:00")
      val utcTime2 = read[UtcTime](success2.text())
      assert(dateTimeFormat.matches(utcTime2.currentTime))
      success2.statusCode ==> 200

      // With optional query parameter (offset-style IDs)
      val success3 = requests.get(s"""$host/time?timeZone=GMT%2B02""")
      val utcTime3 = read[UtcTime](success3.text())
      assert(dateTimeFormat.matches(utcTime3.currentTime))
      success3.statusCode ==> 200

      // With optional query parameter (Region-based IDs)
      val success4 = requests.get(s"$host/time?timeZone=Australia/Sydney")
      val utcTime4 = read[UtcTime](success4.text())
      assert(dateTimeFormat.matches(utcTime4.currentTime))
      success4.statusCode ==> 200
    }

    test("get /time is current") - withServer(CurrentTimeAPI) { host =>
      // Verify time is within seconds of each other
      val success = requests.get(s"$host/time")
      val returnedTime = ZonedDateTime.parse(read[UtcTime](success.text()).currentTime)
      val currentTime = ZonedDateTime.now(
                            ZoneId.systemDefault()
                          ).truncatedTo(ChronoUnit.SECONDS)

      // returned time should be earlier then currentTime plus 10 seconds
      assert(currentTime.plusSeconds(10).isAfter(returnedTime))

      // returned time should be later then currentTime minus 10 seconds
      assert(currentTime.minusSeconds(10).isBefore(returnedTime))
      success.statusCode ==> 200
    }

    test("incorrect calls get error codes") - withServer(CurrentTimeAPI) { host =>
      //incorrect route
      requests.get(s"$host/nonExistingRoute", check = false).statusCode ==> 404

      //incorrect query parameter
      requests.get(s"$host/time?time=-05:00", check = false).statusCode ==> 400

      //invalid time zone
      requests.get(s"$host/time?timeZone=WrongTimeZone", check = false).statusCode ==> 422
    }

    test("currentTime and adjusted time are not equal (when ignoring timezone)") - withServer(CurrentTimeAPI) { host =>
      // With optional query parameter (Region-based IDs)
      val success = requests.get(s"$host/time?timeZone=Australia/Sydney")
      val utcTime = read[UtcTime](success.text())
      success.statusCode ==> 200
      assert(utcTime.currentTime.slice(0, 19) != utcTime.adjustedTime.getOrElse(utcTime.currentTime.slice(0, 19)).slice(0, 19))
    }
  }
}
