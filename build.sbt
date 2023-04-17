import org.scalajs.linker.interface.ModuleSplitStyle

lazy val root = project
  .in(file("."))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    name := "pongdigo",
    scalaVersion := "3.2.2",
    scalacOptions ++= Seq("-encoding", "utf-8", "-deprecation", "-feature"),
    scalaJSLinkerConfig ~= {
      _.withModuleKind(ModuleKind.ESModule)
        .withModuleSplitStyle(
          ModuleSplitStyle.SmallModulesFor(List("pongdigo"))
        )
    }
  )
  .settings( // Indigo specific settings
    showCursor := true,
    title := "My Game",
    gameAssetsDirectory := "assets",
    windowStartWidth := 720, // Width of Electron window, used with `indigoRun`.
    windowStartHeight := 480, // Height of Electron window, used with `indigoRun`.
    libraryDependencies ++= Seq(
      "io.indigoengine" %%% "indigo" % "0.14.0",
      "io.indigoengine" %%% "indigo-extras" % "0.14.0",
      "io.indigoengine" %%% "indigo-json-circe" % "0.14.0"
    )
  )
