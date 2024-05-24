package com.lengo.uni.ui.webpage

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.lengo.common.ui.ChildAppBar
import com.lengo.uni.ui.LocalNavigator

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebPage(urlToRender: String) {
    val controller = LocalNavigator.current

    Column(modifier = Modifier.fillMaxSize()) {
        ChildAppBar(title = "", onBack = { controller.popBackStack() })
        AndroidView(factory = {
            WebView(it).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                webViewClient = WebViewClient()
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                loadUrl(urlToRender)
            }
        }, modifier = Modifier.fillMaxSize(), update = {
            it.loadUrl(urlToRender)
        })
    }
}

@Preview
@Composable
fun WebPageDemo() {
    WebPage("aaaa")
}