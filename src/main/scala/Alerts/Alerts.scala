package Alerts

import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType

/** Represents an alert for when there is invalid data input.
  */
class WrongData extends Alert(AlertType.INFORMATION):
  this.setTitle("Invalid Data Input")
  this.setContentText("No Data Chosen or Wrong Data Format")

/** Represents an alert for when there is invalid data input for the scatter
  * plot.
  */
class WrongDataScatter extends Alert(AlertType.INFORMATION):
  this.setTitle("Invalid Data Input")
  this.setContentText(
    "No Data Chosen or the Headers (X values) are not numbers."
  )

/** Represents an alert for when there is invalid data input for the
  * multiple-series scatter plot.
  */
class WrongDataMultiScatter extends Alert(AlertType.INFORMATION):
  this.setTitle("Invalid Data Input")
  this.setContentText(
    "No Data Chosen or the Headers (X values) are not of format '%Series%;%X_values%'."
  )

/** Represents an alert for when there is invalid data input for the scatter
  * plot.
  */
class WrongDataTimeSeries extends Alert(AlertType.INFORMATION):
  this.setTitle("Invalid Data Input")
  this.setContentText(
    "No Data Chosen or the Headers (Dates ) are not of format 'dd/mm/yyyy'."
  )

/** Represents an alert for when an invalid file is input, specifically non-CSV
  * files.
  */
class WrongFormat extends Alert(AlertType.INFORMATION):
  setTitle("Invalid File Input")
  setContentText("Please only choose CSV files")

/** Represents an alert for when an invalid file format is input, specifically
  * non-.quan files.
  */
class WrongInputFormat extends Alert(AlertType.INFORMATION):
  setTitle("Invalid File Input")
  setContentText("Please only choose .quan files")

/** Represents an alert for when an invalid .quan files is input.
  */
class EmptyQuanFile extends Alert(AlertType.INFORMATION):
  setTitle("Empty File")
  setContentText("Please only choose valid .quan files")
