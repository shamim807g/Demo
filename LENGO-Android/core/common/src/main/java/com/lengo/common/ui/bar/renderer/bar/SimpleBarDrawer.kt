package com.lengo.common.ui.bar.renderer.bar

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.lengo.common.ui.graph.bar.BarChartData

class SimpleBarDrawer(val mColor: Color = Color(android.graphics.Color.WHITE)) : BarDrawer {
  private val barPaint = Paint().apply {
    this.isAntiAlias = true
  }

  override fun drawBar(
    drawScope: DrawScope,
    canvas: Canvas,
    barArea: Rect,
    bar: BarChartData.Bar
  ) {
    canvas.drawRoundRect(barArea.left,barArea.top,barArea.right,barArea.bottom,20f,20f, barPaint.apply {
      color = mColor
      //this.shader = LinearGradient( 0.0f, 0.0f,0.0f, 100f,Color.parseColor("#CCFFFFFF"), color, Shader.TileMode.MIRROR)
    })
  }
}