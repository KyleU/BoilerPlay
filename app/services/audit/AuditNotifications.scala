package services.audit

import com.kyleu.projectile.services.database.JdbcDatabase
import models.audit.{Audit, AuditRecordRow}
import scala.concurrent.ExecutionContext.Implicits.global
import models.queries.audit.{AuditQueries, AuditRecordRowQueries}
import com.kyleu.projectile.util.Logging
import com.kyleu.projectile.util.tracing.TraceData

import scala.concurrent.{ExecutionContext, Future}

object AuditNotifications extends Logging {
  private[this] def acc[T, U](seq: Seq[T], f: T => Future[U])(ec: ExecutionContext) = seq.foldLeft(Future.successful(Seq.empty[U])) { (ret, i) =>
    ret.flatMap(s => f(i).map(_ +: s)(ec))(ec)
  }

  def persist(db: JdbcDatabase, a: Audit, records: Seq[AuditRecordRow])(implicit trace: TraceData) = {
    log.debug(s"Persisting audit [${a.id}]...")
    val ret = db.executeF(AuditQueries.insert(a)).map { _ =>
      acc(records, (r: AuditRecordRow) => db.executeF(AuditRecordRowQueries.insert(r)).map { _ =>
        log.debug(s"Persisted audit record [${r.id}] for audit [${a.id}].")
      })(global).map { _ =>
        log.debug(s"Persisted audit [${a.id}] with [${records.size}] records.")
      }
    }
    ret.failed.foreach(x => log.warn(s"Unable to persist audit [${a.id}].", x))
    ret
  }
}
