package Tools

import Alerts.WrongData
import Boxes.{CardsBox, ChartBox}
import CSVInput.*
import DashboardHandlers.DashboardExporter
import GraphDisplay.*
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.chart.Chart
import scalafx.scene.control.{Button, Label, Tooltip}
import scalafx.scene.layout.{Background, BackgroundFill, CornerRadii, VBox}
import scalafx.scene.paint.Color
import scalafx.scene.text.Font
import scalafx.stage.FileChooser
import scalafx.stage.FileChooser.ExtensionFilter
import scalafx.util.Duration

import java.io.File
import scala.GUI.stage

/** Provides interactive tools for generating and managing charts in the
  * dashboard. Allows users to create various types of charts, save the
  * dashboard, and manipulate data visualization.
  *
  * @param diagramBox
  *   The container where the generated charts will be displayed.
  * @param dataBox
  *   The container holding the data used for chart generation.
  */
class ChartTools(val diagramBox: ChartBox, val dataBox: CardsBox)
    extends VBox():

  private val chosenData = dataBox.chosenData
  // Configuration and initialization of chart generation buttons.
  private val barChartButton = createChartButton(
    "Bar Chart",
    () => new BarChartDisplay(dataBox.chosenData),
    Some(createTooltip("Create new bar chart"))
  )
  private val pieChartButton = createChartButton(
    "Pie Chart",
    () => new PieChartDisplay(dataBox.chosenData),
    Some(createTooltip("Create new pie chart"))
  )
  private val scatterChartButton = createChartButton(
    "Scatter Chart",
    () => new ScatterChartDisplay(dataBox.chosenData),
    Some(createTooltip("Create new scatter chart"))
  )

  this.setBackground(
    Background(
      Array(
        new BackgroundFill(
          (Color.color(0, 0, 0)),
          new CornerRadii(0),
          Insets(0)
        )
      )
    )
  )
  this.setPadding(Insets(25))
  this.setSpacing(10)
  this.setAlignment(Pos.Center)
  private val timeSeriesScatterButton = createChartButton(
    "Time Series Line Chart",
    () => new LineChartTimeSeriesDisplay(dataBox.chosenData),
    Some(createTooltip("Headers must be of format 'dd/mm/yyyy'"))
  )
  private val timeSeriesBarButton = createChartButton(
    "Multiple Series Scatter Chart",
    () => new MultiSeriesScatterChartDisplay(dataBox.chosenData),
    Some(createTooltip("Headers must be of format '%Series%;%X_values%'"))
  )

  /** Configures and handles the functionality for saving the current dashboard
    * state to a file.
    */
  private val saveButton = new Button("Save To File") {
    font = Font("Times New Roman", 15)
    style = "-fx-background-color: #ffb7ce; -fx-text-fill: black;"
    wrapText = true
    tooltip = new Tooltip("Save this dashboard to file") {
      showDelay = Duration(0)
    }
    onAction = _ => saveDashboard()
  }
  // Adding all the interactive buttons to the VBox.
  private val components = Array(
    barChartButton,
    pieChartButton,
    scatterChartButton,
    timeSeriesScatterButton,
    timeSeriesBarButton,
    saveButton
  )
  private var diagrams: Array[ChartDisplay] = Array()

  /** Creates a tooltip with the specified message and an optional delay.
    *
    * @param message
    *   The message to be displayed in the tooltip.
    * @param delay
    *   The delay before showing the tooltip. Defaults to immediate display.
    * @return
    *   A Tooltip instance with the specified configuration.
    */
  private def createTooltip(
    message: String,
    delay: Duration = Duration(0)
  ): Tooltip =
    val tooltip = new Tooltip(message)
    tooltip.showDelay = delay
    tooltip

  /** Creates a button for generating a specific type of chart.
    *
    * @param label
    *   The text label of the button.
    * @param chartType
    *   A function that returns a new instance of ChartDisplay.
    * @param tooltip
    *   An optional tooltip for the button.
    * @return
    *   A configured Button instance.
    */
  private def createChartButton(
                                 label: String,
                                 chartType: () => ChartDisplay,
                                 providedTooltip: Option[Tooltip] = None
  ): Button = {
    val button = new Button(label) {
      font = Font("Times New Roman", 15)
      style = "-fx-background-color: #ffb7ce; -fx-text-fill: black;"
      wrapText = true
      tooltip =
        providedTooltip.getOrElse(new Tooltip("Save this dashboard to file") {
          showDelay = Duration(0)
        })
      onMouseClicked = _ => generateChart(chartType)
    }
    button
  }

  /** Generates a chart using the provided chart type function and updates the
    * UI accordingly.
    *
    * @param chartType
    *   A function that returns a new instance of ChartDisplay.
    */
  private def generateChart(chartType: () => ChartDisplay): Unit = {
    val diagram = chartType()
    diagram.chart match {
      case Some(chart: Chart) =>
        diagrams = diagrams :+ diagram
        diagramBox.children = Array(chart)
      case _ =>
        val alert = new WrongData
        alert.showAndWait()
    }
  }

  /** Opens a file chooser dialog allowing the user to specify a file location
    * and name for saving the dashboard.
    */
  private def saveDashboard(): Unit =
    val chooser = new FileChooser
    chooser.setTitle("Save dashboard")
    chooser.extensionFilters.add(
      new FileChooser.ExtensionFilter("Custom File", "*.quan")
    )

    val nameInput = new WebInputDialog
    val inputBox = nameInput.getInputBox
    val inputField = nameInput.inputField
    val label = Label("Enter File Name")
    inputBox.children = Array(label, inputField)

    val savedChart = diagrams.last match
      case b: BarChartDisplay                 => "1"
      case mS: MultiSeriesScatterChartDisplay => "2"
      case p: PieChartDisplay                 => "3"
      case sT: LineChartTimeSeriesDisplay     => "4"
      case s: ScatterChartDisplay             => "5"
      case _                                  => "0"

    val name = nameInput.showAndWait()
    name match
      case Some(name: String) =>
        chooser.setInitialFileName(name)
        val selected = chooser.showSaveDialog(stage)
        if selected != null then
          val path = selected.getPath
          val file = new File(path)
          val saver =
            new DashboardExporter(file, dataBox.chosenData, savedChart)
          saver.saveFile()
      case _ =>
        val alert = new WrongData
        alert.showAndWait()
  end saveDashboard

  components.foreach(comp => {
    comp.prefWidthProperty().bind(this.widthProperty())
    comp.prefHeightProperty().bind(this.heightProperty())
    comp.setFont(Font("Times New Roman", 15))
    comp.wrapText = true
    comp.wrapText = true
  })
  this.children = components
