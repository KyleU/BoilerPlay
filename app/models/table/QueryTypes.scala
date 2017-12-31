package models.table

import java.sql.{Date, Time, Timestamp}
import java.time.{LocalDate, LocalDateTime, LocalTime}

import services.database.SlickQueryService.imports._

object QueryTypes {
  implicit val localDateColumnType = MappedColumnType.base[LocalDate, Date](l => Date.valueOf(l), d => d.toLocalDate)
  implicit val localTimeColumnType = MappedColumnType.base[LocalTime, Time](l => Time.valueOf(l), t => t.toLocalTime)
  implicit val localDateTimeColumnType = MappedColumnType.base[LocalDateTime, Timestamp](l => Timestamp.valueOf(l), t => t.toLocalDateTime)
}
