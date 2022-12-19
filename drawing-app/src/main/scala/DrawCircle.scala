import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.paint.Color

import math.{pow, sqrt}
import scala.xml.Node

class DrawCircle (centerX: Double,  centerY: Double, outX: Double, outY: Double, gc: GraphicsContext, color: Color) extends Drawable {
  val radius: Double = sqrt(pow(centerX - outX, 2) + pow(centerY - outY, 2))
  def draw(): Unit = {
    gc.setFill(color)
    gc.fillOval(centerX- radius, centerY - radius, radius * 2, radius*2)
  }


   def toXml: Node = {
    <drawable type="Circle" x={centerX.toString} y={centerY.toString} outX={outX.toString} outY={outY.toString}>
      {Drawable.colorToXML(color)}
    </drawable>
  }
}