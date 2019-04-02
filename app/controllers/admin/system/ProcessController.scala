package controllers.admin.system

import java.util.UUID

import com.google.inject.Injector
import com.kyleu.projectile.controllers.AuthController
import com.kyleu.projectile.graphql.GraphQLService
import com.kyleu.projectile.models.Application

import scala.concurrent.ExecutionContext.Implicits.global
import services.process.ProcessService

import scala.concurrent.Future

@javax.inject.Singleton
class ProcessController @javax.inject.Inject() (
    override val app: Application, injector: Injector, graphQLService: GraphQLService
) extends AuthController("process") {
  def list = withSession("sandbox.list", admin = true) { implicit request => implicit td =>
    Future.successful(Ok(views.html.admin.process.procList(request.identity, app.cfg(Some(request.identity), admin = true), ProcessService.getActive)))
  }

  def run(cmd: Option[String]) = withSession("run", admin = true) { implicit request => implicit td =>
    val cmdSplit = cmd.getOrElse("").split(' ').filter(_.nonEmpty)
    if (cmdSplit.isEmpty) {
      throw new IllegalStateException("Please provide a command to run by passing the \"cmd\" query string parameter.")
    }
    val proc = ProcessService.start(request, cmdSplit, o => println(o), (e, d) => log.info(d + ": " + e))
    Future.successful(render {
      case Accepts.Html() => Ok(views.html.admin.process.procDetail(request.identity, app.cfg(Some(request.identity), admin = true), proc))
      //case Accepts.Json() => Ok(proc.asJson)
    })
  }

  def detail(id: UUID) = withSession("list", admin = true) { implicit request => implicit td =>
    Future.successful(Ok(views.html.admin.process.procDetail(request.identity, app.cfg(Some(request.identity), admin = true), ProcessService.getProc(id))))
  }
}
