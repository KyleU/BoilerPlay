package models.database.jdbc

import java.sql.{Connection, PreparedStatement, Types}
import java.util.UUID

import models.database.{Query, RawQuery, Statement}
import util.{Logging, NullUtils}

import scala.annotation.tailrec
import scala.util.control.NonFatal

trait Queryable extends Logging {
  @tailrec
  @SuppressWarnings(Array("AsInstanceOf"))
  private[this] def prepare(stmt: PreparedStatement, values: Seq[Any], index: Int = 1): Unit = {
    if (values.nonEmpty) {
      values.headOption.getOrElse(throw new IllegalArgumentException()) match {
        case v if NullUtils.isNull(v) => stmt.setNull(index, Types.NULL)

        case Some(x) => stmt.setObject(index, Conversions.convert(x.asInstanceOf[AnyRef]))
        case None => stmt.setNull(index, Types.NULL)

        case v => stmt.setObject(index, Conversions.convert(v.asInstanceOf[AnyRef]))
      }
      prepare(stmt, values.tail, index + 1)
    }
  }

  def apply[A](connection: Connection, query: RawQuery[A]): A = {
    log.debug(s"${query.sql} with ${query.values.mkString("(", ", ", ")")}")
    val stmt = connection.prepareStatement(query.sql)
    try {
      try {
        prepare(stmt, query.values)
      } catch {
        case NonFatal(x) => log.errorThenThrow(s"Unable to prepare query for [${query.sql}].", x)
      }
      val results = stmt.executeQuery()
      try {
        query.handle(results)
      } catch {
        case NonFatal(x) => log.errorThenThrow(s"Unable to handle query results for [${query.sql}].", x)
      } finally {
        results.close()
      }
    } finally {
      stmt.close()
    }
  }

  def executeUpdate(connection: Connection, statement: Statement): Int = {
    log.debug(s"${statement.sql} with ${statement.values.mkString("(", ", ", ")")}")
    val stmt = connection.prepareStatement(statement.sql)
    try {
      prepare(stmt, statement.values)
    } catch {
      case NonFatal(x) =>
        stmt.close()
        log.errorThenThrow(s"Unable to prepare statement [${statement.sql}].", x)
    }
    try {
      stmt.executeUpdate()
    } catch {
      case NonFatal(x) => log.errorThenThrow(s"Unable to execute statement [${statement.sql}].", x)
    } finally {
      stmt.close()
    }
  }

  def executeUnknown[A](connection: Connection, query: Query[A], resultId: Option[UUID]): Either[A, Int] = {
    log.debug(s"${query.sql} with ${query.values.mkString("(", ", ", ")")}")
    val stmt = connection.prepareStatement(query.sql)
    try {
      try {
        prepare(stmt, query.values)
      } catch {
        case NonFatal(x) => log.errorThenThrow(s"Unable to prepare raw query [${query.sql}].", x)
      }
      val isResultset = stmt.execute()
      if (isResultset) {
        val res = stmt.getResultSet
        try {
          Left(query.handle(res))
        } catch {
          case NonFatal(x) => log.errorThenThrow(s"Unable to handle query results for [${query.sql}].", x)
        } finally {
          res.close()
        }
      } else {
        Right(stmt.getUpdateCount)
      }
    } finally {
      stmt.close()
    }
  }
}
