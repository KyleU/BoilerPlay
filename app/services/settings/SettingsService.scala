package services.settings

import com.kyleu.projectile.models.auth.UserCredentials
import com.kyleu.projectile.models.result.data.DataField
import com.kyleu.projectile.services.database.JdbcDatabase
import models.settings.{Setting, SettingKeyType}
import com.kyleu.projectile.util.Logging
import com.kyleu.projectile.util.tracing.TraceData
import models.queries.settings.SettingQueries
import com.kyleu.projectile.util.tracing.OpenTracingService

import scala.concurrent.{ExecutionContext, Future}

@javax.inject.Singleton
class SettingsService @javax.inject.Inject() (
    tracing: OpenTracingService, db: JdbcDatabase, svc: SettingService
)(implicit ec: ExecutionContext) extends Logging {
  private[this] var settings = Seq.empty[Setting]
  private[this] var settingsMap = Map.empty[SettingKeyType, String]

  def apply(key: SettingKeyType) = settingsMap.getOrElse(key, key.default)
  def asBool(key: SettingKeyType) = apply(key) == "true"
  def getOrSet(key: SettingKeyType, s: => String)(implicit trace: TraceData) = tracing.traceBlocking("get.or.set") { td =>
    settingsMap.get(key) match {
      case Some(v) => Future.successful(v)
      case None => set(key, s)(td)
    }
  }

  def load()(implicit trace: TraceData) = tracing.trace("settings.service.load") { _ =>
    db.queryF(SettingQueries.getAll()).map { set =>
      settingsMap = set.map(s => s.k -> s.v).toMap
      settings = SettingKeyType.values.map(k => Setting(k, settingsMap.getOrElse(k, k.default)))
      log.info(s"Loaded [${set.size}] system settings.")
    }
  }

  def isOverride(key: SettingKeyType) = settingsMap.isDefinedAt(key)

  def getAll = settings
  def getOverrides = settings.filter(s => isOverride(s.k))

  def set(key: SettingKeyType, value: String)(implicit trace: TraceData) = tracing.trace("settings.service.set") { _ =>
    val s = Setting(key, value)
    val creds = UserCredentials.system
    val ret = if (s.isDefault) {
      settingsMap = settingsMap - key
      svc.remove(creds, key)
    } else {
      svc.getByPrimaryKey(creds, key).flatMap {
        case Some(_) => svc.update(creds, key, Seq(DataField("v", Some(s.v))))
        case None => svc.insert(creds, s)
      }.map { _ =>
        settingsMap = settingsMap + (key -> value)
      }
    }
    ret.map { _ =>
      settings = SettingKeyType.values.map(k => Setting(k, settingsMap.getOrElse(k, k.default)))
      value
    }
  }

  def allowRegistration = asBool(SettingKeyType.AllowRegistration)
}
