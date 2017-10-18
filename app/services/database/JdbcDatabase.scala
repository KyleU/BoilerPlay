package services.database

import java.sql.Connection
import java.util.Properties

import com.codahale.metrics.MetricRegistry
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import models.database.jdbc.Queryable
import models.database.{DatabaseConfig, RawQuery, Statement}
import util.metrics.{Checked, Instrumented}
import util.tracing.{TraceData, TracingService}

import scala.util.control.NonFatal

abstract class JdbcDatabase(override val key: String, configPrefix: String) extends Database[Connection] with Queryable {
  private[this] def time[A](klass: java.lang.Class[_])(f: => A) = {
    val ctx = Instrumented.metricRegistry.timer(MetricRegistry.name(klass)).time()
    try { f } finally { ctx.stop }
  }

  private[this] var ds: Option[HikariDataSource] = None
  private[this] def source = ds.getOrElse(util.ise("Database not initialized."))

  private[this] def perform[T](conn: Option[Connection])(block: Connection => T): T = {
    val c = conn.map((_, false)).getOrElse((source.getConnection, true))
    try {
      block(c._1)
    } finally {
      if (c._2) c._1.close()
    }
  }

  def open(cfg: play.api.Configuration, svc: TracingService) = {
    ds.foreach(_ => util.ise("Database already initialized."))

    val config = DatabaseConfig.fromConfig(cfg, configPrefix)
    val properties = new Properties

    val poolConfig = new HikariConfig(properties) {
      setPoolName(util.Config.projectId + "." + key)
      setJdbcUrl(config.url)
      setUsername(config.username)
      setPassword(config.password.getOrElse(""))
      setConnectionTimeout(10000)
      setMinimumIdle(1)
      setMaximumPoolSize(32)

      setHealthCheckRegistry(Checked.healthCheckRegistry)
      setMetricRegistry(Instrumented.metricRegistry)
    }

    val poolDataSource = new HikariDataSource(poolConfig)

    ds = Some(poolDataSource)

    start(config, svc)
  }

  override def transaction[A](f: (TraceData, Connection) => A)(implicit traceData: TraceData) = {
    val connection = source.getConnection
    connection.setAutoCommit(false)
    try {
      val result = f(traceData, connection)
      connection.commit()
      result
    } catch {
      case NonFatal(x) =>
        connection.rollback()
        throw x
    } finally {
      connection.close()
    }
  }

  override def execute(statement: Statement, conn: Option[Connection])(implicit traceData: TraceData) = {
    val connection = conn.getOrElse(source.getConnection)
    try {
      time(statement.getClass) { executeUpdate(connection, statement) }
    } finally {
      if (conn.isEmpty) { connection.close() }
    }
  }

  override def query[A](query: RawQuery[A], conn: Option[Connection])(implicit traceData: TraceData) = {
    val connection = conn.getOrElse(source.getConnection)
    try {
      time(query.getClass)(apply(connection, query))
    } finally {
      if (conn.isEmpty) { connection.close() }
    }
  }

  def withConnection[T](f: (Connection) => T) = {
    perform(None) { f(_) }
  }

  override def close() = {
    ds.foreach(_.close())
    true
  }
}
