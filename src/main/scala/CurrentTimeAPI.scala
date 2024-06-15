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
    val updatedDatetime: Either[Throwable, Option[ZonedDateTime]] = timeZone match {
      case Some(timeZone) =>
        try{
          Right(Some(currentTime.withZoneSameInstant(ZoneId.of(timeZone))))
        }catch {
          case ex: Throwable => Left(ex)
        }
      case _ => Right(None)
    }

    updatedDatetime match {
      case Right(Some(adjustedTime)) => cask.Response(
        write[UtcTime](UtcTime(currentTime.format(dateFormatter),
          Some(adjustedTime.toString))), 200)
      case Right(None) => cask.Response(
        write[UtcTime](UtcTime(currentTime.format(dateFormatter))), 200)
      case Left(ex) => cask.Response(ex.toString, 422)
    }

  }

  initialize()
}

