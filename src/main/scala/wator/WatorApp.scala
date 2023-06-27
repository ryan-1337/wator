package wator

import scala.collection.immutable
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.postfixOps
import scala.util.Random

object WatorApp extends JFXApp3 {

  override def start(): Unit = {
    val numberOfTunas: Int                = 15
    val tunasRadius: Int                   = 2 // rayon de chaque thon
    val screenBounds: Rectangle2D             = Screen.primary.visualBounds
    val (boardWidth, boardHeight): (Int, Int) = (screenBounds.width.intValue, screenBounds.height.intValue)
    val particles: List[Particle]             = generateParticles(numberOfTunas, tunasRadius, boardWidth, boardHeight)
    val state: ObjectProperty[List[Particle]] = ObjectProperty(particles)

    stage = new PrimaryStage {
      title = "Particles"
      width = boardWidth
      height = boardHeight
      scene = new Scene {
        fill = White
        content = drawParticles(state.value)
        state.onChange {
          content = drawParticles(state.value)
        }
      }
    }

    infiniteTimeline(state, boardWidth, boardHeight).play()
  }

  def generateParticles(n: Int, radius: Int, width: Int, height: Int): List[Particle] =
    List
      .fill(n) {
        val (x, y)    = (Random.nextInt(width), Random.nextInt(height))                             // position aléatoire
        val direction = Direction.random                                                            // direction aléatoire
        val color     = Color.rgb(Random.nextInt(256), Random.nextInt(256), Random.nextInt(256), 1) // couleur aléatoire

        Particle(x, y, radius, direction, color)
      }

  def drawParticles(particles: List[Particle]): List[Circle] = particles.map(_.draw)

  def infiniteTimeline(particles: ObjectProperty[List[Particle]], boardWidth: Int, boardHeight: Int): Timeline =
    new Timeline {
      keyFrames = List(KeyFrame(time = Duration(25), onFinished = _ => updateState(particles, boardWidth, boardHeight)))
      cycleCount = Indefinite
    }

  def updateState(state: ObjectProperty[List[Particle]], boardWidth: Int, boardHeight: Int): Unit = {
    val board = state.value.groupBy(p => (p.x, p.y))
    state.update(state.value.map(_.move(board, boardWidth, boardHeight)))
  }

}
