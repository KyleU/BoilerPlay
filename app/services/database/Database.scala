package services.database

import java.net.InetAddress

import com.google.common.net.InetAddresses
import models.database.{DatabaseConfig, RawQuery, Statement}
import util.Logging
import util.metrics.Instrumented
import util.tracing.{TraceData, TracingService}
import zipkin.Endpoint

trait Database[Conn] extends Instrumented with Logging {
  protected[this] def key: String

  def transaction[A](f: (TraceData, Conn) => A)(implicit traceData: TraceData): A
  def execute(statement: Statement, conn: Option[Conn] = None)(implicit traceData: TraceData): Int
  def query[A](query: RawQuery[A], conn: Option[Conn] = None)(implicit traceData: TraceData): A

  def close(): Boolean

  private[this] lazy val endpoint = {
    val builder = Endpoint.builder().port(getConfig.port).serviceName("database." + key)
    InetAddress.getByName(getConfig.host) match {
      case x if x.getAddress.length == 4 => builder.ipv4(InetAddresses.coerceToInteger(x))
      case x => builder.ipv6(x.getAddress)
    }
    builder.build()
  }

  private[this] var tracingServiceOpt: Option[TracingService] = None
  protected def tracing = tracingServiceOpt.getOrElse {
    util.ise("Tracing service not configured. Did you forget to call \"open\"?")
  }

  private[this] var config: Option[DatabaseConfig] = None
  def getConfig = config.getOrElse(util.ise("Database not open."))

  protected[this] def start(cfg: DatabaseConfig, svc: TracingService) = {
    tracingServiceOpt = Some(svc)
    config = Some(cfg)
  }

  protected[this] def prependComment(obj: Object, sql: String) = s"/* ${obj.getClass.getSimpleName.replace("$", "")} */ $sql"

  protected[this] def trace[A](traceName: String)(f: TraceData => A)(implicit traceData: TraceData) = tracing.traceBlocking(key + "." + traceName) { td =>
    td.span.kind(brave.Span.Kind.CLIENT).remoteEndpoint(endpoint.toV2)
    f(td)
  }
}
