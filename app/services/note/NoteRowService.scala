/* Generated File */
package services.note

import com.kyleu.projectile.models.result.data.DataField
import com.kyleu.projectile.models.result.filter.Filter
import com.kyleu.projectile.models.result.orderBy.OrderBy
import com.kyleu.projectile.services.{Credentials, ModelServiceHelper}
import com.kyleu.projectile.services.database.JdbcDatabase
import com.kyleu.projectile.util.CsvUtils
import com.kyleu.projectile.util.tracing.{TraceData, TracingService}
import java.time.LocalDateTime
import java.util.UUID
import models.note.NoteRow
import models.queries.note.NoteRowQueries
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

@javax.inject.Singleton
class NoteRowService @javax.inject.Inject() (val db: JdbcDatabase, override val tracing: TracingService) extends ModelServiceHelper[NoteRow]("noteRow") {
  def getByPrimaryKey(creds: Credentials, id: UUID)(implicit trace: TraceData) = {
    traceF("get.by.primary.key")(td => db.queryF(NoteRowQueries.getByPrimaryKey(id))(td))
  }
  def getByPrimaryKeyRequired(creds: Credentials, id: UUID)(implicit trace: TraceData) = getByPrimaryKey(creds, id).map { opt =>
    opt.getOrElse(throw new IllegalStateException(s"Cannot load noteRow with id [$id]"))
  }
  def getByPrimaryKeySeq(creds: Credentials, idSeq: Seq[UUID])(implicit trace: TraceData) = if (idSeq.isEmpty) {
    Future.successful(Nil)
  } else {
    traceF("get.by.primary.key.seq")(td => db.queryF(NoteRowQueries.getByPrimaryKeySeq(idSeq))(td))
  }

  override def countAll(creds: Credentials, filters: Seq[Filter] = Nil)(implicit trace: TraceData) = {
    traceF("get.all.count")(td => db.queryF(NoteRowQueries.countAll(filters))(td))
  }
  override def getAll(creds: Credentials, filters: Seq[Filter] = Nil, orderBys: Seq[OrderBy] = Nil, limit: Option[Int] = None, offset: Option[Int] = None)(implicit trace: TraceData) = {
    traceF("get.all")(td => db.queryF(NoteRowQueries.getAll(filters, orderBys, limit, offset))(td))
  }

  // Search
  override def searchCount(creds: Credentials, q: Option[String], filters: Seq[Filter] = Nil)(implicit trace: TraceData) = {
    traceF("search.count")(td => db.queryF(NoteRowQueries.searchCount(q, filters))(td))
  }
  override def search(
    creds: Credentials, q: Option[String], filters: Seq[Filter] = Nil, orderBys: Seq[OrderBy] = Nil, limit: Option[Int] = None, offset: Option[Int] = None
  )(implicit trace: TraceData) = {
    traceF("search")(td => db.queryF(NoteRowQueries.search(q, filters, orderBys, limit, offset))(td))
  }

  def searchExact(
    creds: Credentials, q: String, orderBys: Seq[OrderBy] = Nil, limit: Option[Int] = None, offset: Option[Int] = None
  )(implicit trace: TraceData) = {
    traceF("search.exact")(td => db.queryF(NoteRowQueries.searchExact(q, orderBys, limit, offset))(td))
  }

  def countByAuthor(creds: Credentials, author: UUID)(implicit trace: TraceData) = traceF("count.by.author") { td =>
    db.queryF(NoteRowQueries.CountByAuthor(author))(td)
  }
  def getByAuthor(creds: Credentials, author: UUID, orderBys: Seq[OrderBy] = Nil, limit: Option[Int] = None, offset: Option[Int] = None)(implicit trace: TraceData) = traceF("get.by.author") { td =>
    db.queryF(NoteRowQueries.GetByAuthor(author, orderBys, limit, offset))(td)
  }
  def getByAuthorSeq(creds: Credentials, authorSeq: Seq[UUID])(implicit trace: TraceData) = if (authorSeq.isEmpty) {
    Future.successful(Nil)
  } else {
    traceF("get.by.author.seq") { td =>
      db.queryF(NoteRowQueries.GetByAuthorSeq(authorSeq))(td)
    }
  }

  def countByCreated(creds: Credentials, created: LocalDateTime)(implicit trace: TraceData) = traceF("count.by.created") { td =>
    db.queryF(NoteRowQueries.CountByCreated(created))(td)
  }
  def getByCreated(creds: Credentials, created: LocalDateTime, orderBys: Seq[OrderBy] = Nil, limit: Option[Int] = None, offset: Option[Int] = None)(implicit trace: TraceData) = traceF("get.by.created") { td =>
    db.queryF(NoteRowQueries.GetByCreated(created, orderBys, limit, offset))(td)
  }
  def getByCreatedSeq(creds: Credentials, createdSeq: Seq[LocalDateTime])(implicit trace: TraceData) = if (createdSeq.isEmpty) {
    Future.successful(Nil)
  } else {
    traceF("get.by.created.seq") { td =>
      db.queryF(NoteRowQueries.GetByCreatedSeq(createdSeq))(td)
    }
  }

  def countById(creds: Credentials, id: UUID)(implicit trace: TraceData) = traceF("count.by.id") { td =>
    db.queryF(NoteRowQueries.CountById(id))(td)
  }
  def getById(creds: Credentials, id: UUID, orderBys: Seq[OrderBy] = Nil, limit: Option[Int] = None, offset: Option[Int] = None)(implicit trace: TraceData) = traceF("get.by.id") { td =>
    db.queryF(NoteRowQueries.GetById(id, orderBys, limit, offset))(td)
  }
  def getByIdSeq(creds: Credentials, idSeq: Seq[UUID])(implicit trace: TraceData) = if (idSeq.isEmpty) {
    Future.successful(Nil)
  } else {
    traceF("get.by.id.seq") { td =>
      db.queryF(NoteRowQueries.GetByIdSeq(idSeq))(td)
    }
  }

  def countByRelPk(creds: Credentials, relPk: String)(implicit trace: TraceData) = traceF("count.by.relPk") { td =>
    db.queryF(NoteRowQueries.CountByRelPk(relPk))(td)
  }
  def getByRelPk(creds: Credentials, relPk: String, orderBys: Seq[OrderBy] = Nil, limit: Option[Int] = None, offset: Option[Int] = None)(implicit trace: TraceData) = traceF("get.by.relPk") { td =>
    db.queryF(NoteRowQueries.GetByRelPk(relPk, orderBys, limit, offset))(td)
  }
  def getByRelPkSeq(creds: Credentials, relPkSeq: Seq[String])(implicit trace: TraceData) = if (relPkSeq.isEmpty) {
    Future.successful(Nil)
  } else {
    traceF("get.by.relPk.seq") { td =>
      db.queryF(NoteRowQueries.GetByRelPkSeq(relPkSeq))(td)
    }
  }

  def countByRelType(creds: Credentials, relType: String)(implicit trace: TraceData) = traceF("count.by.relType") { td =>
    db.queryF(NoteRowQueries.CountByRelType(relType))(td)
  }
  def getByRelType(creds: Credentials, relType: String, orderBys: Seq[OrderBy] = Nil, limit: Option[Int] = None, offset: Option[Int] = None)(implicit trace: TraceData) = traceF("get.by.relType") { td =>
    db.queryF(NoteRowQueries.GetByRelType(relType, orderBys, limit, offset))(td)
  }
  def getByRelTypeSeq(creds: Credentials, relTypeSeq: Seq[String])(implicit trace: TraceData) = if (relTypeSeq.isEmpty) {
    Future.successful(Nil)
  } else {
    traceF("get.by.relType.seq") { td =>
      db.queryF(NoteRowQueries.GetByRelTypeSeq(relTypeSeq))(td)
    }
  }

  def countByText(creds: Credentials, text: String)(implicit trace: TraceData) = traceF("count.by.text") { td =>
    db.queryF(NoteRowQueries.CountByText(text))(td)
  }
  def getByText(creds: Credentials, text: String, orderBys: Seq[OrderBy] = Nil, limit: Option[Int] = None, offset: Option[Int] = None)(implicit trace: TraceData) = traceF("get.by.text") { td =>
    db.queryF(NoteRowQueries.GetByText(text, orderBys, limit, offset))(td)
  }
  def getByTextSeq(creds: Credentials, textSeq: Seq[String])(implicit trace: TraceData) = if (textSeq.isEmpty) {
    Future.successful(Nil)
  } else {
    traceF("get.by.text.seq") { td =>
      db.queryF(NoteRowQueries.GetByTextSeq(textSeq))(td)
    }
  }

  // Mutations
  def insert(creds: Credentials, model: NoteRow)(implicit trace: TraceData) = traceF("insert") { td =>
    db.executeF(NoteRowQueries.insert(model))(td).flatMap {
      case 1 => getByPrimaryKey(creds, model.id)(td)
      case _ => throw new IllegalStateException("Unable to find newly-inserted Note.")
    }
  }
  def insertBatch(creds: Credentials, models: Seq[NoteRow])(implicit trace: TraceData) = {
    traceF("insertBatch")(td => db.executeF(NoteRowQueries.insertBatch(models))(td))
  }
  def create(creds: Credentials, fields: Seq[DataField])(implicit trace: TraceData) = traceF("create") { td =>
    db.executeF(NoteRowQueries.create(fields))(td).flatMap { _ =>
      getByPrimaryKey(creds, UUID.fromString(fieldVal(fields, "id")))
    }
  }

  def remove(creds: Credentials, id: UUID)(implicit trace: TraceData) = {
    traceF("remove")(td => getByPrimaryKey(creds, id)(td).flatMap {
      case Some(current) =>
        db.executeF(NoteRowQueries.removeByPrimaryKey(id))(td).map(_ => current)
      case None => throw new IllegalStateException(s"Cannot find NoteRow matching [$id]")
    })
  }

  def update(creds: Credentials, id: UUID, fields: Seq[DataField])(implicit trace: TraceData) = {
    traceF("update")(td => getByPrimaryKey(creds, id)(td).flatMap {
      case Some(current) if fields.isEmpty => Future.successful(current -> s"No changes required for Note [$id]")
      case Some(_) => db.executeF(NoteRowQueries.update(id, fields))(td).flatMap { _ =>
        getByPrimaryKey(creds, id)(td).map {
          case Some(newModel) =>
            newModel -> s"Updated [${fields.size}] fields of Note [$id]"
          case None => throw new IllegalStateException(s"Cannot find NoteRow matching [$id]")
        }
      }
      case None => throw new IllegalStateException(s"Cannot find NoteRow matching [$id]")
    })
  }

  def csvFor(totalCount: Int, rows: Seq[NoteRow])(implicit trace: TraceData) = {
    traceB("export.csv")(td => CsvUtils.csvFor(Some(key), totalCount, rows, NoteRowQueries.fields)(td))
  }
}
