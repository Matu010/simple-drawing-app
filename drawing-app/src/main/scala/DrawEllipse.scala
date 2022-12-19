import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.paint.Color
import scalafx.scene.shape.Ellipse

import scala.xml.Node

class DrawEllipse(centerX: Double, centerY: Double, outX: Double, outY: Double, gc: GraphicsContext, color: Color) extends Drawable {
  def draw(): Unit = {
    val ellipse = new Ellipse
    gc.setFill(color)
    ellipse.setCenterX(centerX)
    ellipse.setCenterY(centerY)

    ellipse.setRadiusX(Math.abs(outX - ellipse.getCenterX))
    ellipse.setRadiusY(Math.abs(outY - ellipse.getCenterY))

    if (ellipse.getCenterX > outX) ellipse.setCenterX(outX)
    if (ellipse.getCenterY > outY) ellipse.setCenterY(outY)
    gc.fillOval(ellipse.getCenterX, ellipse.getCenterY, ellipse.getRadiusX, ellipse.getRadiusY)
  }

  def toXml: Node = {
    <drawable type="Ellipse" x={centerX.toString} y={centerY.toString} outX={outX.toString} outY={outY.toString}>
      {Drawable.colorToXML(color)}
    </drawable>
  }
}
