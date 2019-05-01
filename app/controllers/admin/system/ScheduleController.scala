package controllers.admin.system

import com.kyleu.projectile.controllers.AuthController
import com.kyleu.projectile.models.Application
import services.sync.SyncService
import services.task.ScheduledTaskService
import services.task.scheduled.ScheduledTasks

import scala.concurrent.{ExecutionContext, Future}

@javax.inject.Singleton
class ScheduleController @javax.inject.Inject() (
    override val app: Application,
    svc: ScheduledTaskService,
    syncService: SyncService,
    tasks: ScheduledTasks
)(implicit ec: ExecutionContext) extends AuthController("schedule") {
  def list = withSession("list", admin = true) { implicit request => implicit td =>
    syncService.progressSvc.getByKeySeq(request, tasks.all.map(_.key)).map { syncs =>
      Ok(views.html.admin.task.scheduleList(request.identity, app.cfg(Some(request.identity), admin = true), tasks.all, syncs))
    }
  }

  def run(key: String) = withSession("run", admin = true) { implicit request => implicit td =>
    val f = key match {
      case "all" => svc.runAll(creds = request)
      case _ => svc.runSingle(creds = request, task = tasks.byKey(key), args = Seq("force"))
    }
    f.map { results =>
      Ok(views.html.admin.task.scheduleRun(request.identity, app.cfg(Some(request.identity), admin = true), results))
    }
  }

  def reset = withSession("reset", admin = true) { implicit request => implicit td =>
    tasks.all.foreach(t => syncService.set(request, t.key, "Reset", "Manual reset."))
    Future.successful(Redirect(controllers.admin.system.routes.ScheduleController.list()))
  }
}
