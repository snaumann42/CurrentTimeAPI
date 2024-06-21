package com.naumann

import com.naumann.DAO.UtcTime
import io.circe.generic.auto._
import io.circe.syntax._

import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.{ZoneId, ZonedDateTime}


object CurrentTimeAPI extends cask.MainRoutes{
  val dateFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

  @cask.get("/time")
  def currentTime(timeZone: Option[String] = None) = {

    // Get the current datetime and timezone
    val currentTime = ZonedDateTime.now(
      ZoneId.of("UTC")
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
        UtcTime(currentTime.format(dateFormatter),
          Some(adjustedTime.format(dateFormatter))).asJson.toString(), 200)
      case Right(None) => cask.Response(
        UtcTime(currentTime.format(dateFormatter)).asJson.toString(), 200)
      case Left(ex) => cask.Response(ex.toString, 422)
    }

  }

  initialize()
}

