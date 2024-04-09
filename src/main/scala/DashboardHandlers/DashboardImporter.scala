package DashboardHandlers

import Boxes.CardsBox
import GraphDisplay.*
import scalafx.geometry.Pos
import scalafx.scene.layout.{GridPane, HBox, RowConstraints}
import scalafx.scene.paint.Color

import java.io.{BufferedReader, FileReader}

/** Loads and processes the data from a file into a dashboard format, ready for
  * display in the application.
  *
  * @param fileName
  *   Path to the file containing the data to be imported.
  */
class DashboardImporter(fileName: String) extends GridPane {

  /** Creates a graph box based on the provided data and graph type.
    *
    * @param dataCards
    *   The data cards to be displayed.
    * @param graphType
    *   The type of graph to display.
    * @param color
    *   The color to apply to the data cards.
    * @return
    *   A HBox containing the graph.
    */
  private def createGraphBox(
    dataCards: Seq[Card],
    graphType: String,
    color: Color
  ): HBox =
    dataCards.foreach(_.changeColor(color))

    val graphBox = new HBox {
      this.prefHeightProperty().bind(this.heightProperty)
      this.prefWidthProperty().bind(this.widthProperty)
      alignment = Pos.Center
    }

    // Select and create the appropriate chart based on the graph type
    val chart = graphType match {
      case "1" => new BarChartDisplay(dataCards.toBuffer).chart
      case "2" => new MultiSeriesScatterChartDisplay(dataCards.toBuffer).chart
      case "3" => new PieChartDisplay(dataCards.toBuffer).chart
      case "4" => new LineChartTimeSeriesDisplay(dataCards.toBuffer).chart
      case "5" => new ScatterChartDisplay(dataCards.toBuffer).chart
      case _   => throw new Exception("Invalid graph type")
    }
    chart.foreach(graphBox.children.add(_))
    graphBox

  end createGraphBox

  /** Configures the grid layout and constraints.
    */
  private def configureGrid(): Unit =
    this.setHgap(5.0)
    this.setVgap(2.5)
    this.rowConstraints = Array(
      new RowConstraints {
        percentHeight = 80
      },
      new RowConstraints {
        percentHeight = 20
      }
    )
  end configureGrid

  // Use ImportDashboardUtils to get the parsed data
  private val (color, graphType, headers, data) =
    DashboardImporter.parseFile(fileName)

  // Set up grid constraints and alignment
  configureGrid()

  // Create the visual elements of the dashboard with the parsed data
  private val dataBox = new CardsBox(headers, data)
  dataBox.prefHeightProperty().bind(this.heightProperty())
  dataBox.prefWidthProperty().bind(this.widthProperty())

  private val graphBox = createGraphBox(dataBox.dataCards, graphType, color)

  // Add the boxes to the grid
  this.add(graphBox, 0, 0, 2, 1)
  this.add(dataBox, 0, 1, 2, 1)
}

/** Handles reading and parsing of data from files for the dashboard.
  */
object DashboardImporter {

  /** Parses the RGB color values from a comma-separated string.
    *
    * @param colorString
    *   The string containing color values.
    * @return
    *   The Color object parsed from the string.
    */
  def parseColor(colorString: String): Color = {
    val rgb = colorString.trim.split(',').map(_.toDouble)
    Color.color(rgb(0), rgb(1), rgb(2))
  }

  /** Parses the specified file, extracting the color, graph type, headers, and
    * data.
    *
    * @param fileName
    *   Path to the file to be parsed.
    * @return
    *   A tuple containing the extracted color, graph type, headers, and data.
    */
  def parseFile(
    fileName: String
  ): (Color, String, Seq[String], Seq[Seq[Float]]) = {
    var headers = Seq.empty[String]
    var data = Seq.empty[Seq[Float]]
    var color = Color.White
    var graphType = ""

    val fileObject = new FileReader(fileName)
    val reader = new BufferedReader(fileObject)
    try {
      val colorLine = reader.readLine()
      if (colorLine == null)
        throw new IllegalArgumentException(
          "File is empty or does not start with a color line."
        )
      color = parseColor(colorLine)

      val graphTypeLine = reader.readLine()
      if (graphTypeLine == null)
        throw new IllegalArgumentException(
          "File does not contain a graph type line following the color line."
        )
      graphType = graphTypeLine.trim

      val headersLine = reader.readLine()
      if (headersLine == null)
        throw new IllegalArgumentException(
          "File does not contain headers following the graph type line."
        )
      headers = headersLine.split(',').toSeq

      val dataLines =
        Iterator.continually(reader.readLine()).takeWhile(_ != null).toSeq
      if (dataLines.isEmpty)
        throw new IllegalArgumentException("No data lines found in the file.")
      data = dataLines.map(_.trim.split(',').map(_.toFloat))
    } catch {
      case e: Exception =>
        // Log the exception
        println(s"Failed to parse file: ${e.getMessage}")
        // Re-throw the exception to propagate the error up the call stack
        throw e
    } finally {
      reader.close()
      fileObject.close()
    }

    (color, graphType, headers, data)
  }

}
