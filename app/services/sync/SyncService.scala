package services.sync

import com.kyleu.projectile.util.DateUtils
import com.kyleu.projectile.models.auth.UserCredentials
import models.sync.SyncProgressRow
import com.kyleu.projectile.util.tracing.TraceData

import scala.concurrent.ExecutionContext
import scala.util.control.NonFatal

@javax.inject.Singleton
class SyncService @javax.inject.Inject() (val progressSvc: SyncProgressRowService)(implicit ec: ExecutionContext) {
  def set(creds: UserCredentials, key: String, status: String, msg: String)(implicit td: TraceData) = {
    val progress = SyncProgressRow(key = key, status = status, message = msg, lastTime = DateUtils.now).toDataFields
    progressSvc.getByPrimaryKey(creds, key).flatMap {
      case Some(_) => progressSvc.update(creds, key, progress.tail).map(x => Some(x._1))
      case None => progressSvc.create(creds, progress).recover {
        case NonFatal(_) => None
      }
    }
  }
}
