package models.queries.auth

import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo
import models.database.DatabaseFieldType._
import models.database._
import models.queries.BaseQueries
import models.result.data.DataField
import models.result.filter.Filter
import models.user.{Role, SystemUser, UserPreferences}
import util.JsonSerializers

object SystemUserQueries extends BaseQueries[SystemUser]("systemUser", "system_users") {
  override val fields = Seq(
    DatabaseField("id"), DatabaseField("username"), DatabaseField("prefs"), DatabaseField("email"), DatabaseField("role"), DatabaseField("created")
  )
  override protected val pkColumns = Seq("id")
  override protected val searchColumns = Seq("id", "username", "email")

  val create = CreateFields

  def countAll(filters: Seq[Filter] = Nil) = onCountAll(filters)
  def getAll = GetAll

  val search = Search
  val searchCount = SearchCount
  val searchExact = SearchExact

  def update(id: UUID, fields: Seq[DataField]) = UpdateFields(Seq(id), fields)

  case class UpdateUser(u: SystemUser) extends Statement {
    override val name = s"$key.update.user"
    override val sql = updateSql(Seq("username", "prefs", "email", "role"))
    override val values = Seq(u.username, JsonSerializers.writePreferences(u.preferences), u.profile.providerKey, u.role.toString, u.id)
  }

  case class SetPreferences(userId: UUID, userPreferences: UserPreferences) extends Statement {
    override val name = s"$key.set.preferences"
    override val sql = updateSql(Seq("prefs"))
    override val values = Seq(JsonSerializers.writePreferences(userPreferences), userId)
  }

  case class SetRole(id: UUID, role: Role) extends Statement {
    override val name = s"$key.set.role"
    override val sql = s"update ${quote(tableName)} set ${quote("role")} = ? where ${quote("id")} = ?"
    override val values = Seq(role.toString, id)
  }

  case class FindUserByUsername(username: String) extends FlatSingleRowQuery[SystemUser] {
    override val name = s"$key.find.by.username"
    override val sql = getSql(Some(quote("username") + " = ?"))
    override val values = Seq(username)
    override def flatMap(row: Row) = Some(fromRow(row))
  }

  case class FindUserByProfile(loginInfo: LoginInfo) extends FlatSingleRowQuery[SystemUser] {
    override val name = s"$key.find.by.profile"
    override val sql = getSql(Some(quote("email") + " = ?"))
    override val values = Seq(loginInfo.providerKey)
    override def flatMap(row: Row) = Some(fromRow(row))
  }

  override protected def fromRow(row: Row) = {
    val id = UuidType(row, "id")
    val username = StringType(row, "username")
    val prefsString = StringType(row, "prefs")
    val preferences = JsonSerializers.readPreferences(prefsString)
    val profile = LoginInfo("credentials", StringType(row, "email"))
    val role = Role.withNameInsensitive(StringType(row, "role").trim)
    val created = TimestampType(row, "created")
    SystemUser(id, username, preferences, profile, role, created)
  }

  override protected def toDataSeq(u: SystemUser) = Seq(
    u.id.toString, u.username, JsonSerializers.writePreferences(u.preferences), u.profile.providerKey, u.role.toString, u.created
  )
}
