package Boxes

import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.{Label, Tooltip}
import scalafx.scene.effect.DropShadow
import scalafx.scene.layout.*
import scalafx.scene.paint.Color
import scalafx.scene.text.Font
import scalafx.util.Duration

/** SummaryBox class displays a summary of statistical data from an array of
  * Card objects. It extends HBox to arrange summary cards horizontally with
  * customizable spacing and background.
  *
  * @param dataCards
  *   An array of Card objects containing the data for summary calculation.
  */
class SummaryBox(dataCards: Array[Card]) extends HBox:
  // Setting background color for the whole HBox
  this.setBackground(
    Background(
      Array(
        new BackgroundFill(Color.color(0, 0, 0), new CornerRadii(0), Insets(0))
      )
    )
  )
  this.setSpacing(5.0) // Setting spacing between elements

  // Calculating summary statistics
  private val sum = dataCards.map(_.value).sum
  private val minCard =
    if (dataCards.nonEmpty) dataCards.minBy(_.value) else null
  private val maxCard =
    if (dataCards.nonEmpty) dataCards.maxBy(_.value) else null
  private val average = sum / dataCards.length
  private val variance = dataCards
    .map(card => math.pow(card.value - average, 2))
    .sum / dataCards.length
  private val standardDeviation = math.sqrt(variance).toFloat

  /** Returns the sum of all the values in the data cards.
    *
    * @return
    *   The sum of values as a Float.
    */
  def getSum = sum

  /** Returns the card with the minimum value.
    *
    * @return
    *   The `Card` object with the minimum value. If the dataCards array is
    *   empty, returns `null`.
    */
  def getMin = minCard

  /** Returns the card with the maximum value.
    *
    * @return
    *   The `Card` object with the maximum value. If the dataCards array is
    *   empty, returns `null`.
    */
  def getMax = maxCard

  /** Returns the average of all the values in the data cards.
    *
    * @return
    *   The average of values as a Float. If the dataCards array is empty, the
    *   result is `NaN`.
    */
  def getAverage = average

  /** Returns the standard deviation of all the values in the data cards.
    * Standard deviation measures the amount of variation or dispersion of a set
    * of values.
    *
    * @return
    *   The standard deviation of values as a Float. If the dataCards array is
    *   empty, the result is `NaN`.
    */
  def getStandardDeviation = standardDeviation

  /** Helper method to create a VBox with a specified label and tooltip. This
    * method is used to generate individual statistic cards for the summary.
    *
    * @param labelText
    *   The text to display on the label of the card.
    * @param tooltipText
    *   The text to display in the tooltip when hovering over the card.
    * @return
    *   A VBox configured as a summary card.
    */
  private def createCard(labelText: String, tooltipText: String): VBox =
    val label = new Label(labelText)
    label.wrapText = true
    label.setFont(Font("Arial Bold", 15))
    label.setTooltip(new Tooltip(tooltipText) {
      showDelay = Duration(0)
    })
    val card = new VBox(label) {
      alignment = Pos.Center
      // #ffb7ce
      background = Background(
        Array(
          BackgroundFill(
            Color.color(1.0, 0.718, 0.808),
            CornerRadii(0),
            Insets(0)
          )
        )
      )
      onMouseEntered = _ => effect = DropShadow(5.0, Color.Black)
      onMouseExited = _ => effect = null
    }
    card.prefWidthProperty().bind(this.widthProperty)
    card.prefHeightProperty().bind(this.heightProperty)
    card
  end createCard

  // Create cards for each statistic
  private val minCardVBox = createCard(
    f"Min: ${if (minCard != null) f"${minCard.value}%.2f" else "N/A"}",
    s"Minimum value found in '${if (minCard != null) minCard.header else "N/A"}'"
  )
  private val sumCard = createCard(f"Sum: $sum%.2f", "Total sum of values")
  private val maxCardVBox = createCard(
    f"Max: ${if (maxCard != null) f"${maxCard.value}%.2f" else "N/A"}",
    s"Maximum value found in '${if (maxCard != null) maxCard.header else "N/A"}'"
  )
  private val averageCard =
    createCard(f"Average: $average%.2f", "Average of values")
  private val sdCard = createCard(
    f"Standard Deviation: $standardDeviation%.2f",
    "Standard Deviation of values"
  )

  // Add all cards to the HBox
  children = Seq(sumCard, minCardVBox, maxCardVBox, averageCard, sdCard)
