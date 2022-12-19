import scalafx.scene.paint.Color
// Trait that help us make the classes for different shapes
trait Drawable extends Serializable{
  // draws the shape to the canvas
  def draw(): Unit
  // makes the drawable to xml
  def toXml: xml.Node
}
object Drawable {
  def colorToXML(color: Color): xml.Node = {
      <color red={color.red.toString} green={color.green.toString} blue={color.blue.toString} opacity={color.opacity.toString}/>
  }
}
