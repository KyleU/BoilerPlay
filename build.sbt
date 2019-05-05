val projectId = "boilerplay"
val projectVersion = "2.0.0"

lazy val client = (project in file("client")).settings(
  version := projectVersion,
  libraryDependencies ++= Seq("com.kyleu" %%% "projectile-lib-scalajs" % projectileVersion)
).enablePlugins(ScalaJSPlugin, ScalaJSWeb)

lazy val doc = Project(id = "doc", base = file("./doc")).settings(
  version := projectVersion,
  paradoxTheme := Some(builtinParadoxTheme("generic")),
  sourceDirectory in Paradox := sourceDirectory.value / "main" / "paradox",
  git.remoteRepo := "git@github.com:KyleU/boilerplay.git"
).enablePlugins(ParadoxPlugin, ParadoxSitePlugin, SiteScaladocPlugin, GhpagesPlugin)

lazy val server = Project(id = projectId, base = file(".")).enablePlugins(ProjectilePlayProject).settings(
  projectileProjectId := projectId,
  projectileProjectName := "Boilerplay",
  projectileProjectPort := 9000,

  maintainer := "Boilerplay User <admin@boilerplay.com>",
  libraryDependencies ++= Seq(projectileLib("slick"), projectileLib("doobie")),

  play.sbt.routes.RoutesKeys.routesImport += "util.web.ModelBindables._",
  scalaJSProjects := Seq(client),

  JsEngineKeys.engineType := JsEngineKeys.EngineType.Node,
  pipelineStages in Assets := Seq(scalaJSPipeline),
  pipelineStages ++= Seq(gzip),

  scmInfo := Some(ScmInfo(url("https://github.com/KyleU/boilerplay"), "git@github.com:KyleU/boilerplay.git")),
  git.remoteRepo := scmInfo.value.get.connection,

  javaOptions in Universal ++= Seq("-J-Xmx2g", "-J-Xms256m", s"-Dproject=$projectId")
)
