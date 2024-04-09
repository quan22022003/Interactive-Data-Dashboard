package Boxes

import scalafx.geometry.Insets
import scalafx.scene.layout.{Background, BackgroundFill, CornerRadii, TilePane}
import scalafx.scene.paint.Color

import scala.collection.mutable.Buffer

/** A container for displaying data as cards in a tile format.
  *
  * @param headers
  *   The headers for each column of data.
  * @param numbers
  *   The numerical data corresponding to each header, organized as rows.
  */
class CardsBox(headers: Seq[String], numbers: Seq[Seq[Float]]) extends TilePane:

  /** Toggles the selection state of a card, adding or removing it from the
    * chosen data.
    *
    * @param card
    *   The card to toggle.
    */
  private def toggleCardSelection(card: Card): Unit =
    if card.isClicked then
      chosenData -= card
      card.toggleClicked(false)
    else
      chosenData += card
      card.toggleClicked(true)

  // Set the initial appearance and layout properties of the TilePane
  background = Background(
    Array(BackgroundFill(Color.Black, CornerRadii.Empty, Insets.Empty))
  )
  padding = Insets(10)
  hgap = 5.0
  vgap = 5.0
  prefColumns = 8
  prefTileWidth = 80
  prefTileHeight = 50

  // Initialize storage for data cards and selected (chosen) data cards
  var dataCards = Array[Card]()
  val chosenData = Buffer[Card]()

  // Populate the CardsBox with cards based on the provided headers and data
  headers.indices.foreach { i =>
    numbers.foreach { line =>
      val newCard = new Card(headers(i), line(i))
      dataCards = dataCards :+ newCard

      // Attach a click event handler to toggle the card's selection state
      newCard.onMouseClicked = _ => toggleCardSelection(newCard)
    }
  }

  // Add the data cards to the TilePane's children to display them
  children = dataCards

end CardsBox
