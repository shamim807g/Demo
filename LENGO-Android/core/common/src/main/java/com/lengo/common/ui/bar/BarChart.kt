package com.lengo.common.ui.graph.bar

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import com.lengo.common.ui.graph.bar.BarChartUtils.axisAreas
import com.lengo.common.ui.graph.bar.BarChartUtils.barDrawableArea
import com.lengo.common.ui.graph.bar.BarChartUtils.forEachWithArea
import com.lengo.common.ui.bar.animation.simpleChartAnimation
import com.lengo.common.ui.bar.renderer.bar.BarDrawer
import com.lengo.common.ui.bar.renderer.bar.SimpleBarDrawer
import com.lengo.common.ui.bar.renderer.label.LabelDrawer
import com.lengo.common.ui.bar.renderer.label.SimpleValueDrawer
import com.lengo.common.ui.bar.renderer.xaxis.SimpleXAxisDrawer
import com.lengo.common.ui.bar.renderer.xaxis.XAxisDrawer
import com.lengo.common.ui.bar.renderer.yaxis.SimpleYAxisDrawer
import com.lengo.common.ui.bar.renderer.yaxis.YAxisDrawer

@Composable
fun BarChart(
  barChartData: BarChartData,
  modifier: Modifier = Modifier,
  animation: AnimationSpec<Float> = simpleChartAnimation(),
  barDrawer: BarDrawer = SimpleBarDrawer(),
  xAxisDrawer: XAxisDrawer = SimpleXAxisDrawer(),
  yAxisDrawer: YAxisDrawer = SimpleYAxisDrawer(),
  labelDrawer: LabelDrawer = SimpleValueDrawer()
) {
  val transitionAnimation = remember(barChartData.bars) { Animatable(initialValue = 0f) }

  LaunchedEffect(barChartData.bars) {
    transitionAnimation.animateTo(1f, animationSpec = animation)
  }

  val progress = transitionAnimation.value

  Canvas(modifier = modifier
    .fillMaxSize()
    .drawBehind {
      drawIntoCanvas { canvas ->
        val (xAxisArea, yAxisArea) = axisAreas(
          drawScope = this,
          totalSize = size,
          xAxisDrawer = xAxisDrawer,
          labelDrawer = labelDrawer
        )
        val barDrawableArea = barDrawableArea(xAxisArea)

        // Draw yAxis line.
//        yAxisDrawer.drawAxisLine(
//          drawScope = this,
//          canvas = canvas,
//          drawableArea = yAxisArea
//        )

        // Draw xAxis line.
//        xAxisDrawer.drawAxisLine(
//          drawScope = this,
//          canvas = canvas,
//          drawableArea = xAxisArea
//        )
        // Draw each bar.
        barChartData.forEachWithArea(
          this,
          barDrawableArea,
          progress,
          labelDrawer
        ) { barArea, bar ->
          barDrawer.drawBar(
            drawScope = this,
            canvas = canvas,
            barArea = barArea,
            bar = bar
          )
        }
      }
    }
  ) {
    /**
     *  Typically we could draw everything here, but because of the lack of canvas.drawText
     *  APIs we have to use Android's `nativeCanvas` which seems to be drawn behind
     *  Compose's canvas.
     */
    drawIntoCanvas { canvas ->
      val (xAxisArea, yAxisArea) = axisAreas(
        drawScope = this,
        totalSize = size,
        xAxisDrawer = xAxisDrawer,
        labelDrawer = labelDrawer
      )
      val barDrawableArea = barDrawableArea(xAxisArea)

      barChartData.forEachWithArea(
        this,
        barDrawableArea,
        progress,
        labelDrawer
      ) { barArea, bar ->

        labelDrawer.drawLabel(
          drawScope = this,
          canvas = canvas,
          labelMiddle = bar.labelMiddle,
          labelBottom = bar.labelBottom,
          barArea = barArea,
          xAxisArea = xAxisArea
        )
      }

//      yAxisDrawer.drawAxisLabels(
//        drawScope = this,
//        canvas = canvas,
//        minValue = barChartData.minYValue,
//        maxValue = barChartData.maxYValue,
//        drawableArea = yAxisArea
//      )
    }
  }
}