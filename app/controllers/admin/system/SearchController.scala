package controllers.admin.system

import java.util.UUID

import com.google.inject.Injector
import controllers.BaseController
import models.Application
import scala.concurrent.ExecutionContext.Implicits.global
import models.auth.UserCredentials
import play.twirl.api.Html
import com.kyleu.projectile.util.tracing.TraceData
import services.audit.{AuditRecordRowService, AuditRowService}
import services.ddl.FlywaySchemaHistoryRowService
import services.note.NoteRowService
import services.settings.SettingService
import services.sync.SyncProgressRowService
import services.task.ScheduledTaskRunRowService
import services.user.SystemUserService

import scala.concurrent.Future

@javax.inject.Singleton
class SearchController @javax.inject.Inject() (override val app: Application, injector: Injector) extends BaseController("search") {
  def search(q: String) = withSession("admin.search", admin = true) { implicit request => implicit td =>
    val creds = UserCredentials.fromRequest(request)
    val results = try {
      searchInt(creds, q, q.toInt)
    } catch {
      case _: NumberFormatException => try {
        searchUuid(creds, q, UUID.fromString(q))
      } catch {
        case _: IllegalArgumentException => searchString(creds, q)
      }
    }
    results.map { r =>
      Ok(views.html.admin.explore.searchResults(q, r, request.identity))
    }
  }

  private[this] def searchInt(creds: UserCredentials, q: String, id: Int)(implicit timing: TraceData) = {
    val intSearches = Seq[Future[Seq[Html]]]() ++
      /* Start int searches */
      /* End int searches */
      Nil

    Future.sequence(intSearches).map(_.flatten)
  }

  private[this] def searchUuid(creds: UserCredentials, q: String, id: UUID)(implicit timing: TraceData) = {
    val uuidSearches = Seq[Future[Seq[Html]]]() ++
      /* Start uuid searches */
      /* Projectile export section [boilerplay] */
      Seq(
        injector.getInstance(classOf[AuditRecordRowService]).getByPrimaryKey(creds, id).map(_.map(model => views.html.admin.audit.auditRecordRowSearchResult(model, s"Audit Record [${model.id}] matched [$q].")).toSeq),
        injector.getInstance(classOf[AuditRowService]).getByPrimaryKey(creds, id).map(_.map(model => views.html.admin.audit.auditRowSearchResult(model, s"Audit [${model.id}] matched [$q].")).toSeq),
        injector.getInstance(classOf[NoteRowService]).getByPrimaryKey(creds, id).map(_.map(model => views.html.admin.note.noteRowSearchResult(model, s"Note [${model.id}] matched [$q].")).toSeq),
        injector.getInstance(classOf[ScheduledTaskRunRowService]).getByPrimaryKey(creds, id).map(_.map(model => views.html.admin.task.scheduledTaskRunRowSearchResult(model, s"Scheduled Task Run [${model.id}] matched [$q].")).toSeq),
        injector.getInstance(classOf[SystemUserService]).getByPrimaryKey(creds, id).map(_.map(model => views.html.admin.user.systemUserSearchResult(model, s"System User [${model.id}] matched [$q].")).toSeq)
      ) ++
        /* End uuid searches */
        Nil

    Future.sequence(uuidSearches).map(_.flatten)
  }

  private[this] def searchString(creds: UserCredentials, q: String)(implicit timing: TraceData) = {
    val stringSearches = Seq[Future[Seq[Html]]]() ++
      /* Start string searches */
      /* Projectile export section [boilerplay] */
      Seq(
        injector.getInstance(classOf[AuditRecordRowService]).searchExact(creds, q = q, limit = Some(5)).map(_.map(model => views.html.admin.audit.auditRecordRowSearchResult(model, s"Audit Record [${model.id}] matched [$q]."))),
        injector.getInstance(classOf[AuditRowService]).searchExact(creds, q = q, limit = Some(5)).map(_.map(model => views.html.admin.audit.auditRowSearchResult(model, s"Audit [${model.id}] matched [$q]."))),
        injector.getInstance(classOf[FlywaySchemaHistoryRowService]).searchExact(creds, q = q, limit = Some(5)).map(_.map(model => views.html.admin.ddl.flywaySchemaHistoryRowSearchResult(model, s"Flyway Schema History [${model.installedRank}] matched [$q]."))),
        injector.getInstance(classOf[NoteRowService]).searchExact(creds, q = q, limit = Some(5)).map(_.map(model => views.html.admin.note.noteRowSearchResult(model, s"Note [${model.id}] matched [$q]."))),
        injector.getInstance(classOf[ScheduledTaskRunRowService]).searchExact(creds, q = q, limit = Some(5)).map(_.map(model => views.html.admin.task.scheduledTaskRunRowSearchResult(model, s"Scheduled Task Run [${model.id}] matched [$q]."))),
        injector.getInstance(classOf[SettingService]).searchExact(creds, q = q, limit = Some(5)).map(_.map(model => views.html.admin.settings.settingSearchResult(model, s"Setting [${model.k}] matched [$q]."))),
        injector.getInstance(classOf[SyncProgressRowService]).searchExact(creds, q = q, limit = Some(5)).map(_.map(model => views.html.admin.sync.syncProgressRowSearchResult(model, s"Sync Progress [${model.key}] matched [$q]."))),
        injector.getInstance(classOf[SystemUserService]).searchExact(creds, q = q, limit = Some(5)).map(_.map(model => views.html.admin.user.systemUserSearchResult(model, s"System User [${model.id}] matched [$q].")))
      ) ++
        /* End string searches */
        Nil

    Future.sequence(stringSearches).map(_.flatten)
  }
}
