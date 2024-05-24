package com.lengo.common.ui.line.legend

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import com.lengo.common.ui.line.internal.DefaultText
import com.lengo.common.ui.line.legend.data.LegendEntry

@Composable
fun VerticalLegend(
  modifier: Modifier = Modifier,
  legendEntries: List<LegendEntry>,
  text: @Composable (entry: LegendEntry) -> Unit = {
    DefaultText(text = it.text)
  },
) {
  Column(
    modifier = modifier,
    verticalArrangement = Arrangement.Center
  ) {
    legendEntries.fastForEachIndexed { idx, item ->
      key("${idx}_${item.hashCode()}") {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .requiredSize(item.shape.size)
              .background(item.shape.color, item.shape.shape)
          )

          Spacer(modifier = Modifier.requiredSize(8.dp))

          text(item)
        }

        if (idx != legendEntries.lastIndex)
          Spacer(modifier = Modifier.requiredSize(8.dp))
      }
    }
  }
}
