package com.lengo.common.extension

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.icu.text.Transliterator
import android.os.Build
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import kotlinx.coroutines.flow.Flow
import java.util.regex.Pattern


@Stable
class StableHolder<T>(val item: T) {
    operator fun component1(): T = item
}

@Immutable
class ImmutableHolder<T>(val item: T) {
    operator fun component1(): T = item
}


fun getStringByIdName(context: Context, idName: String?): String? {
    val res: Resources = context.resources
    return res.getString(res.getIdentifier(idName, "string", context.packageName))
}

fun getStringId(context: Context, idName: String?): Int? {
    val res: Resources = context.resources
    return res.getIdentifier(idName, "string", context.packageName)
}

fun String.removeLastChar(): String {
    if (!isNullOrEmpty()) {
        return substring(0, length - 1)
    }
    return ""
}

fun randomString(totalLength: Int): String {
    var AB = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
    var finalString = ""
    (0..totalLength).forEach {
        val randomIndex = (AB.indices).random()
        val char = AB[randomIndex]
        finalString += char
        AB = AB.replace(char.toString(),"")
    }
    return finalString
}

private val VerticalScrollConsumer = object : NestedScrollConnection {
    override fun onPreScroll(available: Offset, source: NestedScrollSource) = available.copy(x = 0f)
}

private val HorizontalScrollConsumer = object : NestedScrollConnection {
    override fun onPreScroll(available: Offset, source: NestedScrollSource) = available.copy(y = 0f)
}

fun Modifier.disableVerticalPointerInputScroll() = this.nestedScroll(VerticalScrollConsumer)

fun Modifier.disableHorizontalPointerInputScroll() = this.nestedScroll(HorizontalScrollConsumer)

@Immutable
object HexToJetpackColor {
    @Stable
    fun getColor(colorString: String): Color {
        return Color(android.graphics.Color.parseColor("#$colorString"))
    }
}

fun String.getAnglesIndexes(): Pair<Int?,Int?> {
    var firstAngleIndex: Int? = null
    var secondAngleIndex: Int? = null
    val charArray = this.toCharArray()
    charArray.forEachIndexed { index, c ->
        if(c == '<') {
            firstAngleIndex = index
        } else if(c == '>') {
            if(firstAngleIndex != null) {
                secondAngleIndex = index
            }
        }
    }
    return Pair(firstAngleIndex,secondAngleIndex)
}


fun String.extractBeforeAngleBracket(): String {
    val pair = this.getAnglesIndexes()
    val firstAngleIndex: Int? = pair.first
    return this.substring(0,firstAngleIndex!!)
}

fun String.extractBetweenAngleBracket(): String {
    val pair = this.getAnglesIndexes()
    val firstAngleIndex: Int? = pair.first
    val secondAngleIndex: Int? = pair.second
    return this.substring(firstAngleIndex!! + 1,secondAngleIndex!!)
}

fun String.extractAfterAngleBracket(): String {
    val pair = this.getAnglesIndexes()
    val secondAngleIndex: Int? = pair.second
    return this.substring(secondAngleIndex!! + 1,this.length)
}


fun String.replacePlaceholder(placeHolder: String): String {
    val pair = this.getAnglesIndexes()
    val firstAngleIndex: Int? = pair.first
    val secondAngleIndex: Int? = pair.second
    return this.replaceRange(firstAngleIndex!! + 1, secondAngleIndex!!, placeHolder)
}

fun Transliterator?.toTransliteratorString(text: String): String {
    return if(this != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !text.isEmpty()) {
        this.transliterate(text)
    } else {
        ""
    }
}

fun String.removeAngleBraces(): String {
    return this.replace("<","").replace(">","")
}

fun String.extractCoinsToBePurchaseBasedOnType(): Int {
    var coin = filter { it.isDigit() || it == '=' }.toInt()
    if (this.contains("Silver")) {
        coin = (coin * 100)
    } else if (this.contains("Gold")) {
        coin = (coin * 10000)
    }
    return coin
}

fun String.extractCoinsToDisplayBasedOnType(): Int {
    var coin = filter { it.isDigit() || it == '=' }.toInt()
    return coin
}

fun String.removePackageName(): String {
    return try {
        substring(0,indexOf("("))
    } catch(ex: Exception) {
        this
    }
}

@Immutable
data class Coins(
    val bronze: Int = 0,
    val gold: Int = 0,
    val silver: Int = 0,
)

val gold = 10000
val silver = 100

fun Int.bronzeToCoins(): Coins {
    var currentCoins = this
    var gold = 0
    var silver = 0
    if ((currentCoins / 10000) > 0) {
        gold += (currentCoins / 10000)
        currentCoins -= (gold * 10000)
    }
    if ((currentCoins / 100) > 0) {
        silver += (currentCoins / 100)
        currentCoins -= (silver * 100)
    }
    return Coins(currentCoins, gold, silver)
}

fun String.toAnnotatedString(color: Color = Color.Unspecified): AnnotatedString? {
    return try {
        val pattern = Pattern.compile("\\*\\*(.*?)\\*\\*")
        val matcher = pattern.matcher(this)
        val sb = StringBuffer()
        val stringBuilder = AnnotatedString.Builder()
        while (matcher.find()) {
            sb.setLength(0)
            val group = matcher.group()
            val spanText = group.substring(2, group.length - 2)
            matcher.appendReplacement(sb, spanText)
            stringBuilder.append(sb.toString())
            val start = stringBuilder.length - spanText.length
            stringBuilder.addStyle(SpanStyle(color = color),start = start,end = stringBuilder.length)
        }
        sb.setLength(0)
        matcher.appendTail(sb)
        stringBuilder.append(sb.toString())
        stringBuilder.toAnnotatedString()
    }catch (ex: Exception) {
        null
    }
}



@Composable
fun <T> rememberFlowWithLifecycle(
    flow: Flow<T>,
    lifecycle: Lifecycle = LocalLifecycleOwner.current.lifecycle,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED
): Flow<T> = remember(flow, lifecycle) {
    flow.flowWithLifecycle(
        lifecycle = lifecycle,
        minActiveState = minActiveState
    )
}


fun AppCompatActivity.closeKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun buildStackTraceString(elements: Array<StackTraceElement>?): String? {
    val sb = StringBuilder()
    if (elements != null && elements.size > 0) {
        for (element in elements) {
            sb.append(element.toString() + "\n")
        }
    }
    return sb.toString()
}

fun getStackTrace(): String? {
    return buildStackTraceString(Thread.currentThread().stackTrace)
}