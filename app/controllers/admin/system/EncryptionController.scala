package controllers.admin.system

import com.kyleu.projectile.controllers.AuthController
import com.kyleu.projectile.models.Application

import scala.concurrent.ExecutionContext.Implicits.global
import util.EncryptionUtils
import com.kyleu.projectile.web.util.ControllerUtils

import scala.concurrent.Future

@javax.inject.Singleton
class EncryptionController @javax.inject.Inject() (override val app: Application) extends AuthController("encryption") {
  def form = withSession("list", admin = true) { implicit request => implicit td =>
    Future.successful(Ok(views.html.admin.encryption(request.identity, app.cfg(Some(request.identity), admin = true))))
  }

  def post() = withSession("list", admin = true) { implicit request => implicit td =>
    val form = ControllerUtils.getForm(request.body)
    val action = form.get("action")
    val (unenc, enc) = action match {
      case Some("encrypt") =>
        val u = form.getOrElse("unenc", throw new IllegalStateException("Must provide [unenc] value when action is [encrypt]."))
        u -> EncryptionUtils.encrypt(u)
      case Some("decrypt") =>
        val e = form.getOrElse("enc", throw new IllegalStateException("Must provide [enc] value when action is [decrypt]."))
        EncryptionUtils.decrypt(e) -> e
      case _ => throw new IllegalStateException("Must provide [action] value of \"encrypt\" or \"decrypt\".")
    }

    Future.successful(Ok(views.html.admin.encryption(request.identity, app.cfg(Some(request.identity), admin = true), unenc, enc)))
  }
}
