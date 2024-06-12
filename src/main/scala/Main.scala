import upickle.default._

import java.time.format.DateTimeFormatter
import java.time.{ZoneId, ZonedDateTime}


object Main {
  object MinimalApplication extends cask.MainRoutes{
    
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-ddTHH:mm:ssZ")

    // adjustedTime (optional): If a timezone parameter is provided,
    //    the API should return the current time adjusted to the
    //    specified timezone in the requested format.
    @cask.get("/time")
    def currentTime(timeZone: Option[String] = None) = {

      // Get the current datetime and timezone
      val currentTime = ZonedDateTime.now(
        ZoneId.systemDefault()
      )

      // Update datetime with optional timezone parameter
      val updatedDatetime = timeZone match {
        case Some(timeZone) => currentTime.withZoneSameInstant(ZoneId.of(timeZone))
        case None => currentTime
      }

      // return datetime with the proper status
      cask.Response(write(updatedDatetime.format(dateFormatter)), 200)
    }

    initialize()
  }
}
