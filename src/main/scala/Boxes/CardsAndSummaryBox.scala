package Boxes

import scalafx.geometry.Insets
import scalafx.scene.layout.*
import scalafx.scene.paint.Color

/** Combines a CardsBox with a summary section into a cohesive layout.
  *
  * @param dataBox
  *   The CardsBox component containing the main data representation.
  */
class CardsAndSummaryBox(val dataBox: CardsBox) extends GridPane:
  // Create and configure the summary HBox
  private def createSummaryBox(): HBox =
    val widthProp = this.width
    val heightProp = this.height

    val summary = new HBox {
      prefWidth <== widthProp
      prefHeight <== heightProp * 0.2 // Use 20% of CardsAndSummaryBox's height
      background = Background(
        Array(new BackgroundFill(Color.Black, CornerRadii.Empty, Insets.Empty))
      )
      padding = Insets(5.0)
      spacing = 5.0
    }

    // Instantiate and configure the summary cards
    val summaryCards = new SummaryBox(dataBox.dataCards)
    summaryCards.prefWidth <== summary.width
    summaryCards.prefHeight <== summary.height
    summary.children += summaryCards
    summary
  end createSummaryBox

  // Set up the row constraints for the grid layout
  this.rowConstraints = Array(
    new RowConstraints {
      percentHeight = 20
    }, // Allocate 20% height to the summary row
    new RowConstraints {
      percentHeight = 80
    } // Allocate 80% height to the data row
  )

  // Create and add the summary box to the grid
  val summary = createSummaryBox()
  add(summary, columnIndex = 0, rowIndex = 0, colspan = 2, rowspan = 1)

  // Add the CardsBox to the grid
  add(dataBox, columnIndex = 0, rowIndex = 1, colspan = 2, rowspan = 1)
