package GraphDisplay

import scalafx.collections.ObservableBuffer
import scalafx.scene.chart.*

import scala.collection.mutable.Buffer

/** Creates a pie chart display from a collection of data cards. Similar to
  * BarChartDisplay, it aggregates data based on 'header', but represents it in
  * a pie chart format.
  */
class PieChartDisplay(chosenData: Buffer[Card]) extends ChartDisplay {
  override val chart: Option[PieChart] =
    Option.when(chosenData.nonEmpty) { // Proceeds only if data is available.
      // Prepare data by grouping by header and summing up the values.
      val pieChartData = ObservableBuffer(
        chosenData
          .groupBy(_.header)
          .view
          .mapValues(_.map(_.value).sum)
          .map { case (header, sum) => PieChart.Data(header, sum) }
          .toSeq*
      )
      val pieChart = new PieChart {
        data = pieChartData
        title = "New Pie Chart"
      }
      pieChart
    }
}
