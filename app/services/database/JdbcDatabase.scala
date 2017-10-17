package services.database

import java.sql.Connection
import java.util.Properties

import com.codahale.metrics.MetricRegistry
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import models.database.jdbc.Queryable
import models.database.{DatabaseConfig, RawQuery, Statement}
import util.metrics.{Checked, Instrumented}
import util.tracing.{TraceData, TracingService}

import scala.concurrent.Future

abstract class JdbcDatabase(override val key: String, configPrefix: String) extends Database[Connection] with Queryable {
  private[this] def time[A](klass: java.lang.Class[_])(f: => A) = {
    val ctx = Instrumented.metricRegistry.timer(MetricRegistry.name(klass)).time()
    try { f } finally { ctx.stop }
  }

  private[this] var ds: Option[HikariDataSource] = None
  private[this] def source = ds.getOrElse(throw new IllegalStateException("Database not initialized."))

  private[this] def perform[T](conn: Option[Connection])(block: Connection => T): T = {
    val c = conn.map((_, false)).getOrElse((source.getConnection, true))
    try {
      block(c._1)
    } finally {
      if (c._2) c._1.close()
    }
  }

  def open(cfg: play.api.Configuration, svc: TracingService) = {
    ds.foreach(_ => throw new IllegalStateException("Database already initialized."))

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

  override def transaction[A](f: (TraceData, Connection) => Future[A], conn: Option[Connection] = None)(implicit traceData: TraceData) = {
    perform(conn) {
      f(traceData, _)
    }
  }

  override def execute(statement: Statement, conn: Option[Connection])(implicit traceData: TraceData) = {
    perform(conn) { c =>
      time(statement.getClass) { executeUpdate(c, statement) }
    }
  }

  override def query[A](query: RawQuery[A], conn: Option[Connection])(implicit traceData: TraceData) = {
    perform(conn) { c =>
      time(query.getClass)(apply(c, query))
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
