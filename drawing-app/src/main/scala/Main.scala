import javafx.scene.canvas.GraphicsContext
import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.event.ActionEvent
import scalafx.scene.Scene
import scalafx.scene.canvas.Canvas
import scalafx.scene.control._
import scalafx.scene.layout.{BorderPane, VBox}
import scalafx.scene.paint.Color
import scalafx.scene.text.Text
import scalafx.stage.FileChooser
import scala.xml.Node

object Main extends JFXApp{
  // Making the primary stage
  stage = new PrimaryStage {
    width = 800
    height = 600
    title = "Drawing app"
  }

  //Making the menubar and adding the items to it

  private val menuBar = new MenuBar
  private val fileMenu = new Menu("File")
  private val openItem = new MenuItem("Open")
  private val saveItem = new MenuItem("Save")
  private val newItem = new MenuItem("New")
  fileMenu.items = List(openItem, saveItem, newItem)
  menuBar.menus = List(fileMenu)
  // newItem clears the canvas and the current list of drawings
  newItem.onAction = (_: ActionEvent) => {
    gc.clearRect(0, 0, canvas.getWidth, canvas.getHeight)
    currentDrawings = currentDrawings.empty
  }
  // openItem opens a xml file from a file selector and then draws it to the board
  openItem.onAction = (_: ActionEvent) => {
    val chooser = new FileChooser {
      title = "Open Drawing"
    }
    val file = chooser.showOpenDialog(stage)
    if (file != null) {
      if (file.getName.endsWith(".xml")) {
        gc.clearRect(0, 0, canvas.getWidth, canvas.getHeight)
        currentDrawings = currentDrawings.empty
        val xmlData = xml.XML.loadFile(file)
        val loadedDrawings = (xmlData \ "drawable").map(drawingsFromNode)
        loadedDrawings.foreach(d => {
          d.draw()
          currentDrawings = currentDrawings :+ d
        })
      }
    }
  }
  // saveItem makes the current drawing list to a xml file
  saveItem.onAction = (_: ActionEvent) => {
    val chooser = new FileChooser {
      title = "Save Drawing"
    }
    val file = chooser.showSaveDialog(stage)
    if (file != null) {
      val xmlData = <Drawing>{currentDrawings.map(c => c.toXml)}</Drawing>
      xml.XML.save(file.getAbsolutePath, xmlData)
    }
  }

  // drawingGroup is group of buttons and it decides what we draw with the items
  private val drawingGroup = new ToggleGroup()
  private val lineItem = new RadioButton("Line")
  lineItem.setToggleGroup(drawingGroup)
  lineItem.setSelected(true)

  private val circleItem = new RadioButton("Circle")
  circleItem.setToggleGroup(drawingGroup)

  private val ellipseItem = new RadioButton("Ellipse")
  ellipseItem.setToggleGroup(drawingGroup)

  private val rectangleItem = new RadioButton("Rectangle")
  rectangleItem.setToggleGroup(drawingGroup)
  //Left side of the ui also contais other elements.
  private val text: Text = new Text()
  text.setText("Color")
  private val lineColor = new ColorPicker(Color.Blue)
  private val undoButton = new Button("Undo")
  private val clearButton = new Button("Clear")
  private val leftBox = new VBox(10)
  leftBox.getChildren.addAll(lineItem, circleItem, ellipseItem, rectangleItem, text, lineColor, undoButton, clearButton)
  // clear button clears the canvas and empties the list of current drawings
  clearButton.onAction = (_: ActionEvent) => {
    gc.clearRect(0, 0, canvas.getWidth, canvas.getHeight)
    currentDrawings = currentDrawings :+ new ClearedBoard(currentDrawings, gc)
  }

  //undo removes the last added drawing and draws everything else to the canvas
  undoButton.onAction = (_: ActionEvent) => {
    if(currentDrawings.nonEmpty) {
      currentDrawings = currentDrawings.dropRight(1)
      gc.clearRect(0, 0, canvas.getWidth, canvas.getHeight)
      currentDrawings.foreach(_.draw())
    }
  }
  //Linecolor set the color of the drawings from the color picking menu
  lineColor.setOnAction(_ => {
    gc.setFill(lineColor.getValue)
    gc.setStroke(lineColor.getValue)
    currentLineColor = lineColor.getValue
  })



  //start coordinates of the drawing
  private var startCoordx: Double = 0
  private var startCoordy: Double = 0

  //values for current color and here we also make the canvas interactable
  private var currentLineColor: Color = Color.Blue
  private var currentDrawings: Seq[Drawable] = List[Drawable]()
  private val canvas = new Canvas(600, 600)
  private val gc: GraphicsContext = canvas.getGraphicsContext2D
  gc.setStroke(currentLineColor)
  gc.setFill(currentLineColor)
  gc.setLineWidth(5)


  // when mouse is dragged we show where the selected drawing would go, but we do not save it anywhere
  canvas.setOnMouseDragged(e => {
    gc.clearRect(0, 0, canvas.getWidth, canvas.getHeight)
    currentDrawings.foreach(_.draw())
    gc.setStroke(currentLineColor)
    if (lineItem.isSelected) {
      new DrawLine(startCoordx, startCoordy, e.getX, e.getY, gc, currentLineColor).draw()
    } else if (circleItem.isSelected) {
      new DrawCircle(startCoordx, startCoordy, e.getX, e.getY, gc, currentLineColor).draw()
    } else if (ellipseItem.isSelected) {
      new DrawEllipse(startCoordx, startCoordy, e.getX, e.getY, gc, currentLineColor).draw()
    } else if (rectangleItem.isSelected) {
      new DrawRectangle(startCoordx, startCoordy, e.getX, e.getY, gc, currentLineColor).draw()
    }
  })
  //on mouse pressed we get the start values for the drawing
  canvas.setOnMousePressed(e => {
    startCoordx = e.getX
    startCoordy = e.getY
  })
  // on mouse released we get the end values for the drawing and we create a new drawable with the values and save it to the current drawings
  canvas.setOnMouseReleased(e => {
    if (lineItem.isSelected) {
      val line = new DrawLine(startCoordx, startCoordy, e.getX, e.getY, gc, currentLineColor)
      line.draw()
      currentDrawings = currentDrawings :+ line
    } else if (circleItem.isSelected) {
      val circle = new DrawCircle(startCoordx, startCoordy, e.getX, e.getY, gc, currentLineColor)
      circle.draw()
      currentDrawings = currentDrawings :+ circle
    } else if (ellipseItem.isSelected) {
      val ellipse = new DrawEllipse(startCoordx, startCoordy, e.getX, e.getY, gc, currentLineColor)
      ellipse.draw()
      currentDrawings = currentDrawings :+ ellipse
    } else if (rectangleItem.isSelected) {
      val rectangle = new DrawRectangle(startCoordx, startCoordy, e.getX, e.getY, gc, currentLineColor)
      rectangle.draw()
      currentDrawings = currentDrawings :+ rectangle
    }
  })


  //Here we set the layout of the UI

  private val pane: BorderPane = new BorderPane
  private val scene: Scene = new Scene(pane)
  pane.setCenter(canvas)
  pane.setTop(menuBar)
  pane.setLeft(leftBox)
  stage.setScene(scene)



  //Making the color compatible with XML for saving

  def xmlToColor(node: xml.Node): Color = {
    val red = (node \ "@red").text.toDouble
    val green = (node \ "@green").text.toDouble
    val blue = (node \ "@blue").text.toDouble
    val opacity = (node \ "@opacity").text.toDouble
    Color(red, green, blue, opacity)
  }

  // helper for opening xml files

  def drawingsFromNode(n: Node): Drawable = {
    val drawingType = (n \ "@type").text

    drawingType match {
      case "Rectangle" =>
        val color = (n \ "color").map(xmlToColor).head
        val startX = (n \ "@x").text.toDouble
        val startY = (n \ "@y").text.toDouble
        val endX = (n \ "@outX").text.toDouble
        val endY = (n \ "@outY").text.toDouble
        new DrawRectangle(startX, startY, endX, endY, gc, color)
      case "Ellipse" =>
        val color = (n \ "color").map(xmlToColor).head
        val startX = (n \ "@x").text.toDouble
        val startY = (n \ "@y").text.toDouble
        val endX = (n \ "@outX").text.toDouble
        val endY = (n \ "@outY").text.toDouble
        new DrawEllipse(startX, startY, endX, endY, gc, color)
      case "Circle" =>
        val startX = (n \ "@x").text.toDouble
        val startY = (n \ "@y").text.toDouble
        val endX = (n \ "@outX").text.toDouble
        val endY = (n \ "@outY").text.toDouble
        val color = (n \ "color").map(xmlToColor).head
        new DrawCircle(startX, startY, endX, endY, gc, color)
      case "Line" =>
        val startX = (n \ "@x").text.toDouble
        val startY = (n \ "@y").text.toDouble
        val endX = (n \ "@outX").text.toDouble
        val endY = (n \ "@outY").text.toDouble
        val color = (n \ "color").map(xmlToColor).head
        new DrawLine(startX, startY, endX, endY, gc, color)
      case _ => new ClearedBoard(currentDrawings, gc)
    }
  }
}

