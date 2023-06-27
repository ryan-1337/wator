package wator

import base.Direction

import java.awt.Color
import java.lang.Math.*
import scala.collection.mutable.ArrayBuffer
import scala.util.Random
import scalafx.scene.paint.Color

case class Tuna(x: Int, y: Int, r: Int, direction: Direction, color: Color) {

  val diameter: Int = r * 2

  def draw: Circle =
    new Circle {
      centerX = x
      centerY = y
      radius = r
      fill = color
    }

  def move(particles: Map[Coordinates, List[Tuna]], boardWidth: Int, boardHeight: Int): Tuna = {
    val (nextX: Int, nextY: Int)     = nextPosition(x, y, direction, boardWidth, boardHeight)
    val neighbours: Seq[Coordinates] = neighboursOf(x, y, diameter)
    val collision: Boolean           = neighbours.exists(particles.contains)
    val newDirection: Direction      = if (collision) Direction.random else direction

    copy(x = nextX, y = nextY, direction = newDirection)
  }

}
