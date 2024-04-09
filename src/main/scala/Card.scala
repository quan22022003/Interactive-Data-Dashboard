package scala

import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.{Label, Tooltip}
import scalafx.scene.effect.DropShadow
import scalafx.scene.layout.{Background, BackgroundFill, CornerRadii, VBox}
import scalafx.scene.paint.Color
import scalafx.scene.text.Font
import scalafx.util.Duration

/** Represents a graphical card that displays a value and a header. The card can
  * be in a clicked or unclicked state, which changes its appearance.
  *
  * @param header
  *   The header text of the card.
  * @param value
  *   The numerical value displayed on the card.
  */

class Card(val header: String, var value: Float) extends VBox {
  // Initial card colors
  var unclickedColor: Color = Color.color(1, 0.718, 0.808)
  private val clickedColor = Color.color(1, 0.525, 0.675)

  // Current background starts as unclicked
  this.updateBackgroundColor(false)

  // Track whether the card is in clicked state
  private var _isClicked = false

  // Initial background setup
  this.background = Background(
    Array(BackgroundFill(unclickedColor, CornerRadii.Empty, Insets.Empty))
  )
  this.prefWidth = 100
  this.prefHeight = 75
  this.padding = Insets(8)
  this.alignment = Pos.Center

  // Tooltip setup
  private val infoTooltip = Tooltip(s"$header: $value")
  infoTooltip.showDelay = Duration.Zero

  // Label setup
  private val numLabel = new Label(value.toString) {
    tooltip = infoTooltip
    font = Font("Times New Roman", 18)
  }
  numLabel.autosize()

  this.children.add(numLabel)

  // Drop shadow effect for mouse hover
  private val dropShadowEffect = DropShadow(5.0, Color.Gray)

  this.onMouseEntered = _ => this.effect = dropShadowEffect
  this.onMouseExited = _ => this.effect = null

  /** Updates the card's background color based on its clicked state.
    *
    * @param isClicked
    *   Boolean indicating whether the card is clicked.
    */
  def updateBackgroundColor(isClicked: Boolean): Unit =
    this.background = Background(
      Array(
        BackgroundFill(
          if (isClicked) clickedColor else unclickedColor,
          CornerRadii.Empty,
          Insets.Empty
        )
      )
    )

  /** Toggles the card's clicked state and updates its background color.
    *
    * @param clicked
    *   Boolean indicating the new clicked state.
    */
  def toggleClicked(clicked: Boolean): Unit = {
    _isClicked = clicked
    if (_isClicked) {
      this.setBackground(
        Background(
          Array(BackgroundFill(clickedColor, CornerRadii.Empty, Insets.Empty))
        )
      )
    } else {
      this.setBackground(
        Background(
          Array(BackgroundFill(unclickedColor, CornerRadii.Empty, Insets.Empty))
        )
      )
    }
  }

  /** Changes the card's color to a specified new color.
    *
    * @param newColor
    *   The new Color object to apply to the card's background.
    */
  def changeColor(newColor: Color): Unit = {
    this.setBackground(
      Background(
        Array(BackgroundFill(newColor, CornerRadii.Empty, Insets.Empty))
      )
    )
    this.unclickedColor = newColor
  }

  // Getters for clicked state
  def isClicked: Boolean = _isClicked
}
