package pongdigo

import indigo.*
import scala.scalajs.js.annotation.JSExportTopLevel
import indigoextras.geometry.BoundingBox
import indigoextras.geometry.LineSegment
import indigoextras.geometry.Vertex

case class Scoreboard(leftScore: Int, rightScore: Int)
case class Paddle(position: Point, size: Size = Size(20, 60))

case class Ball(position: Point, radius: Int, velocity: Int, force: Vector2):
  def withForce(value: Vector2): Ball = this.copy(force = value)
  def withVelocity(value: Int): Ball = this.copy(velocity = value)

  def moveBy(amount: Point): Ball =
    this.copy(position = position + amount)

// Code thanks to https://github.com/davesmith00000/pong/blob/main/src/main/scala/pkg/Pong.scala
object Ball:
  def applyForce(b: Ball, f: Vector2): Ball =
    b.moveBy((Vector2(b.velocity) * f).toPoint).withForce(f)

  def moveBall(
      ball: Ball,
      walls: Batch[Rectangle],
      paddles: Batch[Rectangle]
  ): Ball =
    val current = ball.position
    val ballAdvance = applyForce(ball, ball.force)

    val line = LineSegment(
      Vertex.fromPoint(current),
      Vertex.fromPoint(
        ballAdvance.position + (Point(ball.radius) * ball.force.toPoint)
      )
    )

    val wallCollision =
      walls.exists(w => BoundingBox.fromRectangle(w).lineIntersects(line))
    val paddleCollision =
      paddles.exists(p => BoundingBox.fromRectangle(p).lineIntersects(line))

    val nextForce = Vector2(
      if paddleCollision then -ball.force.x else ball.force.x,
      if wallCollision then -ball.force.y else ball.force.y
    )

    if wallCollision || paddleCollision then applyForce(ball, nextForce)
    else ballAdvance

case class PongGame(
    player: Paddle,
    playerShape: Rectangle,
    cpu: Paddle,
    cpuShape: Rectangle,
    ball: Ball,
    ballShape: util.Circle,
    scoreboard: Scoreboard,
    viewPort: Size
):
  def walls: Batch[Rectangle] =
    Batch(
      Rectangle(0, 0, this.viewPort.width, 10),
      Rectangle(0, this.viewPort.height, this.viewPort.width, 10)
    )

  def paddles: Batch[Rectangle] = Batch(this.playerShape, this.cpuShape)

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
      Point(viewPort.width / 2, viewPort.height / 2),
      10,
      3,
      Vector2(1, 2)
    )
    val ballShape =
      util.Circle((Point(viewPort.width / 2, viewPort.height / 2)), ball.radius)

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

  val ballStart = Ball(Point(270, 195), 10, 3, Vector2(1, 1))

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
      val nextBall = Ball.moveBall(model.ball, model.walls, model.paddles)

      def giveVec =
        val choose = List(1, -1)
        Vector2(
          choose(context.dice.roll(2) - 1),
          choose(context.dice.roll(2) - 1)
        )

      if nextBall.position.x < -10 then
        Outcome(
          model.copy(ball = ballStart.withForce(giveVec))
        )
      else if nextBall.position.x > model.viewPort.width then
        Outcome(
          model.copy(ball = ballStart.withForce(giveVec))
        )
      else Outcome(model.copy(ball = nextBall))
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
      Shape.Circle(
        model.ball.position,
        model.ball.radius,
        Fill.Color(RGBA.Coral)
      )

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
