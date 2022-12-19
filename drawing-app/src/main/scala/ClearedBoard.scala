import scalafx.scene.canvas.GraphicsContext

import scala.xml.Node


class ClearedBoard(currentDrawings: Seq[Drawable], gc: GraphicsContext) extends Drawable {
 def draw(): Unit = {
   gc.clearRect(0, 0, gc.getCanvas.getWidth, gc.getCanvas.getHeight)
 }
  def toXml: Node = {
    <drawable type="Cleared" >
    </drawable>
  }
}
