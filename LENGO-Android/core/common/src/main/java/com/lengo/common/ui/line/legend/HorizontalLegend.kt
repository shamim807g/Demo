package com.lengo.common.ui.line.legend

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import com.lengo.common.ui.line.internal.DefaultText
import com.lengo.common.ui.line.legend.data.LegendEntry

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HorizontalLegend(
  legendEntries: List<LegendEntry>,
  text: @Composable (item: LegendEntry) -> Unit = { DefaultText(text = it.text) },
) {
  FlowRow(
    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start),
    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
  ) {
    legendEntries.fastForEach { item ->
      key(item.hashCode()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .requiredSize(item.shape.size)
              .background(item.shape.color, item.shape.shape)
          )

          Spacer(modifier = Modifier.requiredSize(8.dp))

          text(item)
        }
      }
    }
  }
}
