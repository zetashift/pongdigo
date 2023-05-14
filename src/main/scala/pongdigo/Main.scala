package pongdigo

import indigo.*
import scala.scalajs.js.annotation.JSExportTopLevel
import indigoextras.geometry.BoundingBox

case class Paddle(position: Point, size: Size = Size(20, 60))
case class Ball(velocity: Point, direction: Point, position: Point)
case class Scoreboard(leftScore: Int, rightScore: Int)

case class PongGame(
    player: Paddle,
    playerShape: Rectangle,
    cpu: Paddle,
    cpuShape: Rectangle,
    ball: Ball,
    ballShape: util.Circle,
    scoreboard: Scoreboard,
    viewPort: Size
)

object PongGame:

  def init(viewPort: Size): PongGame =
    val player = Paddle(Point(0, viewPort.height / 2))
    val playerShape =
      Rectangle(player.size.width, player.size.height)

    val cpu = Paddle(
      Point(viewPort.width - player.size.width, viewPort.height / 2)
    )
    val cpuShape =
      Rectangle(cpu.size.width, cpu.size.height)

    val ball = Ball(
      Point(2, 0),
      Point(3, 4),
      Point(viewPort.width / 2, viewPort.height / 2)
    )
    val ballShape =
      util.Circle((Point(viewPort.width / 2, viewPort.height / 2)), 10)

    val scoreboard = Scoreboard(0, 0)

    PongGame(
      player,
      playerShape,
      cpu,
      cpuShape,
      ball,
      ballShape,
      scoreboard,
      viewPort
    )

@JSExportTopLevel("IndigoGame")
object Pongdigo extends IndigoDemo[Size, Size, PongGame, Unit]:

  def updateViewModel(
      context: FrameContext[Size],
      model: PongGame,
      viewModel: Unit
  ): GlobalEvent => Outcome[Unit] = _ => Outcome(())

  def boot(flags: Map[String, String]): Outcome[BootResult[Size]] =
    val gameViewport =
      (flags.get("width"), flags.get("height")) match
        case (Some(w), Some(h)) => GameViewport(w.toInt, h.toInt)
        case _ =>
          val vp: Size = Size(640, 480)
          GameViewport(vp.width, vp.height)

    Outcome(
      BootResult(
        GameConfig.default
          .withMagnification(1)
          .withViewport(gameViewport),
        gameViewport.size
      )
    )

  def eventFilters: EventFilters = EventFilters.Permissive

  def initialModel(startupData: Size): Outcome[PongGame] =
    val initial = PongGame.init(startupData)
    Outcome(initial)

  def initialViewModel(startupData: Size, model: PongGame): Outcome[Unit] =
    Outcome(())

  val config: GameConfig =
    GameConfig.default

  val animations: Set[Animation] =
    Set()

  val assets: Set[AssetType] =
    Set()

  val fonts: Set[FontInfo] =
    Set()

  val shaders: Set[Shader] =
    Set()

  def setup(
      bootData: Size,
      assetCollection: AssetCollection,
      dice: Dice
  ): Outcome[Startup[Size]] =
    Outcome(Startup.Success(bootData))

  def updateModel(
      context: FrameContext[Size],
      model: PongGame
  ): GlobalEvent => Outcome[PongGame] =
    case KeyboardEvent.KeyDown(Key.UP_ARROW) =>
      Outcome(
        model.copy(player =
          model.player.copy(position = model.player.position.moveBy(0, -8))
        )
      )
    case KeyboardEvent.KeyDown(Key.DOWN_ARROW) =>
      Outcome(
        model.copy(player =
          model.player.copy(position = model.player.position.moveBy(0, 8))
        )
      )
    case FrameTick =>
      val ball = model.ball.copy(position = model.ball.position + Point(3, 0))
      val paddle = BoundingBox
        .fromRectangle(model.cpuShape)
        .sdf(Vector2(model.ballShape.x, model.ballShape.y))

      if paddle < model.ballShape.radius then
        val ball =
          model.ball.copy(position = model.ball.position + Point(-3, 0))

        Outcome(model.copy(ball = ball))
      else Outcome(model.copy(ball = ball))

    case _ => Outcome(model)

  def present(
      context: FrameContext[Size],
      model: PongGame,
      viewModel: Unit
  ): Outcome[SceneUpdateFragment] =
    val playerBox = Shape.Box(
      model.playerShape,
      Fill.Color(RGBA.White)
    )
    val cpuBox = Shape.Box(
      model.cpuShape,
      Fill.Color(RGBA.White)
    )
    val ballCircle =
      Shape.Circle(model.ball.position, 10, Fill.Color(RGBA.Coral))

    Outcome(
      SceneUpdateFragment(
        playerBox
          .moveTo(model.player.position)
          .moveBy(0, model.player.size.height / -2),
        ballCircle.moveTo(model.ball.position),
        cpuBox
          .moveTo(model.cpu.position)
          .moveBy(0, model.player.size.height / -2)
      )
    )
