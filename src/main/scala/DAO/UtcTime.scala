package com.naumann.DAO

import upickle.default.{ReadWriter => RW, macroRW}

case class UtcTime(currentTime: String)

object UtcTime{
  implicit val rm: RW[UtcTime] = macroRW
}
