package util

import java.util.TimeZone

import akka.actor.{ActorSystem, Props}
import com.codahale.metrics.SharedMetricRegistries
import com.mohiva.play.silhouette.api.Silhouette
import models.auth.AuthEnv
import play.api.Environment
import play.api.inject.ApplicationLifecycle
import util.FutureUtils.defaultContext
import play.api.libs.ws.WSClient
import services.database.{Database, MasterDdl}
import services.file.FileService
import services.settings.SettingsService
import services.supervisor.ActorSupervisor
import services.user.UserService
import util.cache.CacheService
import util.metrics.{Instrumented, TracingService}

import scala.concurrent.Future

object Application {
  var initialized = false
}

@javax.inject.Singleton
class Application @javax.inject.Inject() (
    val config: Configuration,
    val lifecycle: ApplicationLifecycle,
    val playEnv: Environment,
    val actorSystem: ActorSystem,
    val userService: UserService,
    val silhouette: Silhouette[AuthEnv],
    val ws: WSClient
) extends Logging {
  if (Application.initialized) {
    log.info("Skipping initialization after failure.")
  } else {
    start()
  }

  val supervisor = actorSystem.actorOf(Props(classOf[ActorSupervisor], this), "supervisor")

  lazy val tracing = TracingService(enabled = config.metrics.tracingEnabled, server = config.metrics.tracingServer, port = config.metrics.tracingPort)

  private[this] def start() = {
    log.info(s"${Config.projectName} is starting.")
    Application.initialized = true

    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    System.setProperty("user.timezone", "UTC")

    SharedMetricRegistries.remove("default")
    SharedMetricRegistries.add("default", Instrumented.metricRegistry)

    lifecycle.addStopHook(() => Future.successful(stop()))

    FileService.setRootDir(config.dataDir)

    Database.open(config.cnf)
    MasterDdl.init().map { _ =>
      SettingsService.load()
    }

    tracing.test()
  }

  private[this] def stop() = {
    Database.close()
    CacheService.close()
    tracing.close()
    SharedMetricRegistries.remove("default")
  }
}
