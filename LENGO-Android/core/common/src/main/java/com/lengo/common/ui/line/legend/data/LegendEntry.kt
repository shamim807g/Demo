package com.lengo.common.ui.line.legend.data

import androidx.compose.ui.text.AnnotatedString
import com.lengo.common.ui.line.ChartShape

data class LegendEntry(
  val text: AnnotatedString,
  val value: Float? = null,
  val percent: Float = Float.MAX_VALUE,
  val shape: ChartShape = ChartShape.Default
)
