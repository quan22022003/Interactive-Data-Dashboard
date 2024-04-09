package Tools

import Alerts.WrongData
import Boxes.{CardsAndSummaryBox, CardsBox, SummaryBox}
import CSVInput.*
import scalafx.Includes.{jfxColor2sfx, jfxGridPane2sfx}
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.{Button, ColorPicker}
import scalafx.scene.layout.{Background, BackgroundFill, CornerRadii, VBox}
import scalafx.scene.paint.Color
import scalafx.scene.text.Font

/** Provides tools for interacting with cards and summaries in the dashboard.
  * This includes functionality to change card sizes, colors, add new data,
  * duplicate, remove, and perform rectangle selection on cards.
  *
  * @param dataAndSummary
  *   An instance of CardsAndSummaryBox containing the data and summary boxes.
  */
class CardTools(dataAndSummary: CardsAndSummaryBox) extends VBox:

  // References to data and summary components of the dashboard for direct manipulation
  private val dataBox = dataAndSummary.dataBox
  private val summary = dataAndSummary.summary

  // Styling and layout setup for the VBox containing the tools
  this.setBackground(
    Background(
      Array(BackgroundFill(Color.Black, CornerRadii.Empty, Insets.Empty))
    )
  )
  padding = Insets(15)
  spacing = 10
  alignment = Pos.Center

  /** Creates a button with common styling and a click event handler.
    *
    * @param text
    *   The text displayed on the button.
    * @param handler
    *   The function to call when the button is clicked.
    * @return
    *   A configured Button instance.
    */
  private def createButton(text: String, handler: () => Unit): Button =
    new Button(text) {
      font = Font("Times New Roman", 15)
      style = "-fx-background-color: #ffb7ce; -fx-text-fill: black;"
      onMouseClicked = _ => handler()
    }

  // Buttons for increasing and decreasing card size, leveraging the common button creation method
  private val increaseSize =
    createButton("Increase Card Size", () => adjustCardSize(1.25))
  private val decreaseSize =
    createButton("Decrease Card Size", () => adjustCardSize(0.75))

  /** Adjusts the size of the cards in the data box.
    *
    * @param factor
    *   The factor by which to adjust the card size. Greater than 1 increases
    *   size, less than 1 decreases size.
    */
  private def adjustCardSize(factor: Double): Unit = {
    dataBox.prefTileHeight = dataBox.prefTileHeight.toDouble * factor
    dataBox.prefTileWidth = dataBox.prefTileHeight.toDouble * factor
  }

  // ColorPicker setup for changing card color
  private val picker = new ColorPicker(Color.color(1, 0.718, 0.808))
  private val colorLabel =
    createButton("Change Card Color", () => changeCardsColor(picker.value()))
  private val colorPicker = new VBox(colorLabel, picker)
  setupColorPicker()

  /** Configures the layout and behavior of the ColorPicker component.
    */
  private def setupColorPicker(): Unit = {
    colorLabel.setAlignment(Pos.Center)
    colorLabel.prefWidthProperty().bind(colorPicker.widthProperty())
    colorLabel.prefHeightProperty().bind(colorPicker.heightProperty())
    picker.prefWidthProperty().bind(colorPicker.widthProperty())
    picker.prefHeightProperty().bind(colorPicker.heightProperty())
  }

  /** Changes the color of all cards in the data box.
    *
    * @param newColor
    *   The new color to apply to the cards.
    */
  private def changeCardsColor(newColor: Color): Unit = {
    dataBox.chosenData.foreach(_.changeColor(newColor))
    dataBox.refreshCardsDisplay()
  }

  // Button for adding new data, invoking a dialog to collect user input
  private val addData = createButton("Add Data", handleAddData)

  /** Handles the addition of new data through a dialog interface. Adds a new
    * card to the data box if valid data is provided.
    */
  private def handleAddData(): Unit = {
    val dialog = new LocalInputDialog
    dialog.showAndWait() match {
      case Some((header: String, value: Float)) => addNewCard(header, value)
      case None                                 => new WrongData().showAndWait()
    }
  }

  /** Adds a new card with the specified header and value to the data box and
    * refreshes the summary.
    *
    * @param header
    *   The header text for the new card.
    * @param value
    *   The value for the new card.
    */
  private def addNewCard(header: String, value: Float): Unit = {
    val newCard = new Card(header, value)
    setupCardClickBehavior(
      newCard
    ) // Ensures new card has click behavior for selection
    dataBox.dataCards = dataBox.dataCards :+ newCard
    dataBox.refreshCardsDisplay()
    updateSummary() // Updates the summary to reflect the change in data
  }

  // Button for hiding and showing selected cards
  private val hide = createButton("Hide Chosen Cards", toggleHideShow)
  private var isHidden = false

  /** Toggles the visibility of selected cards. If cards are currently shown,
    * hides them, and vice versa.
    */
  private def toggleHideShow(): Unit = {
    if (isHidden) {
      dataBox.children = dataBox.dataCards
      hide.text = "Hide Chosen Cards"
      isHidden = false
    } else {
      dataBox.children = dataBox.dataCards.diff(dataBox.chosenData)
      hide.text = "Show Hidden Cards"
      isHidden = true
    }
  }

  // Button for duplicating selected cards
  private val duplicate =
    createButton("Duplicate Chosen Cards", handleDuplicateCards)

  /** Duplicates the currently selected cards, adding them to the data box and
    * deselecting them.
    */
  private def handleDuplicateCards(): Unit = {
    val duplicatedCards =
      dataBox.chosenData.map(card => new Card(card.header, card.value))
    duplicatedCards.foreach { newCard =>
      setupCardClickBehavior(newCard)
      dataBox.dataCards = dataBox.dataCards :+ newCard
    }
    dataBox.chosenData.foreach(_.toggleClicked(false))
    dataBox.chosenData.clear()
    dataBox.refreshCardsDisplay()
    updateSummary()
  }

  // Button for removing selected cards
  private val remove = createButton("Remove Chosen Cards", handleRemoveCards)

  /** Removes the currently selected cards from the data box and updates the
    * summary.
    */
  private def handleRemoveCards(): Unit = {
    dataBox.dataCards = dataBox.dataCards.filterNot(dataBox.chosenData.contains)
    dataBox.chosenData.clear()
    dataBox.refreshCardsDisplay()
    updateSummary()
  }

  // Create a transparent Pane that will serve as the canvas for the rectangle
  val selectionOverlay = new SelectionOverlay(dataBox)

  // Get the parent of the databox to put an overlay over it
  val container =
    dataBox.parent.value.asInstanceOf[javafx.scene.layout.GridPane]
  // Button and mechanism for rectangle selection of cards
  private val rectangleSelectionTool =
    createButton("Select Data Points", handleRectangleSelection)

  /** Enables selection of cards using a rectangular selection area.
    */
  private def handleRectangleSelection(): Unit = {
    if selectionOverlay.isActive then
      container.children.remove(selectionOverlay)
      selectionOverlay.toggleStatus()
    else
      container.add(
        selectionOverlay,
        columnIndex = 0,
        rowIndex = 1,
        colspan = 2,
        rowspan = 1
      )
      selectionOverlay.toggleStatus()
  }

  /** Updates the summary to reflect the current state of the data cards.
    */
  private def updateSummary(): Unit = {
    summary.children = Seq(new SummaryBox(dataBox.dataCards))
  }

  /** Helper method to refresh the display of cards in the CardsBox
    */
  extension (db: CardsBox)
    private def refreshCardsDisplay(): Unit = {
      db.children = db.dataCards
    }

  // Helper method for setting up the behavior when a card is clicked
  private def setupCardClickBehavior(card: Card): Unit = {
    card.onMouseClicked = _ => {
      if (card.isClicked) {
        dataBox.chosenData -= card
        card.toggleClicked(false)
      } else {
        dataBox.chosenData += card
        card.toggleClicked(true)
      }
    }
  }

  // Aggregate all components and bind their properties for layout
  private val components = Array(
    increaseSize,
    decreaseSize,
    colorPicker,
    addData,
    remove,
    hide,
    duplicate,
    rectangleSelectionTool
  )
  components.foreach(comp => {
    comp.prefWidthProperty().bind(this.widthProperty())
    comp.prefHeightProperty().bind(this.heightProperty())
  })

  children = components
