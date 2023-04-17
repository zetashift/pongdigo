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
  .settings(
    showCursor := true,
    title := "Pongdigo",
    gameAssetsDirectory := "assets",
    windowStartWidth := 720, // Width of Electron window, used with `indigoRun`.
    windowStartHeight := 480, // Height of Electron window, used with `indigoRun`.
    libraryDependencies ++= Seq(
      "io.indigoengine" %%% "indigo" % "0.15.0-RC1",
    )
  )
