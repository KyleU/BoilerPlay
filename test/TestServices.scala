/* Generated File */
import com.kyleu.projectile.services.database.ApplicationDatabase
import com.kyleu.projectile.util.tracing.TracingService

object TestServices {
  private[this] val trace = TracingService.noop
  private[this] val db = ApplicationDatabase

  val auditRowService = new services.audit.AuditRowService(db, trace)
  val auditRecordRowService = new services.audit.AuditRecordRowService(db, trace)
  val flywaySchemaHistoryRowService = new services.ddl.FlywaySchemaHistoryRowService(db, trace)
  val noteRowService = new services.note.NoteRowService(db, trace)
  val oauth2InfoRowService = new services.auth.Oauth2InfoRowService(db, trace)
  val passwordInfoRowService = new services.auth.PasswordInfoRowService(db, trace)
  val scheduledTaskRunRowService = new services.task.ScheduledTaskRunRowService(db, trace)
  val settingService = new services.settings.SettingService(db, trace)
  val syncProgressRowService = new services.sync.SyncProgressRowService(db, trace)
  val systemUserRowService = new services.auth.SystemUserRowService(db, trace)
}
