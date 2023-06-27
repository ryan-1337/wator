package base

import scala.util.Random

enum Direction {
  case NORTH, SOUTH, EAST, WEST, NORTH_EAST, NORTH_WEST, SOUTH_EAST, SOUTH_WEST
}

object Direction {

  def random: Direction = {
    val numberOfDirections = Direction.values.length
    Direction.values(Random.nextInt(numberOfDirections))
  }

}
