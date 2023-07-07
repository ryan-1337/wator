package wator

import javafx.application.Application.launch
import scalafx.Includes.*
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.layout.GridPane
import scalafx.scene.shape.{Circle, Rectangle}
import scalafx.scene.paint.Color
import scalafx.animation.{AnimationTimer, KeyFrame, Timeline}
import scalafx.util.Duration

import scala.collection.mutable.ArrayBuffer
import scala.util.Random

class WatorApp extends JFXApp3 {

  val simWidth = 100-1
  val simHeight = 100-1
  val ocean: Array[Array[Animal]] = Array.ofDim[Animal](simWidth, simHeight)

  val tunas = new ArrayBuffer[Tuna]()
  val sharks = new ArrayBuffer[Shark]()




  def move(animal: Animal): Unit = {
    val possibleMoves = List((-1, 0), (1, 0), (0, -1), (0, 1))
      .map { case (dx, dy) => (animal.position._1 + dx, animal.position._2 + dy) }
      .filter { case (x, y) => x >= 0 && x < simWidth && y >= 0 && y < simHeight && ocean(x)(y) == null }

    if (possibleMoves.nonEmpty) {
      val newPosition = possibleMoves(Random.nextInt(possibleMoves.length))
      ocean(animal.position._1)(animal.position._2) = null
      ocean(newPosition._1)(newPosition._2) = animal
      animal.position = newPosition
    }
  }

  def simulate(): Unit = {
    val newTunas = new ArrayBuffer[Tuna]()
    val newSharks = new ArrayBuffer[Shark]()
    val deadSharks = new ArrayBuffer[Shark]()

    for (tuna <- tunas) {
      val previousPosition = tuna.position
      move(tuna)

      tuna.ReproductionCycle -= 1
      if (tuna.ReproductionCycle == 0) {
        tuna.ReproductionCycle = 10
        val babyTuna = new Tuna(previousPosition)
        newTunas += babyTuna
        ocean(previousPosition._1)(previousPosition._2) = babyTuna
      }
    }

    for (shark <- sharks) {
      val previousPosition = shark.position
      val possibleFood = List((-1, 0), (1, 0), (0, -1), (0, 1))
        .map { case (dx, dy) => (shark.position._1 + dx, shark.position._2 + dy) }
        .filter { case (x, y) => x >= 0 && x < simWidth && y >= 0 && y < simHeight && ocean(x)(y).isInstanceOf[Tuna] }

      if (possibleFood.nonEmpty) {
        val foodPosition = possibleFood(Random.nextInt(possibleFood.length))
        val eatenTuna = ocean(foodPosition._1)(foodPosition._2).asInstanceOf[Tuna]
        tunas -= eatenTuna
        shark.energy += 1
        ocean(foodPosition._1)(foodPosition._2) = shark
        ocean(shark.position._1)(shark.position._2) = null
        shark.position = foodPosition
      } else {
        move(shark)
      }

      shark.ReproductionCycle -= 1
      if (shark.ReproductionCycle == 0) {
        shark.ReproductionCycle = 8
        val babyShark = new Shark(previousPosition)
        newSharks += babyShark
        ocean(previousPosition._1)(previousPosition._2) = babyShark
      }

      shark.energy -= 1
      if (shark.energy == 0) {
        ocean(shark.position._1)(shark.position._2) = null
        deadSharks += shark
      }
    }

    tunas ++= newTunas
    sharks ++= newSharks
    sharks --= deadSharks
  }

  def initialize(): Unit = {
    for (_ <- 1 to 5) {
      val position = (Random.nextInt(simWidth), Random.nextInt(simHeight / 2))
      if (ocean(position._1)(position._2) == null) {
        val tuna = new Tuna(position)
        tunas += tuna
        ocean(position._1)(position._2) = tuna
      }
    }
    for (_ <- 1 to 25) {
      val position = (Random.nextInt(simWidth), Random.nextInt(simHeight / 2) + simHeight / 2)
      if (ocean(position._1)(position._2) == null) {
        val shark = new Shark(position)
        sharks += shark
        ocean(position._1)(position._2) = shark
      }
    }
  }

  val gridPane = new GridPane

  val timeline = new Timeline {
    cycleCount = Timeline.Indefinite
    keyFrames = KeyFrame(Duration(10), onFinished = _ => initialize())
  }

  timeline.play()

  override def start(): Unit = {
    stage = new JFXApp3.PrimaryStage {
      title.value = "Wator App"
      scene = new Scene {
        content = gridPane
      }
    }

    def drawOcean(): Unit = {
      gridPane.children.clear()

      for (i <- 0 until simWidth; j <- 0 until simHeight) {
        val circle = new Circle {
          radius = 7
        }

        ocean(i)(j) match {
          case null => circle.fill = Color.Black
          case _: Tuna => circle.fill = Color.Blue
          case _: Shark => circle.fill = Color.Red
        }

        gridPane.add(circle, i,j)
      }
    }

    AnimationTimer(t => {
      simulate()
      drawOcean()
    }).start()

    initialize()
    drawOcean()
  }
}