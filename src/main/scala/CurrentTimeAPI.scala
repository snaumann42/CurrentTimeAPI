package com.naumann

import com.naumann.DAO.UtcTime
import upickle.default._

import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.{ZoneId, ZonedDateTime}


object CurrentTimeAPI extends cask.MainRoutes{
  val dateFormatter = DateTimeFormatter.ISO_INSTANT

  @cask.get("/time")
  def currentTime(timeZone: Option[String] = None) = {

    // Get the current datetime and timezone
    val currentTime = ZonedDateTime.now(
      ZoneId.systemDefault()
    ).truncatedTo(ChronoUnit.SECONDS)

    // Update datetime with optional timezone parameter
    val updatedDatetime: ZonedDateTime = timeZone match {
      case Some(timeZone) => currentTime.withZoneSameInstant(ZoneId.of(timeZone))
      case None => currentTime
    }

    // alternate return
    cask.Response(write(UtcTime(updatedDatetime.format(dateFormatter))), 200)
  }

  initialize()
}

