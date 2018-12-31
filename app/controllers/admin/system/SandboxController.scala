package controllers.admin.system

import akka.util.Timeout
import com.google.inject.Injector
import controllers.BaseController
import models.Application
import scala.concurrent.ExecutionContext.Implicits.global
import models.sandbox.SandboxTask
import com.kyleu.projectile.util.JsonSerializers._

import scala.concurrent.Future
import scala.concurrent.duration._

@javax.inject.Singleton
class SandboxController @javax.inject.Inject() (override val app: Application, injector: Injector) extends BaseController("sandbox") {
  implicit val timeout: Timeout = Timeout(10.seconds)

  def list = withSession("sandbox.list", admin = true) { implicit request => implicit td =>
    Future.successful(Ok(views.html.admin.sandbox.sandboxList(request.identity)))
  }

  def run(key: String, arg: Option[String]) = withSession("sandbox." + key, admin = true) { implicit request => implicit td =>
    val sandbox = SandboxTask.withNameInsensitive(key)
    sandbox.run(SandboxTask.Config(app.tracing, injector, arg)).map { result =>
      render {
        case Accepts.Html() => Ok(views.html.admin.sandbox.sandboxRun(request.identity, result))
        case Accepts.Json() => Ok(result.asJson)
      }
    }
  }
}
