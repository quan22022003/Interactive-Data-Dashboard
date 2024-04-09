package Boxes

import Tools.ChartTools
import scalafx.scene.layout.{ColumnConstraints, GridPane}

/** Represents a graphical box that contains a diagram and a toolbox, laid out
  * in a grid.
  *
  * @param chart
  *   A ChartBox instance representing the chart area.
  * @param toolBox
  *   A ChartTools instance representing the tools for interaction with the
  *   diagram.
  */
class ChartAndToolsBox(chart: ChartBox, toolBox: ChartTools) extends GridPane():

  // Apply the column constraints to the GridPane
  this.columnConstraints = Array(
    new ColumnConstraints {
      percentWidth = 80
    }, // Allocate 80% width to the chart
    new ColumnConstraints {
      percentWidth = 20
    } // Allocate 20% width to the toolbox
  )

  // Add the diagram and toolbox to the GridPane, specifying their positions and spans
  this.add(chart, columnIndex = 0, rowIndex = 0, colspan = 1, rowspan = 2)
  this.add(toolBox, columnIndex = 1, rowIndex = 0, colspan = 1, rowspan = 2)
