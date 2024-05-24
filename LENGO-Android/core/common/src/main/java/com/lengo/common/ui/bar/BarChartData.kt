package com.lengo.common.ui.graph.bar

data class BarChartData(
    val bars: List<Bar>,
    val padBy: Float = 10f,
    val startAtZero: Boolean = true
) {
  init {
    require(padBy in 0f..100f)
  }

  val maxBarValue = bars.maxByOrNull { it.value }?.value ?: 0f

  data class Bar(
    val value: Float,
    val labelMiddle: String,
    val labelBottom: String,
  )
}
