package models.settings

import models.result.data.{DataField, DataFieldModel, DataSummary}
import util.JsonSerializers._

object Setting {
  implicit val jsonEncoder: Encoder[Setting] = deriveEncoder
  implicit val jsonDecoder: Decoder[Setting] = deriveDecoder

  def empty(k: SettingKey = SettingKey.DefaultNewUserRole, v: String = "") = Setting(k, v)
}

final case class Setting(k: SettingKey, v: String) extends DataFieldModel {
  override def toDataFields = Seq(
    DataField("k", Some(k.toString)),
    DataField("v", Some(v))
  )
  lazy val isDefault = v == k.default
  override def toString = s"$k=$v"
  lazy val asBool = v == "true"

  def toSummary = DataSummary(model = "flywaySchemaHistory", pk = Seq(k.toString), title = s"$k / $v")
}
