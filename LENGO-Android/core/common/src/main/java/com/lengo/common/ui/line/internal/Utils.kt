package com.lengo.common.ui.line.internal

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.Dp
import com.lengo.common.ui.line.ChartShape
import com.lengo.common.ui.line.data.LineChartData
import com.lengo.common.ui.line.legend.data.LegendEntry
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

internal fun <T> List<T>.safeGet(idx: Int): T = when {
  idx in 0..lastIndex -> this[idx]
  idx > lastIndex -> this[idx - size]
  else -> error("Can't get a color at $idx")
}

internal const val DEG2RAD = Math.PI / 180.0
internal const val FDEG2RAD = Math.PI.toFloat() / 180f
internal val FLOAT_EPSILON = Float.fromBits(1)

fun LineChartData.createLegendEntries(
  shapeSize: Dp,
): List<LegendEntry> =
  series.map { item ->
    LegendEntry(
      text = AnnotatedString(item.title),
      shape = ChartShape(
        color = item.color,
        shape = legendShape,
        size = shapeSize,
      )
    )
  }

internal fun calculateMinimumRadiusForSpacedSlice(
  center: Offset,
  radius: Float,
  angle: Float,
  arcStartPointX: Float,
  arcStartPointY: Float,
  startAngle: Float,
  sweepAngle: Float
): Float {
  val angleMiddle = startAngle + sweepAngle / 2f

  // Other point of the arc
  val arcEndPointX: Float = center.x + radius * cos((startAngle + sweepAngle) * FDEG2RAD)
  val arcEndPointY: Float = center.y + radius * sin((startAngle + sweepAngle) * FDEG2RAD)

  // Middle point on the arc
  val arcMidPointX: Float = center.x + radius * cos(angleMiddle * FDEG2RAD)
  val arcMidPointY: Float = center.y + radius * sin(angleMiddle * FDEG2RAD)

  // This is the base of the contained triangle
  val basePointsDistance = sqrt(
    (arcEndPointX - arcStartPointX).toDouble().pow(2.0) +
      (arcEndPointY - arcStartPointY).toDouble().pow(2.0)
  )

  // After reducing space from both sides of the "slice",
  //   the angle of the contained triangle should stay the same.
  // So let's find out the height of that triangle.
  val containedTriangleHeight =
    (basePointsDistance / 2.0 * tan((180.0 - angle) / 2.0 * DEG2RAD)).toFloat()

  // Now we subtract that from the radius
  var spacedRadius = radius - containedTriangleHeight

  // And now subtract the height of the arc that's between the triangle and the outer circle
  spacedRadius -= sqrt(
    (arcMidPointX - (arcEndPointX + arcStartPointX) / 2f).toDouble().pow(2.0) +
      (arcMidPointY - (arcEndPointY + arcStartPointY) / 2f).toDouble().pow(2.0)
  ).toFloat()

  return spacedRadius
}
