package models.table.note

import java.time.LocalDateTime
import java.util.UUID

import models.note.Note
import services.query.QueryService.imports._
import services.query.QueryTypes._

object NoteTable {
  val query = TableQuery[NoteTable]

  def getByPrimaryKey(id: UUID) = query.filter(_.id === id).result.headOption
  def getByPrimaryKeySeq(ids: Seq[UUID]) = query.filter(_.id.inSet(ids)).result.headOption

  def insert(n: Note) = query += n
  def delete(id: UUID) = query.filter(_.id === id).delete
}

class NoteTable(tag: Tag) extends Table[Note](tag, "note") {
  def id = column[UUID]("id", O.PrimaryKey)
  def relType = column[Option[String]]("rel_type")
  def relPk = column[Option[String]]("rel_pk")
  def text = column[String]("text")
  def author = column[UUID]("author")
  def created = column[LocalDateTime]("created")

  override val * = (id, relType, relPk, text, author, created) <> ((Note.apply _).tupled, Note.unapply)
}
