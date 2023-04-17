import indigo.*
import scala.scalajs.js.annotation.JSExportTopLevel

case class Paddle(position: Point)
case class Ball(velocity: Double, direction: Point)
case class Scoreboard(leftScore: Int, rightScore: Int)

case class PongGame(player: Paddle, cpu: Paddle, ball: Ball, scoreboard: Scoreboard)

object Constants:
  val paddleWidth = 20
  val paddleHeight = 40
  val windowWidth: Int = 720
  val windowHeight: Int = 480

@JSExportTopLevel("IndigoGame")
object Pongdigo extends IndigoDemo[Unit, Unit, PongGame, Unit]:

  def updateViewModel(context: FrameContext[Unit], model: PongGame, viewModel: Unit): GlobalEvent => Outcome[Unit] = _ => Outcome(())

  def boot(flags: Map[String, String]): Outcome[BootResult[Unit]] = Outcome(BootResult.default)

  def eventFilters: EventFilters = EventFilters.Permissive

  def initialViewModel(startupData: Unit, model: PongGame): Outcome[Unit] = Outcome(())

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
      bootData: Unit,
      assetCollection: AssetCollection,
      dice: Dice
  ): Outcome[Startup[Unit]] =
    Outcome(Startup.Success(()))

  def initialModel(startupData: Unit): Outcome[PongGame] =
    val player = Paddle(Point(0,0))
    val cpu = Paddle(Point(50, 0))
    val ball = Ball(0.4, Point(0,3))
    val scoreboard = Scoreboard(0, 0)
    Outcome(PongGame(player, cpu, ball, scoreboard))

  def updateModel(
      context: FrameContext[Unit],
      model: PongGame
  ): GlobalEvent => Outcome[PongGame] =
    case KeyboardEvent.KeyDown(Key.UP_ARROW) => Outcome(model.copy(player = model.player.copy(position = model.player.position - (0.4))))
    case _ => Outcome(model)

  def present(
      context: FrameContext[Unit],
      model: PongGame,
      viewModel: Unit,
  ): Outcome[SceneUpdateFragment] =
    val player = Shape.Box(Rectangle(Constants.paddleWidth, Constants.paddleHeight), Fill.Color(RGBA.White))
    Outcome(
      SceneUpdateFragment.empty
    )

