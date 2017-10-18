package util.web

import java.time.{LocalDate, LocalDateTime, LocalTime}

import play.api.mvc.{PathBindable, QueryStringBindable}
import util.DateUtils

object QueryStringUtils {
  implicit def localDateTimePathBindable(implicit stringBinder: PathBindable[String]) = new PathBindable[LocalDateTime] {
    override def bind(key: String, value: String) = stringBinder.bind(key, value) match {
      case Right(s) => Right(DateUtils.fromIsoString(s))
      case Left(x) => util.ise(x)
    }
    override def unbind(key: String, ldt: LocalDateTime) = DateUtils.toIsoString(ldt)
  }

  implicit def localDatePathBindable(implicit stringBinder: PathBindable[String]) = new PathBindable[LocalDate] {
    override def bind(key: String, value: String) = stringBinder.bind(key, value) match {
      case Right(s) => Right(LocalDate.parse(s))
      case Left(x) => throw new IllegalStateException(x)
    }
    override def unbind(key: String, ld: LocalDate) = ld.toString
  }

  implicit def localTimePathBindable(implicit stringBinder: PathBindable[String]) = new PathBindable[LocalTime] {
    override def bind(key: String, value: String) = stringBinder.bind(key, value) match {
      case Right(s) => Right(LocalTime.parse(s))
      case Left(x) => throw new IllegalStateException(x)
    }
    override def unbind(key: String, lt: LocalTime) = lt.toString
  }

  implicit def bytePathBindable(implicit intBinder: PathBindable[Int]) = new PathBindable[Byte] {
    override def bind(key: String, value: String) = intBinder.bind(key, value) match {
      case Right(x) => Right(x.toByte)
      case Left(x) => throw new IllegalStateException(x)
    }
    override def unbind(key: String, b: Byte) = b.toInt.toString
  }

  implicit def localDateTimeQueryStringBindable(implicit stringBinder: QueryStringBindable[String]) = new QueryStringBindable[LocalDateTime] {
    override def bind(key: String, params: Map[String, Seq[String]]) = stringBinder.bind(key, params).map {
      case Right(s) => Right(DateUtils.fromIsoString(s))
      case Left(x) => throw new IllegalStateException(x)
    }
    override def unbind(key: String, ldt: LocalDateTime): String = DateUtils.toIsoString(ldt)
  }

  implicit def localDateQueryStringBindable(implicit stringBinder: QueryStringBindable[String]) = new QueryStringBindable[LocalDate] {
    override def bind(key: String, params: Map[String, Seq[String]]) = stringBinder.bind(key, params).map {
      case Right(s) => Right(LocalDate.parse(s))
      case Left(x) => throw new IllegalStateException(x)
    }
    override def unbind(key: String, ld: LocalDate): String = ld.toString
  }

  implicit def localTimeQueryStringBindable(implicit stringBinder: QueryStringBindable[String]) = new QueryStringBindable[LocalTime] {
    override def bind(key: String, params: Map[String, Seq[String]]) = stringBinder.bind(key, params).map {
      case Right(s) => Right(LocalTime.parse(s))
      case Left(x) => throw new IllegalStateException(x)
    }
    override def unbind(key: String, lt: LocalTime): String = lt.toString
  }

  implicit def byteQueryStringBindable(implicit intBinder: QueryStringBindable[Int]) = new QueryStringBindable[Byte] {
    override def bind(key: String, params: Map[String, Seq[String]]) = intBinder.bind(key, params).map {
      case Right(s) => Right(s.toByte)
      case Left(x) => throw new IllegalStateException(x)
    }
    override def unbind(key: String, lt: Byte): String = lt.toString
  }
}
