package CSVInput

import scalafx.Includes.*
import scalafx.scene.control.*
import scalafx.scene.layout.VBox

/** Dialog for web input, allowing the user to enter a URL string. Extends the
  * InputDialog trait with specific configurations for handling URL input.
  */
class WebInputDialog extends InputDialog[String] {
  // Create a text field for URL input
  val inputField = new TextField()
  // Add a label and the text field to the input box
  inputBox.children = Seq(new Label("Enter URL: "), inputField)
  // Set the content of the dialog pane to be the input box
  this.getDialogPane.content = inputBox
  // Set the height and width of the dialog
  this.setHeight(100)
  this.setWidth(500)

  // Create and configure the save button
  val saveButton = createButtonAndConfigure()
  // Add a listener to the URL input field to control the save button's state
  inputField.text.onChange { (_, _, newValue) =>
    saveButton.disable = newValue.trim.isEmpty
  }

  // Set the result converter to output the URL text
  this.resultConverter = dialogButton =>
    if dialogButton == saveButtonType then inputField.text.value else null

  def getInputBox: VBox = inputBox
}
