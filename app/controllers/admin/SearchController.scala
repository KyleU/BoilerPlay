package controllers.admin

import java.util.UUID

import controllers.BaseController
import util.Application
import util.FutureUtils.defaultContext

import scala.concurrent.Future

@javax.inject.Singleton
class SearchController @javax.inject.Inject() (override val app: Application) extends BaseController {
  def search(q: String) = withAdminSession("admin.search") { implicit request =>
    val resultF = try {
      searchInt(q, q.toInt)
    } catch {
      case _: NumberFormatException => try {
        searchUuid(q, UUID.fromString(q))
      } catch {
        case _: IllegalArgumentException => searchString(q)
      }
    }

    resultF.map { results =>
      Ok(views.html.admin.explore.searchResults(q, results, request.identity))
    }
  }

  private[this] def searchUuid(q: String, id: UUID) = app.userService.getById(id).map {
    case Some(u) => Seq(views.html.admin.user.userSearchResult(u, s"User [foo] matched id [$q]."))
    case None => Nil
  }

  private[this] def searchInt(q: String, id: Int) = {
    Future.successful(Nil)
  }

  private[this] def searchString(q: String) = app.userService.getAll().map { users =>
    users.map(u => views.html.admin.user.userSearchResult(u, s"User [foo] matched email [$q]."))
  }
}