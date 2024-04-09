package scala

import Alerts.{WrongData, WrongFormat, WrongInputFormat}
import Boxes.{CardsAndSummaryBox, CardsBox, ChartBox}
import CSVInput.*
import CSVParser.{CSVParser, LocalCSVParser, WebCSVParser}
import DashboardHandlers._
import Tools.{CardTools, ChartTools}
import scalafx.application.JFXApp3
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Label}
import scalafx.scene.layout.{ColumnConstraints, GridPane, RowConstraints, VBox}
import scalafx.scene.text.Font
import scalafx.stage.FileChooser

/** A ScalaFX application for data visualization. It provides a GUI for users to
  * either create a new dashboard or import an existing dashboard from a file.
  */
object GUI extends JFXApp3 {

  /** The entry point for the ScalaFX application. Sets up the primary stage
    * with the initial scene.
    */
  override def start(): Unit =
    stage = new JFXApp3.PrimaryStage {
      title = "Data Visualization"
      scene = createInitialScene()
    }
  end start

  /** Creates the initial scene displayed to the user, offering options to
    * create a new dashboard or import an existing one from a file.
    *
    * @return
    *   The constructed initial Scene object.
    */
  private def createInitialScene(): Scene =
    val layout = new VBox():
      spacing = 8
      padding = Insets(10)

    val welcomeLabel = new Label(
      "Welcome to DataVizPro.\nChoose action:"
    )
    val newDashboardButton = new Button("New Dashboard")
    val importFromFileButton = new Button("Import Dashboard")

    newDashboardButton.onMouseClicked = _ =>
      stage.scene = createDataImportScene()
    importFromFileButton.onMouseClicked = _ => handleImportFromFile()

    layout.children.addAll(
      welcomeLabel,
      newDashboardButton,
      importFromFileButton
    )
    new Scene(layout)
  end createInitialScene

  /** Creates a scene for importing data, either from a file or the web, and
    * provides instructions for the data format.
    *
    * @return
    *   The constructed Scene for data import.
    */
  private def createDataImportScene(): Scene =
    val layout = new VBox():
      prefHeight = 400
      prefWidth = 800
      spacing = 12
      alignment = Pos.Center
      padding = Insets(10)

    val formatInstruction = new Label(
      "The programme only supports CSV Files where the first row is comprised of Headers and second row is comprised of Values\n" +
        "The files must follow these templates:\n" +
        " - For bar charts and pie charts, headers are categories.\n" +
        " - For scatter plots, headers are X values (numbers) and values are Y values (numbers).\n" +
        " - For multiple-series scatter plots, headers are of format '%Series_name%;%X_value%' and values are Y values (numbers).\n" +
        " - For time series, headers are Dates (dd/mm/yyyy)."
    )
    formatInstruction.font = Font("Times New Roman", 12)

    val importFromFileButton = new Button("Import from File")
    val importFromWebButton = new Button("Import from Web")

    importFromFileButton.onMouseClicked = _ => handleFileImport()
    importFromWebButton.onMouseClicked = _ => handleWebImport()

    layout.children.addAll(
      formatInstruction,
      importFromFileButton,
      importFromWebButton
    )
    new Scene(layout)
  end createDataImportScene

  /** Handles the action for importing data from a local file. Opens a file
    * chooser dialog for the user to select a file.
    */
  private def handleFileImport(): Unit =
    val fileChooser = new FileChooser {
      title = "Choose File"
    }
    val selectedFile = fileChooser.showOpenDialog(stage)
    if (selectedFile != null) then
      processSelectedLocalFile(selectedFile.getPath)
  end handleFileImport

  /** Handles the action for importing data from the web. Opens a dialog for the
    * user to enter a URL.
    */
  private def handleWebImport(): Unit =
    val urlInput = new WebInputDialog
    val input = urlInput.showAndWait()
    processSelectedWebFile(input)
  end handleWebImport

  /** Handles importing a dashboard from a file selected by the user. Supports
    * specific file formats.
    */
  private def handleImportFromFile(): Unit =
    val fileChooser = new FileChooser
    fileChooser.setTitle("Choose File")
    val selected = fileChooser.showOpenDialog(stage)

    if selected != null then
      var fileName = selected.getPath
      fileName.split('.').last.toLowerCase match
        case "quan" =>
          val dashboard = new DashboardImporter(fileName)
          val scene3 = Scene(dashboard, 1000, 800)
          stage.scene = scene3
          stage.setMaximized(true)
        case _ =>
          val alert = new WrongInputFormat
          alert.showAndWait()

  end handleImportFromFile

  /** Loads data from a file into the application, creating the necessary data
    * structures and UI components for visualization.
    *
    * If the file contains valid data, it creates instances for data handling
    * (CardsBox, CardsAndSummaryBox), user interaction (CardTools), and
    * visualization (ChartBox, ChartTools). It then binds the UI components to
    * the main interface and displays the scene.
    *
    * @param file
    *   An Option containing a FileInput object representing the user-selected
    *   file.
    * @param graphBox
    *   A GridPane that serves as the container for the chart.
    * @param mainInterface
    *   A GridPane that holds the main user interface components.
    * @param mainScene
    *   The main Scene of the application where the UI components are displayed.
    */
  private def processFile(
    file: Option[CSVParser],
    graphBox: GridPane,
    mainInterface: GridPane,
    mainScene: Scene
  ): Unit =
    if file.get.readFile() then
      var headers = file.get.headers
      var numbers = file.get.data
      // Creates a new CardsBox instance, passing in the headers and data.
      val dataBox = new CardsBox(headers, numbers)
      // Creates an CardsAndSummaryBox instance, wrapping around the dataBox.
      var dataAndSummary = new CardsAndSummaryBox(dataBox)
      // Creates a CardTools instance, passing in the dataAndSummary.
      val cardTool = new CardTools(dataAndSummary)

      // Binds the preferred width of dataAndSummary to the width of mainInterface, ensuring dataAndSummary resizes with mainInterface.
      dataAndSummary.prefWidthProperty().bind(mainInterface.widthProperty())
      // Similar to the previous line, binds the preferred height of dataAndSummary to the height of mainInterface.
      dataAndSummary.prefHeightProperty().bind(mainInterface.heightProperty())

      // Retrieves data cards from dataBox, representING individual pieces of data or visual components
      var dataCards = dataBox.dataCards
      // Sets the children of dataBox to the retrieved dataCards, likely adding these visual components to the dataBox UI
      dataBox.children = dataCards

      // Adds the cardTool component to the mainInterface grid, specifying its position and span within the grid.
      mainInterface.add(cardTool, 0, 1, 1, 1)
      // Adds dataAndSummary to the mainInterface grid, next to cardTool, specifying its grid position and span.
      mainInterface.add(dataAndSummary, 1, 1, 1, 1)

      // Creates a new ChartBox, a component for displaying diagrams or charts.
      val diagramBox = new ChartBox
      // Creates a ChartTools, a set of tools or controls for interacting with the diagrams/charts and data, passing in diagramBox and dataBox.
      var toolBox = new ChartTools(diagramBox, dataBox)

      // Adds diagramBox to graphBox, specifying its position and span within the grid layout.
      graphBox.add(diagramBox, 1, 0, 1, 2)
      // Adds toolBox next to diagramBox in graphBox, specifying its grid position and span.
      graphBox.add(toolBox, 0, 0, 1, 2)

      // Sets the scene of the application stage to mainScene, applying all the UI changes made.
      stage.scene = mainScene
      // Maximizes the application window to fill the screen.
      stage.setMaximized(true)
    else
      // Creates a new instance of WrongData, likely an alert or notification component indicating an issue with the file data.
      val alert = new WrongData
      // Displays the alert and waits for the user to acknowledge it before proceeding.
      alert.showAndWait()
  end processFile

  /** Processes a locally selected file, typically chosen through a file picker
    * dialog. It uses the file path to create a FileInput object and proceeds
    * with the data loading process.
    *
    * @param filePath
    *   The full path to the selected file.
    * @param web
    *   A boolean flag indicating if the file source is from the web (unused in
    *   this function).
    */
  def processSelectedLocalFile(filePath: String, web: Boolean = false): Unit =

    val (graphBox, mainInterface, mainScene) = initializeInterface()
    var file: Option[CSVParser] = None

    filePath.split('.').last.toLowerCase match
      case "csv" =>
        file = Some(LocalCSVParser(filePath))
        processFile(file, graphBox, mainInterface, mainScene)
      case _ =>
        val alert = new WrongFormat
        alert.showAndWait()
  end processSelectedLocalFile

  /** Similar to processSelectedLocalFile, this function handles files selected
    * from the web. It expects a URL input and processes the file accordingly.
    *
    * @param input
    *   The user-provided input, which is expected to be a URL pointing to a
    *   file.
    */
  def processSelectedWebFile(
    input: Option[scalafx.scene.control.DConvert[String, String => String]#S]
  ): Unit =
    val (graphBox, mainInterface, mainScene) = initializeInterface()
    var file: Option[CSVParser] = None

    input match
      case Some(fileName: String) =>
        file = Some(WebCSVParser(fileName.trim))
        processFile(file, graphBox, mainInterface, mainScene)
      case _ =>
        val alert = new WrongFormat
        alert.showAndWait()
  end processSelectedWebFile

  /** Initializes the user interface components for the main data visualization
    * screen.
    *
    * This method sets up the primary grid layout that includes the data
    * visualization area (graphBox) and the data manipulation tools
    * (mainInterface). It also configures the column and row constraints for the
    * layout to ensure that the visualization and tools are displayed correctly.
    *
    * @return
    *   A tuple containing the graph box (data visualization area), main
    *   interface (tool area), and the main scene configured with these
    *   components.
    */
  def initializeInterface(): (GridPane, GridPane, Scene) = {
    val mainInterface = new GridPane()

    // Sets the horizontal gap (spacing) between columns in the mainInterface grid to 5.0 pixels.
    mainInterface.setHgap(5.0)
    // Sets the vertical gap (spacing) between rows in the mainInterface grid to 2.5 pixels.
    mainInterface.setVgap(2.5)
    // Creates a new Scene 1920x1080 pixels containing the mainInterface grid
    val mainScene = Scene(mainInterface, 1920, 1080)

    // main GUI
    val graphBox = new GridPane()

    // dividing it into two columns with widths of 80% and 20% of the grid's total width.
    graphBox.columnConstraints = Array(
      new ColumnConstraints {
        percentWidth = 20
      },
      new ColumnConstraints {
        percentWidth = 80
      }
    )

    // Adds graphBox to the mainInterface grid at column index 0, row index 0 and span 2 columns and 1 row.
    mainInterface.add(graphBox, 0, 0, 2, 1)

    // These constraints define the widths of the columns and the heights of the rows in mainInterface.
    mainInterface.columnConstraints = Array(
      new ColumnConstraints {
        percentWidth = 20
      },
      new ColumnConstraints {
        percentWidth = 80
      }
    )
    mainInterface.rowConstraints = Array(
      new RowConstraints {
        percentHeight = 60
      },
      new RowConstraints {
        percentHeight = 40
      }
    )

    (graphBox, mainInterface, mainScene)
  }
  // Placeholders for additional methods as needed for data processing, UI updates
}
