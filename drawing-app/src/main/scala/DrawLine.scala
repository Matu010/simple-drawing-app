import scalafx.scene.canvas.{Canvas, GraphicsContext}
import scalafx.scene.paint.Color

import scala.xml.Node

class DrawLine (var startX: Double, var startY: Double, var endX: Double, var endY: Double, gc: GraphicsContext, color: Color) extends Drawable {
  def draw(): Unit = {
    gc.setStroke(color)
    gc.strokeLine(startX, startY, endX, endY)
  }

  def toXml: Node = {
    <drawable type="Line" x={startX.toString} y={startY.toString} outX={endX.toString} outY={endY.toString}>
      {Drawable.colorToXML(color)}
    </drawable>
  }

}
