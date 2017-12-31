import sbt._

object Dependencies {
  object Play {
    private[this] val version = "2.6.10"
    val lib = "com.typesafe.play" %% "play" % version
    val filters = play.sbt.PlayImport.filters
    val ws = play.sbt.PlayImport.ws
    val guice = play.sbt.PlayImport.guice
    val cache = play.sbt.PlayImport.ehcache
    val json = "com.typesafe.play" %% "play-json" % "2.6.8"
    val test = "com.typesafe.play" %% "play-test" % version % "test"
  }

  object Akka {
    private[this] val version = "2.5.8"
    val actor = "com.typesafe.akka" %% "akka-actor" % version
    val remote = "com.typesafe.akka" %% "akka-remote" % version
    val logging = "com.typesafe.akka" %% "akka-slf4j" % version
    val cluster = "com.typesafe.akka" %% "akka-cluster" % version
    val clusterMetrics = "com.typesafe.akka" %% "akka-cluster-metrics" % version
    val clusterTools = "com.typesafe.akka" %% "akka-cluster-tools" % version
    val visualMailbox = "de.aktey.akka.visualmailbox" %% "collector" % "1.1.0"
    val testkit = "com.typesafe.akka" %% "akka-testkit" % version % "test"
  }

  object Authentication {
    private[this] val version = "5.0.3"
    val silhouette = "com.mohiva" %% "play-silhouette" % version
    val hasher = "com.mohiva" %% "play-silhouette-password-bcrypt" % version
    val persistence = "com.mohiva" %% "play-silhouette-persistence" % version
    val crypto = "com.mohiva" %% "play-silhouette-crypto-jca" % version
  }

  object Database {
    val version = "3.2.1"

    val postgres = "org.postgresql" % "postgresql" % "9.4.1212"
    val hikariCp = "com.zaxxer" % "HikariCP" % "2.7.4"
    val slickCore = "com.typesafe.slick" %% "slick" % version
    val slickariCp = "com.typesafe.slick" %% "slick-hikaricp" % version
  }

  object GraphQL {
    val sangria = "org.sangria-graphql" %% "sangria" % "1.3.3"
    val playJson = "org.sangria-graphql" %% "sangria-play-json" % "1.0.4"
    val circe = "org.sangria-graphql" %% "sangria-circe" % "1.1.1"
  }

  object Serialization {
    val circeVersion = "0.9.0-M3"
  }

  object WebJars {
    val fontAwesome = "org.webjars" % "font-awesome" % "4.7.0"
    val jquery = "org.webjars" % "jquery" % "2.2.4"
    val materialize = "org.webjars" % "materializecss" % "0.100.2"
    val moment = "org.webjars" % "momentjs" % "2.20.1"
    val mousetrap = "org.webjars" % "mousetrap" % "1.6.0"
  }

  object Metrics {
    val version = "0.1.0"
    val metrics = "io.prometheus" % "simpleclient" % version
    val metricsJvm = "io.prometheus" % "simpleclient_hotspot" % version
    val metricsHttp = "io.prometheus" % "simpleclient_httpserver" % version
    val metricsPush = "io.prometheus" % "simpleclient_pushgateway" % version
  }

  object Tracing {
    private[this] val version = "4.6.0"
    val brave = "io.zipkin.brave" % "brave-core" % version
    val http = "io.zipkin.reporter" % "zipkin-sender-okhttp3" % "1.1.0"
    val logging = "io.zipkin.brave" % "brave-context-slf4j" % version
  }

  object ScalaJS {
    val scalaJSVersion = "0.6.21"
    val jQueryVersion = "0.9.2"
    val jvmStubs = "org.scala-js" %% "scalajs-stubs" % scalaJSVersion % "provided"
  }

  object Utils {
    val scapegoatVersion = "1.3.3"
    val enumeratumVersion = "1.5.14"
    val booPickleVersion = "1.2.5"
    val utestVersion = "0.6.3"

    val scalatagsVersion = "0.6.7"

    val csv = "com.github.tototoshi" %% "scala-csv" % "1.3.5"
    val betterFiles = "com.github.pathikrit" %% "better-files" % "3.4.0"
    val commonsIo = "commons-io" % "commons-io" % "2.6"
    val scalaGuice = "net.codingwell" %% "scala-guice" % "4.1.1"
  }

  object Testing {
    val scalaTest = "org.scalatest" %% "scalatest" % "3.0.4" % "test"
  }
}
