package CSVInput

import scalafx.Includes.*
import scalafx.scene.control.*

/** Dialog for local input, allowing the entry of a header and a numeric value.
  * Extends the InputDialog trait with specific configurations for handling user
  * inputs.
  */
class LocalInputDialog extends InputDialog[(String, Float)] {
  // Text fields for the header (string) and value (float) input by the user.
  private val headerField = new TextField()
  private val valueField = new TextField()

  // Configure tooltips for the header and value fields
  configureTooltip(
    headerField,
    "Enter data category (will be a blank string if not given)"
  )
  configureTooltip(
    valueField,
    "Enter value, must be a number (will be 0 if not given)"
  )

  // Add labels and text fields to the input box
  inputBox.children =
    Seq(new Label("Header"), headerField, new Label("Value"), valueField)

  // Set the content of the dialog pane to be the input box
  this.getDialogPane.content = inputBox
  // Create and configure the save button
  val saveButton = createButtonAndConfigure()
  // Add listeners to the text fields to control the save button's state
  addChangeListeners(saveButton, headerField, valueField)

  // Set the result converter to output a tuple of the header and value
  resultConverter = dialogButton =>
    if dialogButton == saveButtonType && valueField.text.value.trim.nonEmpty
    then
      // Get the text from the header field
      val header = headerField.text.value
      // Try to convert the text from the value field to a Float, defaulting to 0f if not possible
      val value = valueField.text.value.toFloatOption.getOrElse(0f)
      // Return the header and value as a tuple
      (header, value)
    else
      // Return null if the dialog was canceled
      null
}
