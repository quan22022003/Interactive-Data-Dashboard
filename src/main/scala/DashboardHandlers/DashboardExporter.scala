package DashboardHandlers

import scalafx.scene.paint.Color

import java.io.{BufferedWriter, File, FileWriter}
import scala.collection.mutable.Buffer

/** Handles saving of the dashboard state to a file.
  *
  * @param file
  *   The file where the data will be saved.
  * @param chosenData
  *   The data to save.
  * @param chartType
  *   The type of chart represented by the data.
  */
class DashboardExporter(
  file: File,
  chosenData: Buffer[Card],
  chartType: String
) {
  private val color = chosenData.head.unclickedColor

  /** Serializes the chosen data into a sequence of string representations.
    *
    * @return
    *   The serialized data as a sequence of strings.
    */
  private def serializeData(): Seq[String] = {
    val headers = chosenData.map(_.header).distinct
    val dataByHeader =
      chosenData.groupBy(_.header).view.mapValues(_.map(_.value))

    // Determine the number of rows based on the maximum number of values under any header
    val maxRows = dataByHeader.values.map(_.size).max

    // Prepend the headers as the first row
    val headerRow = headers.mkString(",")
    val dataRows = (0 until maxRows).map { rowIndex =>
      headers
        .map(header => dataByHeader(header).lift(rowIndex).getOrElse("0"))
        .mkString(",")
    }

    // return the headerRow appended by dataRows
    headerRow +: dataRows
  }

  /** Saves the serialized data to the file.
    */
  def saveFile(): Unit = {
    val writer = new BufferedWriter(new FileWriter(file))
    try {
      writer.write(DashboardExporter.colorToString(color))
      writer.newLine()
      writer.write(chartType)
      writer.newLine()
      serializeData().foreach { line =>
        writer.write(line)
        writer.newLine()
      }
    } finally {
      writer.close()
    }
  }
}

/** Utility object for parsing colors to string
  */
object DashboardExporter:
  /** Transforms the color to a string representation suitable for file storage.
    *
    * @param color
    *   The color to transform.
    * @return
    *   A string representation of the color.
    */
  def colorToString(color: Color): String =
    f"${color.red},${color.green},${color.blue}"
