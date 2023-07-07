package wator

import scalafx.application.JFXApp3

object Main extends JFXApp3 {
  override def start(): Unit = {
    val simulation = new WatorApp()
    simulation.start()
  }
}

