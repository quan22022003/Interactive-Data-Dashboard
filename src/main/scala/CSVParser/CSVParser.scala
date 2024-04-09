package CSVParser

import java.io.BufferedReader

/** An abstract class designed for parsing CSV (Comma-Separated Values) files.
  * This class provides a framework for reading data from various sources into a
  * standardized format, specifically focusing on extracting headers and
  * numerical data from CSV files.
  */
abstract class CSVParser:
  /** Headers extracted from the CSV file, representing column names.
    */
  var headers: Seq[String] = Seq.empty

  /** Numerical data extracted from the CSV file, represented as a sequence of
    * sequences of floats. Each inner sequence corresponds to a row in the CSV
    * file, with each float representing a cell value.
    */
  var data: Seq[Seq[Float]] = Seq.empty

  /** Abstract method to read and process data from a specific source.
    * Implementations should handle the opening of the data source, call
    * `readBufferedReader` to process the data, and ensure any resources are
    * closed or released appropriately.
    *
    * @return
    *   Boolean indicating whether the file was successfully read and processed.
    */
  def readFile(): Boolean

  /** Helper method to process CSV data from a BufferedReader. Utilizes the
    * shared utility function `readLines` from the CSVParser companion object to
    * process lines into headers and numerical data.
    *
    * @param reader
    *   The BufferedReader to read from.
    * @return
    *   Boolean indicating whether the data was successfully processed.
    */
  def readBufferedReader(reader: BufferedReader): Boolean =
    try
      val (headersRead, dataRead) = CSVParser.readLines(reader)
      if headersRead.nonEmpty && dataRead.forall(_.length == headersRead.length)
      then
        headers = headersRead
        data = dataRead
        true
      else false
    finally reader.close()

/** Companion object for the CSVParser class, providing utility functions to
  * support CSV file parsing.
  */
object CSVParser:
  /** Static method to read and process lines from a BufferedReader into headers
    * and numerical data. The first line is read as the headers (column names),
    * and subsequent lines are processed as data rows. Each data row is split by
    * commas and converted into a sequence of floats.
    *
    * @param reader
    *   The BufferedReader to read from.
    * @return
    *   A tuple containing the headers (Seq[String]) and the data
    *   (Seq[Seq[Float]]).
    */
  def readLines(reader: BufferedReader): (Seq[String], Seq[Seq[Float]]) =
    val firstLine = Option(reader.readLine()).getOrElse("")
    val headers = firstLine.split(",").toSeq
    val dataBuffer = scala.collection.mutable.Buffer[Seq[Float]]()

    try
      var line = reader.readLine()
      while line != null do
        val lineData = line.trim.split(",").map(_.toFloat).toSeq
        dataBuffer.append(lineData)
        line = reader.readLine()
      (headers, dataBuffer.toSeq)
    catch case _: NumberFormatException => (Seq.empty, Seq.empty)
