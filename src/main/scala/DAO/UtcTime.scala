package com.naumann.DAO

import upickle.default.{ReadWriter => RW, macroRW}

case class UtcTime(currentTime: String, adjustedTime: Option[String] = None)

object UtcTime{
  implicit val rw: RW[UtcTime] = macroRW
}

