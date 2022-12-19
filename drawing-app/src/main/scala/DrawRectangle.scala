import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle

import scala.xml.Node

class DrawRectangle (centerX: Double, centerY: Double, outX: Double, outY: Double, gc: GraphicsContext, color: Color) extends Drawable {

  def draw(): Unit ={
    gc.setFill(color)
    val rect = new Rectangle
    rect.setX(centerX)
    rect.setY(centerY)

    rect.setWidth(Math.abs(outX - rect.getX))
    rect.setHeight(Math.abs(outY - rect.getY))

    if(rect.getX > outX) rect.setX(outX)
    if (rect.getY > outY) rect.setY(outY)

    gc.fillRect(rect.getX, rect.getY, rect.getWidth, rect.getHeight)
  }

  def toXml: Node = {
    <drawable type="Rectangle" x={centerX.toString} y={centerY.toString} outX={outX.toString} outY={outY.toString}>
      {Drawable.colorToXML(color)}
    </drawable>
  }

}
