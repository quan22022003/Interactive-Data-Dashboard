package Boxes

import scalafx.geometry.{Insets, Pos}
import scalafx.scene.layout.{Background, BackgroundFill, CornerRadii, VBox}
import scalafx.scene.paint.Color

/** * A visual container for diagrams/charts in the data dashboard. * Sets a
  * specific background and alignment for the diagrams area.
  */
class ChartBox extends VBox():
  this.setBackground(
    Background(
      Array(new BackgroundFill(Color.White, new CornerRadii(0), Insets(0)))
    )
  )
  this.setAlignment(Pos.Center)
