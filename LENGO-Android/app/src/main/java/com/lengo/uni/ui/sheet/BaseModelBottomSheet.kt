package com.lengo.uni.ui.sheet

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.lengo.common.extension.closeKeyboard
import com.lengo.modalsheet.ExperimentalSheetApi
import com.lengo.modalsheet.ModalSheet
import com.lengo.uni.ui.MainActivity


@OptIn(ExperimentalSheetApi::class)
@Composable
fun BaseModalSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    val activity = LocalContext.current as MainActivity
    ModalSheet(
        visible = visible,
        cancelable = true,
        shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
        elevation = 0.dp,
        backgroundColor = MaterialTheme.colors.secondary,
        scrimColor = Color.Black.copy(alpha = 0.65f),
        onVisibleChange = { if(!it) {
            activity.closeKeyboard()
            onDismiss() }
          },
        content = content
    )
}