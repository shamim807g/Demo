package com.lengo.uni.ui.bottomsheet

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lengo.common.ui.bottomsheet.BottomSheetWithCloseDialog
import com.lengo.uni.ui.MainActivity
import com.lengo.uni.ui.sheet.BaseModalSheet


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EmojiModelSheet(
    visible: Boolean,
    packId: Long,
    type: String,
    owner: Long,
    lang: String,
    onDismiss: (String) -> Unit
) {
    val activity = (LocalContext.current as MainActivity)
    val mainViewModel = activity.mainViewModel
    BaseModalSheet(visible = visible, onDismiss = {
        onDismiss("")
    }) {
        BottomSheetWithCloseDialog(onClosePressed = {
            onDismiss("")
        }) {
            EmojiSheet {
                onDismiss(it)
                mainViewModel.updatePackEmoji(packId,type,owner,lang, it)
            }
        }
    }
}


@ExperimentalFoundationApi
@Composable
private fun EmojiSheet(onEmojiSelected: (String) -> Unit) {
    Column(Modifier.fillMaxSize()) {
        LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 55.dp)) {
            items(emojiList.size) { index ->
                Text(
                    text = emojiList[index],
                    fontSize = 28.sp,
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable { onEmojiSelected(emojiList[index]) }
                )
            }
        }
    }
}

//https://www.piliapp.com/emoji/list/
val emojiList = listOf(
    "😀",
    "😁",
    "😂",
    "😃",
    "😄",
    "😅",
    "😆",
    "😇",
    "😈",
    "😉",
    "😊",
    "😋",
    "😌",
    "😍",
    "😎",
    "😏",
    "😐",
    "😑",
    "😒",
    "😓",
    "😔",
    "😕",
    "😖",
    "😗",
    "😘",
    "😙",
    "😚",
    "😛",
    "😜",
    "😝",
    "😞",
    "😟",
    "😠",
    "😡",
    "😢",
    "😣",
    "😤",
    "😥",
    "😦",
    "😧",
    "😨",
    "😩",
    "😪",
    "😫",
    "😬",
    "😭",
    "😮",
    "😯",
    "😰",
    "😱",
    "😲",
    "😳",
    "😴",
    "😵",
    "😶",
    "😷",
    "😸",
    "😹",
    "😺",
    "😻",
    "😼",
    "😽",
    "😾",
    "😿",
    "🙀",
    "🙁",
    "🙂",
    "🙃",
    "🙄",
    "🀄️",
    "🃏",
    "🅰️",
    "🅱️",
    "🅾️",
    "🅿️",
    "🆎",
    "🆑",
    "🆒",
    "🆓",
    "🆔",
    "🆕",
    "🆖",
    "🆗",
    "🆘",
    "🆙",
    "🆚",
    "🇦🇨",
    "🇦🇩",
    "🇦🇪",
    "🇦🇫",
    "🇦🇬",
    "🇦🇮",
    "🇦🇱",
    "🇦🇲",
    "🇦🇴",
    "🇦🇶",
    "🇦🇷",
    "🇦🇸",
    "🇦🇹",
    "🇦🇺",
    "🇦🇼",
    "🇦🇽",
    "🇦🇿",
    "🇦",
    "🇧🇦",
    "🇧🇧",
    "🇧🇩",
    "🇧🇪",
    "🇧🇫",
    "🇧🇬",
    "🇧🇭",
    "🇧🇮",
    "🇧🇯",
    "🇧🇱",
    "🇧🇲",
    "🇧🇳",
    "🇧🇴",
    "🇧🇶",
    "🇧🇷",
    "🇧🇸",
    "🇧🇹",
    "🇧🇻",
    "🇧🇼",
    "🇧🇾",
    "🇧🇿",
    "🇧",
    "🇨🇦",
    "🇨🇨",
    "🇨🇩",
    "🇨🇫",
    "🇨🇬",
    "🇨🇭",
    "🇨🇮",
    "🇨🇰",
    "🇨🇱",
    "🇨🇲",
    "🇨🇳",
    "🇨🇴",
    "🇨🇵",
    "🇨🇷",
    "🇨🇺",
    "🇨🇻",
    "🇨🇼",
    "🇨🇽",
    "🇨🇾",
    "🇨🇿",
    "🇨",
    "🇩🇪",
    "🇩🇬",
    "🇩🇯",
    "🇩🇰",
    "🇩🇲",
    "🇩🇴",
    "🇩🇿",
    "🇩",
    "🇪🇦",
    "🇪🇨",
    "🇪🇪",
    "🇪🇬",
    "🇪🇭",
    "🇪🇷",
    "🇪🇸",
    "🇪🇹",
    "🇪🇺",
    "🇪",
    "🇫🇮",
    "🇫🇯",
    "🇫🇰",
    "🇫🇲",
    "🇫🇴",
    "🇫🇷",
    "🇫",
    "🇬🇦",
    "🇬🇧",
    "🇬🇩",
    "🇬🇪",
    "🇬🇫",
    "🇬🇬",
    "🇬🇭",
    "🇬🇮",
    "🇬🇱",
    "🇬🇲",
    "🇬🇳",
    "🇬🇵",
    "🇬🇶",
    "🇬🇷",
    "🇬🇸",
    "🇬🇹",
    "🇬🇺",
    "🇬🇼",
    "🇬🇾",
    "🇬",
    "🇭🇰",
    "🇭🇲",
    "🇭🇳",
    "🇭🇷",
    "🇭🇹",
    "🇭🇺",
    "🇭",
    "🇮🇨",
    "🇮🇩",
    "🇮🇪",
    "🇮🇱",
    "🇮🇲",
    "🇮🇳",
    "🇮🇴",
    "🇮🇶",
    "🇮🇷",
    "🇮🇸",
    "🇮🇹",
    "🇮",
    "🇯🇪",
    "🇯🇲",
    "🇯🇴",
    "🇯🇵",
    "🇯",
    "🇰🇪",
    "🇰🇬",
    "🇰🇭",
    "🇰🇮",
    "🇰🇲",
    "🇰🇳",
    "🇰🇵",
    "🇰🇷",
    "🇰🇼",
    "🇰🇾",
    "🇰🇿",
    "🇰",
    "🇱🇦",
    "🇱🇧",
    "🇱🇨",
    "🇱🇮",
    "🇱🇰",
    "🇱🇷",
    "🇱🇸",
    "🇱🇹",
    "🇱🇺",
    "🇱🇻",
    "🇱🇾",
    "🇱",
    "🇲🇦",
    "🇲🇨",
    "🇲🇩",
    "🇲🇪",
    "🇲🇫",
    "🇲🇬",
    "🇲🇭",
    "🇲🇰",
    "🇲🇱",
    "🇲🇲",
    "🇲🇳",
    "🇲🇴",
    "🇲🇵",
    "🇲🇶",
    "🇲🇷",
    "🇲🇸",
    "🇲🇹",
    "🇲🇺",
    "🇲🇻",
    "🇲🇼",
    "🇲🇽",
    "🇲🇾",
    "🇲🇿",
    "🇲",
    "🇳🇦",
    "🇳🇨",
    "🇳🇪",
    "🇳🇫",
    "🇳🇬",
    "🇳🇮",
    "🇳🇱",
    "🇳🇴",
    "🇳🇵",
    "🇳🇷",
    "🇳🇺",
    "🇳🇿",
    "🇳",
    "🇴🇲",
    "🇴",
    "🇵🇦",
    "🇵🇪",
    "🇵🇫",
    "🇵🇬",
    "🇵🇭",
    "🇵🇰",
    "🇵🇱",
    "🇵🇲",
    "🇵🇳",
    "🇵🇷",
    "🇵🇸",
    "🇵🇹",
    "🇵🇼",
    "🇵🇾",
    "🇵",
    "🇶🇦",
    "🇶",
    "🇷🇪",
    "🇷🇴",
    "🇷🇸",
    "🇷🇺",
    "🇷🇼",
    "🇷",
    "🇸🇦",
    "🇸🇧",
    "🇸🇨",
    "🇸🇩",
    "🇸🇪",
    "🇸🇬",
    "🇸🇭",
    "🇸🇮",
    "🇸🇯",
    "🇸🇰",
    "🇸🇱",
    "🇸🇲",
    "🇸🇳",
    "🇸🇴",
    "🇸🇷",
    "🇸🇸",
    "🇸🇹",
    "🇸🇻",
    "🇸🇽",
    "🇸🇾",
    "🇸🇿",
    "🇸",
    "🇹🇦",
    "🇹🇨",
    "🇹🇩",
    "🇹🇫",
    "🇹🇬",
    "🇹🇭",
    "🇹🇯",
    "🇹🇰",
    "🇹🇱",
    "🇹🇲",
    "🇹🇳",
    "🇹🇴",
    "🇹🇷",
    "🇹🇹",
    "🇹🇻",
    "🇹🇼",
    "🇹🇿",
    "🇹",
    "🇺🇦",
    "🇺🇬",
    "🇺🇲",
    "🇺🇳",
    "🇺🇸",
    "🇺🇾",
    "🇺🇿",
    "🇺",
    "🇻🇦",
    "🇻🇨",
    "🇻🇪",
    "🇻🇬",
    "🇻🇮",
    "🇻🇳",
    "🇻🇺",
    "🇻",
    "🇼🇫",
    "🇼🇸",
    "🇼",
    "🇽🇰",
    "🇽",
    "🇾🇪",
    "🇾🇹",
    "🇾",
    "🇿🇦",
    "🇿🇲",
    "🇿🇼",
    "🇿",
    "🈁",
    "🈂️",
    "🈚️",
    "🈯️",
    "🈲",
    "🈳",
    "🈴",
    "🈵",
    "🈶",
    "🈷️",
    "🈸",
    "🈹",
    "🈺",
    "🉐",
    "🉑",
    "🌀",
    "🌁",
    "🌂",
    "🌃",
    "🌄",
    "🌅",
    "🌆",
    "🌇",
    "🌈",
    "🌉",
    "🌊",
    "🌋",
    "🌌",
    "🌍",
    "🌎",
    "🌏",
    "🌐",
    "🌑",
    "🌒",
    "🌓",
    "🌔",
    "🌕",
    "🌖",
    "🌗",
    "🌘",
    "🌙",
    "🌚",
    "🌛",
    "🌜",
    "🌝",
    "🌞",
    "🌟",
    "🌠",
    "🌡️",
    "🌤️",
    "🌥️",
    "🌦️",
    "🌧️",
    "🌨️",
    "🌩️",
    "🌪️",
    "🌫️",
    "🌬️",
    "🌭",
    "🌮",
    "🌯",
    "🌰",
    "🌱",
    "🌲",
    "🌳",
    "🌴",
    "🌵",
    "🌶️",
    "🌷",
    "🌸",
    "🌹",
    "🌺",
    "🌻",
    "🌼",
    "🌽",
    "🌾",
    "🌿",
    "🍀",
    "🍁",
    "🍂",
    "🍃",
    "🍄",
    "🍅",
    "🍆",
    "🍇",
    "🍈",
    "🍉",
    "🍊",
    "🍋",
    "🍌",
    "🍍",
    "🍎",
    "🍏",
    "🍐",
    "🍑",
    "🍒",
    "🍓",
    "🍔",
    "🍕",
    "🍖",
    "🍗",
    "🍘",
    "🍙",
    "🍚",
    "🍛",
    "🍜",
    "🍝",
    "🍞",
    "🍟",
    "🍠",
    "🍡",
    "🍢",
    "🍣",
    "🍤",
    "🍥",
    "🍦",
    "🍧",
    "🍨",
    "🍩",
    "🍪",
    "🍫",
    "🍬",
    "🍭",
    "🍮",
    "🍯",
    "🍰",
    "🍱",
    "🍲",
    "🍳",
    "🍴",
    "🍵",
    "🍶",
    "🍷",
    "🍸",
    "🍹",
    "🍺",
    "🍻",
    "🍼",
    "🍽️",
    "🍾",
    "🍿",
    "🎀",
    "🎁",
    "🎂",
    "🎃",
    "🎄",
    "🎅🏻",
    "🎅🏼",
    "🎅🏽",
    "🎅🏾",
    "🎅🏿",
    "🎅",
    "🎆",
    "🎇",
    "🎈",
    "🎉",
    "🎊",
    "🎋",
    "🎌",
    "🎍",
    "🎎",
    "🎏",
    "🎐",
    "🎑",
    "🎒",
    "🎓",
    "🎖️",
    "🎗️",
    "🎙️",
    "🎚️",
    "🎛️",
    "🎞️",
    "🎟️",
    "🎠",
    "🎡",
    "🎢",
    "🎣",
    "🎤",
    "🎥",
    "🎦",
    "🎧",
    "🎨",
    "🎩",
    "🎪",
    "🎫",
    "🎬",
    "🎭",
    "🎮",
    "🎯",
    "🎰",
    "🎱",
    "🎲",
    "🎳",
    "🎴",
    "🎵",
    "🎶",
    "🎷",
    "🎸",
    "🎹",
    "🎺",
    "🎻",
    "🎼",
    "🎽",
    "🎾",
    "🎿",
    "🏀",
    "🏁",
    "🏂🏻",
    "🏂🏼",
    "🏂🏽",
    "🏂🏾",
    "🏂🏿",
    "🏂",
    "🏃🏻‍♀️",
    "🏃🏻‍♂️",
    "🏃🏻",
    "🏃🏼‍♀️",
    "🏃🏼‍♂️",
    "🏃🏼",
    "🏃🏽‍♀️",
    "🏃🏽‍♂️",
    "🏃🏽",
    "🏃🏾‍♀️",
    "🏃🏾‍♂️",
    "🏃🏾",
    "🏃🏿‍♀️",
    "🏃🏿‍♂️",
    "🏃🏿",
    "🏃‍♀️",
    "🏃‍♂️",
    "🏃",
    "🏄🏻‍♀️",
    "🏄🏻‍♂️",
    "🏄🏻",
    "🏄🏼‍♀️",
    "🏄🏼‍♂️",
    "🏄🏼",
    "🏄🏽‍♀️",
    "🏄🏽‍♂️",
    "🏄🏽",
    "🏄🏾‍♀️",
    "🏄🏾‍♂️",
    "🏄🏾",
    "🏄🏿‍♀️",
    "🏄🏿‍♂️",
    "🏄🏿",
    "🏄‍♀️",
    "🏄‍♂️",
    "🏄",
    "🏅",
    "🏆",
    "🏇🏻",
    "🏇🏼",
    "🏇🏽",
    "🏇🏾",
    "🏇🏿",
    "🏇",
    "🏈",
    "🏉",
    "🏊🏻‍♀️",
    "🏊🏻‍♂️",
    "🏊🏻",
    "🏊🏼‍♀️",
    "🏊🏼‍♂️",
    "🏊🏼",
    "🏊🏽‍♀️",
    "🏊🏽‍♂️",
    "🏊🏽",
    "🏊🏾‍♀️",
    "🏊🏾‍♂️",
    "🏊🏾",
    "🏊🏿‍♀️",
    "🏊🏿‍♂️",
    "🏊🏿",
    "🏊‍♀️",
    "🏊‍♂️",
    "🏊",
    "🏋🏻‍♀️",
    "🏋🏻‍♂️",
    "🏋🏻",
    "🏋🏼‍♀️",
    "🏋🏼‍♂️",
    "🏋🏼",
    "🏋🏽‍♀️",
    "🏋🏽‍♂️",
    "🏋🏽",
    "🏋🏾‍♀️",
    "🏋🏾‍♂️",
    "🏋🏾",
    "🏋🏿‍♀️",
    "🏋🏿‍♂️",
    "🏋🏿",
    "🏋️‍♀️",
    "🏋️‍♂️",
    "🏋️",
    "🏌🏻‍♀️",
    "🏌🏻‍♂️",
    "🏌🏻",
    "🏌🏼‍♀️",
    "🏌🏼‍♂️",
    "🏌🏼",
    "🏌🏽‍♀️",
    "🏌🏽‍♂️",
    "🏌🏽",
    "🏌🏾‍♀️",
    "🏌🏾‍♂️",
    "🏌🏾",
    "🏌🏿‍♀️",
    "🏌🏿‍♂️",
    "🏌🏿",
    "🏌️‍♀️",
    "🏌️‍♂️",
    "🏌️",
    "🏍️",
    "🏎️",
    "🏏",
    "🏐",
    "🏑",
    "🏒",
    "🏓",
    "🏔️",
    "🏕️",
    "🏖️",
    "🏗️",
    "🏘️",
    "🏙️",
    "🏚️",
    "🏛️",
    "🏜️",
    "🏝️",
    "🏞️",
    "🏟️",
    "🏠",
    "🏡",
    "🏢",
    "🏣",
    "🏤",
    "🏥",
    "🏦",
    "🏧",
    "🏨",
    "🏩",
    "🏪",
    "🏫",
    "🏬",
    "🏭",
    "🏮",
    "🏯",
    "🏰",
    "🏳️‍🌈",
    "🏳️‍⚧️",
    "🏳️",
    "🏴‍☠️",
    "🏴󠁧󠁢󠁥󠁮󠁧󠁿",
    "🏴󠁧󠁢󠁳󠁣󠁴󠁿",
    "🏴󠁧󠁢󠁷󠁬󠁳󠁿",
    "🏴",
    "🏵️",
    "🏷️",
    "🏸",
    "🏹",
    "🏺",
    "🏻",
    "🏼",
    "🏽",
    "🏾",
    "🏿",
    "🐀",
    "🐁",
    "🐂",
    "🐃",
    "🐄",
    "🐅",
    "🐆",
    "🐇",
    "🐈‍⬛",
    "🐈",
    "🐉",
    "🐊",
    "🐋",
    "🐌",
    "🐍",
    "🐎",
    "🐏",
    "🐐",
    "🐑",
    "🐒",
    "🐓",
    "🐔",
    "🐕‍🦺",
    "🐕",
    "🐖",
    "🐗",
    "🐘",
    "🐙",
    "🐚",
    "🐛",
    "🐜",
    "🐝",
    "🐞",
    "🐟",
    "🐠",
    "🐡",
    "🐢",
    "🐣",
    "🐤",
    "🐥",
    "🐦",
    "🐧",
    "🐨",
    "🐩",
    "🐪",
    "🐫",
    "🐬",
    "🐭",
    "🐮",
    "🐯",
    "🐰",
    "🐱",
    "🐲",
    "🐳",
    "🐴",
    "🐵",
    "🐶",
    "🐷",
    "🐸",
    "🐹",
    "🐺",
    "🐻‍❄️",
    "🐻",
    "🐼",
    "🐽",
    "🐾",
    "🐿️",
    "👀",
    "👁‍🗨",
    "👁️",
    "👂🏻",
    "👂🏼",
    "👂🏽",
    "👂🏾",
    "👂🏿",
    "👂",
    "👃🏻",
    "👃🏼",
    "👃🏽",
    "👃🏾",
    "👃🏿",
    "👃",
    "👄",
    "👅",
    "👆🏻",
    "👆🏼",
    "👆🏽",
    "👆🏾",
    "👆🏿",
    "👆",
    "👇🏻",
    "👇🏼",
    "👇🏽",
    "👇🏾",
    "👇🏿",
    "👇",
    "👈🏻",
    "👈🏼",
    "👈🏽",
    "👈🏾",
    "👈🏿",
    "👈",
    "👉🏻",
    "👉🏼",
    "👉🏽",
    "👉🏾",
    "👉🏿",
    "👉",
    "👊🏻",
    "👊🏼",
    "👊🏽",
    "👊🏾",
    "👊🏿",
    "👊",
    "👋🏻",
    "👋🏼",
    "👋🏽",
    "👋🏾",
    "👋🏿",
    "👋",
    "👌🏻",
    "👌🏼",
    "👌🏽",
    "👌🏾",
    "👌🏿",
    "👌",
    "👍🏻",
    "👍🏼",
    "👍🏽",
    "👍🏾",
    "👍🏿",
    "👍",
    "👎🏻",
    "👎🏼",
    "👎🏽",
    "👎🏾",
    "👎🏿",
    "👎",
    "👏🏻",
    "👏🏼",
    "👏🏽",
    "👏🏾",
    "👏🏿",
    "👏",
    "👐🏻",
    "👐🏼",
    "👐🏽",
    "👐🏾",
    "👐🏿",
    "👐",
    "👑",
    "👒",
    "👓",
    "👔",
    "👕",
    "👖",
    "👗",
    "👘",
    "👙",
    "👚",
    "👛",
    "👜",
    "👝",
    "👞",
    "👟",
    "👠",
    "👡",
    "👢",
    "👣",
    "👤",
    "👥",
    "👦🏻",
    "👦🏼",
    "👦🏽",
    "👦🏾",
    "👦🏿",
    "👦",
    "👧🏻",
    "👧🏼",
    "👧🏽",
    "👧🏾",
    "👧🏿",
    "👧",
    "👨🏻‍🌾",
    "👨🏻‍🍳",
    "👨🏻‍🍼",
    "👨🏻‍🎄",
    "👨🏻‍🎓",
    "👨🏻‍🎤",
    "👨🏻‍🎨",
    "👨🏻‍🏫",
    "👨🏻‍🏭",
    "👨🏻‍💻",
    "👨🏻‍💼",
    "👨🏻‍🔧",
    "👨🏻‍🔬",
    "👨🏻‍🚀",
    "👨🏻‍🚒",
    "👨🏻‍🤝‍👨🏼",
    "👨🏻‍🤝‍👨🏽",
    "👨🏻‍🤝‍👨🏾",
    "👨🏻‍🤝‍👨🏿",
    "👨🏻‍🦯",
    "👨🏻‍🦰",
    "👨🏻‍🦱",
    "👨🏻‍🦲",
    "👨🏻‍🦳",
    "👨🏻‍🦼",
    "👨🏻‍🦽",
    "👨🏻‍⚕️",
    "👨🏻‍⚖️",
    "👨🏻‍✈️",
    "👨🏻",
    "👨🏼‍🌾",
    "👨🏼‍🍳",
    "👨🏼‍🍼",
    "👨🏼‍🎄",
    "👨🏼‍🎓",
    "👨🏼‍🎤",
    "👨🏼‍🎨",
    "👨🏼‍🏫",
    "👨🏼‍🏭",
    "👨🏼‍💻",
    "👨🏼‍💼",
    "👨🏼‍🔧",
    "👨🏼‍🔬",
    "👨🏼‍🚀",
    "👨🏼‍🚒",
    "👨🏼‍🤝‍👨🏻",
    "👨🏼‍🤝‍👨🏽",
    "👨🏼‍🤝‍👨🏾",
    "👨🏼‍🤝‍👨🏿",
    "👨🏼‍🦯",
    "👨🏼‍🦰",
    "👨🏼‍🦱",
    "👨🏼‍🦲",
    "👨🏼‍🦳",
    "👨🏼‍🦼",
    "👨🏼‍🦽",
    "👨🏼‍⚕️",
    "👨🏼‍⚖️",
    "👨🏼‍✈️",
    "👨🏼",
    "👨🏽‍🌾",
    "👨🏽‍🍳",
    "👨🏽‍🍼",
    "👨🏽‍🎄",
    "👨🏽‍🎓",
    "👨🏽‍🎤",
    "👨🏽‍🎨",
    "👨🏽‍🏫",
    "👨🏽‍🏭",
    "👨🏽‍💻",
    "👨🏽‍💼",
    "👨🏽‍🔧",
    "👨🏽‍🔬",
    "👨🏽‍🚀",
    "👨🏽‍🚒",
    "👨🏽‍🤝‍👨🏻",
    "👨🏽‍🤝‍👨🏼",
    "👨🏽‍🤝‍👨🏾",
    "👨🏽‍🤝‍👨🏿",
    "👨🏽‍🦯",
    "👨🏽‍🦰",
    "👨🏽‍🦱",
    "👨🏽‍🦲",
    "👨🏽‍🦳",
    "👨🏽‍🦼",
    "👨🏽‍🦽",
    "👨🏽‍⚕️",
    "👨🏽‍⚖️",
    "👨🏽‍✈️",
    "👨🏽",
    "👨🏾‍🌾",
    "👨🏾‍🍳",
    "👨🏾‍🍼",
    "👨🏾‍🎄",
    "👨🏾‍🎓",
    "👨🏾‍🎤",
    "👨🏾‍🎨",
    "👨🏾‍🏫",
    "👨🏾‍🏭",
    "👨🏾‍💻",
    "👨🏾‍💼",
    "👨🏾‍🔧",
    "👨🏾‍🔬",
    "👨🏾‍🚀",
    "👨🏾‍🚒",
    "👨🏾‍🤝‍👨🏻",
    "👨🏾‍🤝‍👨🏼",
    "👨🏾‍🤝‍👨🏽",
    "👨🏾‍🤝‍👨🏿",
    "👨🏾‍🦯",
    "👨🏾‍🦰",
    "👨🏾‍🦱",
    "👨🏾‍🦲",
    "👨🏾‍🦳",
    "👨🏾‍🦼",
    "👨🏾‍🦽",
    "👨🏾‍⚕️",
    "👨🏾‍⚖️",
    "👨🏾‍✈️",
    "👨🏾",
    "👨🏿‍🌾",
    "👨🏿‍🍳",
    "👨🏿‍🍼",
    "👨🏿‍🎄",
    "👨🏿‍🎓",
    "👨🏿‍🎤",
    "👨🏿‍🎨",
    "👨🏿‍🏫",
    "👨🏿‍🏭",
    "👨🏿‍💻",
    "👨🏿‍💼",
    "👨🏿‍🔧",
    "👨🏿‍🔬",
    "👨🏿‍🚀",
    "👨🏿‍🚒",
    "👨🏿‍🤝‍👨🏻",
    "👨🏿‍🤝‍👨🏼",
    "👨🏿‍🤝‍👨🏽",
    "👨🏿‍🤝‍👨🏾",
    "👨🏿‍🦯",
    "👨🏿‍🦰",
    "👨🏿‍🦱",
    "👨🏿‍🦲",
    "👨🏿‍🦳",
    "👨🏿‍🦼",
    "👨🏿‍🦽",
    "👨🏿‍⚕️",
    "👨🏿‍⚖️",
    "👨🏿‍✈️",
    "👨🏿",
    "👨‍🌾",
    "👨‍🍳",
    "👨‍🍼",
    "👨‍🎄",
    "👨‍🎓",
    "👨‍🎤",
    "👨‍🎨",
    "👨‍🏫",
    "👨‍🏭",
    "👨‍👦‍👦",
    "👨‍👦",
    "👨‍👧‍👦",
    "👨‍👧‍👧",
    "👨‍👧",
    "👨‍👨‍👦‍👦",
    "👨‍👨‍👦",
    "👨‍👨‍👧‍👦",
    "👨‍👨‍👧‍👧",
    "👨‍👨‍👧",
    "👨‍👩‍👦‍👦",
    "👨‍👩‍👦",
    "👨‍👩‍👧‍👦",
    "👨‍👩‍👧‍👧",
    "👨‍👩‍👧",
    "👨‍💻",
    "👨‍💼",
    "👨‍🔧",
    "👨‍🔬",
    "👨‍🚀",
    "👨‍🚒",
    "👨‍🦯",
    "👨‍🦰",
    "👨‍🦱",
    "👨‍🦲",
    "👨‍🦳",
    "👨‍🦼",
    "👨‍🦽",
    "👨‍⚕️",
    "👨‍⚖️",
    "👨‍✈️",
    "👨‍❤️‍👨",
    "👨‍❤️‍💋‍👨",
    "👨",
    "👩🏻‍🌾",
    "👩🏻‍🍳",
    "👩🏻‍🍼",
    "👩🏻‍🎄",
    "👩🏻‍🎓",
    "👩🏻‍🎤",
    "👩🏻‍🎨",
    "👩🏻‍🏫",
    "👩🏻‍🏭",
    "👩🏻‍💻",
    "👩🏻‍💼",
    "👩🏻‍🔧",
    "👩🏻‍🔬",
    "👩🏻‍🚀",
    "👩🏻‍🚒",
    "👩🏻‍🤝‍👨🏼",
    "👩🏻‍🤝‍👨🏽",
    "👩🏻‍🤝‍👨🏾",
    "👩🏻‍🤝‍👨🏿",
    "👩🏻‍🤝‍👩🏼",
    "👩🏻‍🤝‍👩🏽",
    "👩🏻‍🤝‍👩🏾",
    "👩🏻‍🤝‍👩🏿",
    "👩🏻‍🦯",
    "👩🏻‍🦰",
    "👩🏻‍🦱",
    "👩🏻‍🦲",
    "👩🏻‍🦳",
    "👩🏻‍🦼",
    "👩🏻‍🦽",
    "👩🏻‍⚕️",
    "👩🏻‍⚖️",
    "👩🏻‍✈️",
    "👩🏻",
    "👩🏼‍🌾",
    "👩🏼‍🍳",
    "👩🏼‍🍼",
    "👩🏼‍🎄",
    "👩🏼‍🎓",
    "👩🏼‍🎤",
    "👩🏼‍🎨",
    "👩🏼‍🏫",
    "👩🏼‍🏭",
    "👩🏼‍💻",
    "👩🏼‍💼",
    "👩🏼‍🔧",
    "👩🏼‍🔬",
    "👩🏼‍🚀",
    "👩🏼‍🚒",
    "👩🏼‍🤝‍👨🏻",
    "👩🏼‍🤝‍👨🏽",
    "👩🏼‍🤝‍👨🏾",
    "👩🏼‍🤝‍👨🏿",
    "👩🏼‍🤝‍👩🏻",
    "👩🏼‍🤝‍👩🏽",
    "👩🏼‍🤝‍👩🏾",
    "👩🏼‍🤝‍👩🏿",
    "👩🏼‍🦯",
    "👩🏼‍🦰",
    "👩🏼‍🦱",
    "👩🏼‍🦲",
    "👩🏼‍🦳",
    "👩🏼‍🦼",
    "👩🏼‍🦽",
    "👩🏼‍⚕️",
    "👩🏼‍⚖️",
    "👩🏼‍✈️",
    "👩🏼",
    "👩🏽‍🌾",
    "👩🏽‍🍳",
    "👩🏽‍🍼",
    "👩🏽‍🎄",
    "👩🏽‍🎓",
    "👩🏽‍🎤",
    "👩🏽‍🎨",
    "👩🏽‍🏫",
    "👩🏽‍🏭",
    "👩🏽‍💻",
    "👩🏽‍💼",
    "👩🏽‍🔧",
    "👩🏽‍🔬",
    "👩🏽‍🚀",
    "👩🏽‍🚒",
    "👩🏽‍🤝‍👨🏻",
    "👩🏽‍🤝‍👨🏼",
    "👩🏽‍🤝‍👨🏾",
    "👩🏽‍🤝‍👨🏿",
    "👩🏽‍🤝‍👩🏻",
    "👩🏽‍🤝‍👩🏼",
    "👩🏽‍🤝‍👩🏾",
    "👩🏽‍🤝‍👩🏿",
    "👩🏽‍🦯",
    "👩🏽‍🦰",
    "👩🏽‍🦱",
    "👩🏽‍🦲",
    "👩🏽‍🦳",
    "👩🏽‍🦼",
    "👩🏽‍🦽",
    "👩🏽‍⚕️",
    "👩🏽‍⚖️",
    "👩🏽‍✈️",
    "👩🏽",
    "👩🏾‍🌾",
    "👩🏾‍🍳",
    "👩🏾‍🍼",
    "👩🏾‍🎄",
    "👩🏾‍🎓",
    "👩🏾‍🎤",
    "👩🏾‍🎨",
    "👩🏾‍🏫",
    "👩🏾‍🏭",
    "👩🏾‍💻",
    "👩🏾‍💼",
    "👩🏾‍🔧",
    "👩🏾‍🔬",
    "👩🏾‍🚀",
    "👩🏾‍🚒",
    "👩🏾‍🤝‍👨🏻",
    "👩🏾‍🤝‍👨🏼",
    "👩🏾‍🤝‍👨🏽",
    "👩🏾‍🤝‍👨🏿",
    "👩🏾‍🤝‍👩🏻",
    "👩🏾‍🤝‍👩🏼",
    "👩🏾‍🤝‍👩🏽",
    "👩🏾‍🤝‍👩🏿",
    "👩🏾‍🦯",
    "👩🏾‍🦰",
    "👩🏾‍🦱",
    "👩🏾‍🦲",
    "👩🏾‍🦳",
    "👩🏾‍🦼",
    "👩🏾‍🦽",
    "👩🏾‍⚕️",
    "👩🏾‍⚖️",
    "👩🏾‍✈️",
    "👩🏾",
    "👩🏿‍🌾",
    "👩🏿‍🍳",
    "👩🏿‍🍼",
    "👩🏿‍🎄",
    "👩🏿‍🎓",
    "👩🏿‍🎤",
    "👩🏿‍🎨",
    "👩🏿‍🏫",
    "👩🏿‍🏭",
    "👩🏿‍💻",
    "👩🏿‍💼",
    "👩🏿‍🔧",
    "👩🏿‍🔬",
    "👩🏿‍🚀",
    "👩🏿‍🚒",
    "👩🏿‍🤝‍👨🏻",
    "👩🏿‍🤝‍👨🏼",
    "👩🏿‍🤝‍👨🏽",
    "👩🏿‍🤝‍👨🏾",
    "👩🏿‍🤝‍👩🏻",
    "👩🏿‍🤝‍👩🏼",
    "👩🏿‍🤝‍👩🏽",
    "👩🏿‍🤝‍👩🏾",
    "👩🏿‍🦯",
    "👩🏿‍🦰",
    "👩🏿‍🦱",
    "👩🏿‍🦲",
    "👩🏿‍🦳",
    "👩🏿‍🦼",
    "👩🏿‍🦽",
    "👩🏿‍⚕️",
    "👩🏿‍⚖️",
    "👩🏿‍✈️",
    "👩🏿",
    "👩‍🌾",
    "👩‍🍳",
    "👩‍🍼",
    "👩‍🎄",
    "👩‍🎓",
    "👩‍🎤",
    "👩‍🎨",
    "👩‍🏫",
    "👩‍🏭",
    "👩‍👦‍👦",
    "👩‍👦",
    "👩‍👧‍👦",
    "👩‍👧‍👧",
    "👩‍👧",
    "👩‍👩‍👦‍👦",
    "👩‍👩‍👦",
    "👩‍👩‍👧‍👦",
    "👩‍👩‍👧‍👧",
    "👩‍👩‍👧",
    "👩‍💻",
    "👩‍💼",
    "👩‍🔧",
    "👩‍🔬",
    "👩‍🚀",
    "👩‍🚒",
    "👩‍🦯",
    "👩‍🦰",
    "👩‍🦱",
    "👩‍🦲",
    "👩‍🦳",
    "👩‍🦼",
    "👩‍🦽",
    "👩‍⚕️",
    "👩‍⚖️",
    "👩‍✈️",
    "👩‍❤️‍👨",
    "👩‍❤️‍👩",
    "👩‍❤️‍💋‍👨",
    "👩‍❤️‍💋‍👩",
    "👩",
    "👪",
    "👫🏻",
    "👫🏼",
    "👫🏽",
    "👫🏾",
    "👫🏿",
    "👫",
    "👬🏻",
    "👬🏼",
    "👬🏽",
    "👬🏾",
    "👬🏿",
    "👬",
    "👭🏻",
    "👭🏼",
    "👭🏽",
    "👭🏾",
    "👭🏿",
    "👭",
    "👮🏻‍♀️",
    "👮🏻‍♂️",
    "👮🏻",
    "👮🏼‍♀️",
    "👮🏼‍♂️",
    "👮🏼",
    "👮🏽‍♀️",
    "👮🏽‍♂️",
    "👮🏽",
    "👮🏾‍♀️",
    "👮🏾‍♂️",
    "👮🏾",
    "👮🏿‍♀️",
    "👮🏿‍♂️",
    "👮🏿",
    "👮‍♀️",
    "👮‍♂️",
    "👮",
    "👯‍♀️",
    "👯‍♂️",
    "👯",
    "👰🏻‍♀️",
    "👰🏻‍♂️",
    "👰🏻",
    "👰🏼‍♀️",
    "👰🏼‍♂️",
    "👰🏼",
    "👰🏽‍♀️",
    "👰🏽‍♂️",
    "👰🏽",
    "👰🏾‍♀️",
    "👰🏾‍♂️",
    "👰🏾",
    "👰🏿‍♀️",
    "👰🏿‍♂️",
    "👰🏿",
    "👰‍♀️",
    "👰‍♂️",
    "👰",
    "👱🏻‍♀️",
    "👱🏻‍♂️",
    "👱🏻",
    "👱🏼‍♀️",
    "👱🏼‍♂️",
    "👱🏼",
    "👱🏽‍♀️",
    "👱🏽‍♂️",
    "👱🏽",
    "👱🏾‍♀️",
    "👱🏾‍♂️",
    "👱🏾",
    "👱🏿‍♀️",
    "👱🏿‍♂️",
    "👱🏿",
    "👱‍♀️",
    "👱‍♂️",
    "👱",
    "👲🏻",
    "👲🏼",
    "👲🏽",
    "👲🏾",
    "👲🏿",
    "👲",
    "👳🏻‍♀️",
    "👳🏻‍♂️",
    "👳🏻",
    "👳🏼‍♀️",
    "👳🏼‍♂️",
    "👳🏼",
    "👳🏽‍♀️",
    "👳🏽‍♂️",
    "👳🏽",
    "👳🏾‍♀️",
    "👳🏾‍♂️",
    "👳🏾",
    "👳🏿‍♀️",
    "👳🏿‍♂️",
    "👳🏿",
    "👳‍♀️",
    "👳‍♂️",
    "👳",
    "👴🏻",
    "👴🏼",
    "👴🏽",
    "👴🏾",
    "👴🏿",
    "👴",
    "👵🏻",
    "👵🏼",
    "👵🏽",
    "👵🏾",
    "👵🏿",
    "👵",
    "👶🏻",
    "👶🏼",
    "👶🏽",
    "👶🏾",
    "👶🏿",
    "👶",
    "👷🏻‍♀️",
    "👷🏻‍♂️",
    "👷🏻",
    "👷🏼‍♀️",
    "👷🏼‍♂️",
    "👷🏼",
    "👷🏽‍♀️",
    "👷🏽‍♂️",
    "👷🏽",
    "👷🏾‍♀️",
    "👷🏾‍♂️",
    "👷🏾",
    "👷🏿‍♀️",
    "👷🏿‍♂️",
    "👷🏿",
    "👷‍♀️",
    "👷‍♂️",
    "👷",
    "👸🏻",
    "👸🏼",
    "👸🏽",
    "👸🏾",
    "👸🏿",
    "👸",
    "👹",
    "👺",
    "👻",
    "👼🏻",
    "👼🏼",
    "👼🏽",
    "👼🏾",
    "👼🏿",
    "👼",
    "👽",
    "👾",
    "👿",
    "💀",
    "💁🏻‍♀️",
    "💁🏻‍♂️",
    "💁🏻",
    "💁🏼‍♀️",
    "💁🏼‍♂️",
    "💁🏼",
    "💁🏽‍♀️",
    "💁🏽‍♂️",
    "💁🏽",
    "💁🏾‍♀️",
    "💁🏾‍♂️",
    "💁🏾",
    "💁🏿‍♀️",
    "💁🏿‍♂️",
    "💁🏿",
    "💁‍♀️",
    "💁‍♂️",
    "💁",
    "💂🏻‍♀️",
    "💂🏻‍♂️",
    "💂🏻",
    "💂🏼‍♀️",
    "💂🏼‍♂️",
    "💂🏼",
    "💂🏽‍♀️",
    "💂🏽‍♂️",
    "💂🏽",
    "💂🏾‍♀️",
    "💂🏾‍♂️",
    "💂🏾",
    "💂🏿‍♀️",
    "💂🏿‍♂️",
    "💂🏿",
    "💂‍♀️",
    "💂‍♂️",
    "💂",
    "💃🏻",
    "💃🏼",
    "💃🏽",
    "💃🏾",
    "💃🏿",
    "💃",
    "💄",
    "💅🏻",
    "💅🏼",
    "💅🏽",
    "💅🏾",
    "💅🏿",
    "💅",
    "💆🏻‍♀️",
    "💆🏻‍♂️",
    "💆🏻",
    "💆🏼‍♀️",
    "💆🏼‍♂️",
    "💆🏼",
    "💆🏽‍♀️",
    "💆🏽‍♂️",
    "💆🏽",
    "💆🏾‍♀️",
    "💆🏾‍♂️",
    "💆🏾",
    "💆🏿‍♀️",
    "💆🏿‍♂️",
    "💆🏿",
    "💆‍♀️",
    "💆‍♂️",
    "💆",
    "💇🏻‍♀️",
    "💇🏻‍♂️",
    "💇🏻",
    "💇🏼‍♀️",
    "💇🏼‍♂️",
    "💇🏼",
    "💇🏽‍♀️",
    "💇🏽‍♂️",
    "💇🏽",
    "💇🏾‍♀️",
    "💇🏾‍♂️",
    "💇🏾",
    "💇🏿‍♀️",
    "💇🏿‍♂️",
    "💇🏿",
    "💇‍♀️",
    "💇‍♂️",
    "💇",
    "💈",
    "💉",
    "💊",
    "💋",
    "💌",
    "💍",
    "💎",
    "💏",
    "💐",
    "💑",
    "💒",
    "💓",
    "💔",
    "💕",
    "💖",
    "💗",
    "💘",
    "💙",
    "💚",
    "💛",
    "💜",
    "💝",
    "💞",
    "💟",
    "💠",
    "💡",
    "💢",
    "💣",
    "💤",
    "💥",
    "💦",
    "💧",
    "💨",
    "💩",
    "💪🏻",
    "💪🏼",
    "💪🏽",
    "💪🏾",
    "💪🏿",
    "💪",
    "💫",
    "💬",
    "💭",
    "💮",
    "💯",
    "💰",
    "💱",
    "💲",
    "💳",
    "💴",
    "💵",
    "💶",
    "💷",
    "💸",
    "💹",
    "💺",
    "💻",
    "💼",
    "💽",
    "💾",
    "💿",
    "📀",
    "📁",
    "📂",
    "📃",
    "📄",
    "📅",
    "📆",
    "📇",
    "📈",
    "📉",
    "📊",
    "📋",
    "📌",
    "📍",
    "📎",
    "📏",
    "📐",
    "📑",
    "📒",
    "📓",
    "📔",
    "📕",
    "📖",
    "📗",
    "📘",
    "📙",
    "📚",
    "📛",
    "📜",
    "📝",
    "📞",
    "📟",
    "📠",
    "📡",
    "📢",
    "📣",
    "📤",
    "📥",
    "📦",
    "📧",
    "📨",
    "📩",
    "📪",
    "📫",
    "📬",
    "📭",
    "📮",
    "📯",
    "📰",
    "📱",
    "📲",
    "📳",
    "📴",
    "📵",
    "📶",
    "📷",
    "📸",
    "📹",
    "📺",
    "📻",
    "📼",
    "📽️",
    "📿",
    "🔀",
    "🔁",
    "🔂",
    "🔃",
    "🔄",
    "🔅",
    "🔆",
    "🔇",
    "🔈",
    "🔉",
    "🔊",
    "🔋",
    "🔌",
    "🔍",
    "🔎",
    "🔏",
    "🔐",
    "🔑",
    "🔒",
    "🔓",
    "🔔",
    "🔕",
    "🔖",
    "🔗",
    "🔘",
    "🔙",
    "🔚",
    "🔛",
    "🔜",
    "🔝",
    "🔞",
    "🔟",
    "🔠",
    "🔡",
    "🔢",
    "🔣",
    "🔤",
    "🔥",
    "🔦",
    "🔧",
    "🔨",
    "🔩",
    "🔪",
    "🔫",
    "🔬",
    "🔭",
    "🔮",
    "🔯",
    "🔰",
    "🔱",
    "🔲",
    "🔳",
    "🔴",
    "🔵",
    "🔶",
    "🔷",
    "🔸",
    "🔹",
    "🔺",
    "🔻",
    "🔼",
    "🔽",
    "🕉️",
    "🕊️",
    "🕋",
    "🕌",
    "🕍",
    "🕎",
    "🕐",
    "🕑",
    "🕒",
    "🕓",
    "🕔",
    "🕕",
    "🕖",
    "🕗",
    "🕘",
    "🕙",
    "🕚",
    "🕛",
    "🕜",
    "🕝",
    "🕞",
    "🕟",
    "🕠",
    "🕡",
    "🕢",
    "🕣",
    "🕤",
    "🕥",
    "🕦",
    "🕧",
    "🕯️",
    "🕰️",
    "🕳️",
    "🕴🏻‍♀️",
    "🕴🏻‍♂️",
    "🕴🏻",
    "🕴🏼‍♀️",
    "🕴🏼‍♂️",
    "🕴🏼",
    "🕴🏽‍♀️",
    "🕴🏽‍♂️",
    "🕴🏽",
    "🕴🏾‍♀️",
    "🕴🏾‍♂️",
    "🕴🏾",
    "🕴🏿‍♀️",
    "🕴🏿‍♂️",
    "🕴🏿",
    "🕴️‍♀️",
    "🕴️‍♂️",
    "🕴️",
    "🕵🏻‍♀️",
    "🕵🏻‍♂️",
    "🕵🏻",
    "🕵🏼‍♀️",
    "🕵🏼‍♂️",
    "🕵🏼",
    "🕵🏽‍♀️",
    "🕵🏽‍♂️",
    "🕵🏽",
    "🕵🏾‍♀️",
    "🕵🏾‍♂️",
    "🕵🏾",
    "🕵🏿‍♀️",
    "🕵🏿‍♂️",
    "🕵🏿",
    "🕵️‍♀️",
    "🕵️‍♂️",
    "🕵️",
    "🕶️",
    "🕷️",
    "🕸️",
    "🕹️",
    "🕺🏻",
    "🕺🏼",
    "🕺🏽",
    "🕺🏾",
    "🕺🏿",
    "🕺",
    "🖇️",
    "🖊️",
    "🖋️",
    "🖌️",
    "🖍️",
    "🖐🏻",
    "🖐🏼",
    "🖐🏽",
    "🖐🏾",
    "🖐🏿",
    "🖐️",
    "🖕🏻",
    "🖕🏼",
    "🖕🏽",
    "🖕🏾",
    "🖕🏿",
    "🖕",
    "🖖🏻",
    "🖖🏼",
    "🖖🏽",
    "🖖🏾",
    "🖖🏿",
    "🖖",
    "🖤",
    "🖥️",
    "🖨️",
    "🖱️",
    "🖲️",
    "🖼️",
    "🗂️",
    "🗃️",
    "🗄️",
    "🗑️",
    "🗒️",
    "🗓️",
    "🗜️",
    "🗝️",
    "🗞️",
    "🗡️",
    "🗣️",
    "🗨️",
    "🗯️",
    "🗳️",
    "🗺️",
    "🗻",
    "🗼",
    "🗽",
    "🗾",
    "🗿",
    "🙅🏻‍♀️",
    "🙅🏻‍♂️",
    "🙅🏻",
    "🙅🏼‍♀️",
    "🙅🏼‍♂️",
    "🙅🏼",
    "🙅🏽‍♀️",
    "🙅🏽‍♂️",
    "🙅🏽",
    "🙅🏾‍♀️",
    "🙅🏾‍♂️",
    "🙅🏾",
    "🙅🏿‍♀️",
    "🙅🏿‍♂️",
    "🙅🏿",
    "🙅‍♀️",
    "🙅‍♂️",
    "🙅",
    "🙆🏻‍♀️",
    "🙆🏻‍♂️",
    "🙆🏻",
    "🙆🏼‍♀️",
    "🙆🏼‍♂️",
    "🙆🏼",
    "🙆🏽‍♀️",
    "🙆🏽‍♂️",
    "🙆🏽",
    "🙆🏾‍♀️",
    "🙆🏾‍♂️",
    "🙆🏾",
    "🙆🏿‍♀️",
    "🙆🏿‍♂️",
    "🙆🏿",
    "🙆‍♀️",
    "🙆‍♂️",
    "🙆",
    "🙇🏻‍♀️",
    "🙇🏻‍♂️",
    "🙇🏻",
    "🙇🏼‍♀️",
    "🙇🏼‍♂️",
    "🙇🏼",
    "🙇🏽‍♀️",
    "🙇🏽‍♂️",
    "🙇🏽",
    "🙇🏾‍♀️",
    "🙇🏾‍♂️",
    "🙇🏾",
    "🙇🏿‍♀️",
    "🙇🏿‍♂️",
    "🙇🏿",
    "🙇‍♀️",
    "🙇‍♂️",
    "🙇",
    "🙈",
    "🙉",
    "🙊",
    "🙋🏻‍♀️",
    "🙋🏻‍♂️",
    "🙋🏻",
    "🙋🏼‍♀️",
    "🙋🏼‍♂️",
    "🙋🏼",
    "🙋🏽‍♀️",
    "🙋🏽‍♂️",
    "🙋🏽",
    "🙋🏾‍♀️",
    "🙋🏾‍♂️",
    "🙋🏾",
    "🙋🏿‍♀️",
    "🙋🏿‍♂️",
    "🙋🏿",
    "🙋‍♀️",
    "🙋‍♂️",
    "🙋",
    "🙌🏻",
    "🙌🏼",
    "🙌🏽",
    "🙌🏾",
    "🙌🏿",
    "🙌",
    "🙍🏻‍♀️",
    "🙍🏻‍♂️",
    "🙍🏻",
    "🙍🏼‍♀️",
    "🙍🏼‍♂️",
    "🙍🏼",
    "🙍🏽‍♀️",
    "🙍🏽‍♂️",
    "🙍🏽",
    "🙍🏾‍♀️",
    "🙍🏾‍♂️",
    "🙍🏾",
    "🙍🏿‍♀️",
    "🙍🏿‍♂️",
    "🙍🏿",
    "🙍‍♀️",
    "🙍‍♂️",
    "🙍",
    "🙎🏻‍♀️",
    "🙎🏻‍♂️",
    "🙎🏻",
    "🙎🏼‍♀️",
    "🙎🏼‍♂️",
    "🙎🏼",
    "🙎🏽‍♀️",
    "🙎🏽‍♂️",
    "🙎🏽",
    "🙎🏾‍♀️",
    "🙎🏾‍♂️",
    "🙎🏾",
    "🙎🏿‍♀️",
    "🙎🏿‍♂️",
    "🙎🏿",
    "🙎‍♀️",
    "🙎‍♂️",
    "🙎",
    "🙏🏻",
    "🙏🏼",
    "🙏🏽",
    "🙏🏾",
    "🙏🏿",
    "🙏",
    "🚀",
    "🚁",
    "🚂",
    "🚃",
    "🚄",
    "🚅",
    "🚆",
    "🚇",
    "🚈",
    "🚉",
    "🚊",
    "🚋",
    "🚌",
    "🚍",
    "🚎",
    "🚏",
    "🚐",
    "🚑",
    "🚒",
    "🚓",
    "🚔",
    "🚕",
    "🚖",
    "🚗",
    "🚘",
    "🚙",
    "🚚",
    "🚛",
    "🚜",
    "🚝",
    "🚞",
    "🚟",
    "🚠",
    "🚡",
    "🚢",
    "🚣🏻‍♀️",
    "🚣🏻‍♂️",
    "🚣🏻",
    "🚣🏼‍♀️",
    "🚣🏼‍♂️",
    "🚣🏼",
    "🚣🏽‍♀️",
    "🚣🏽‍♂️",
    "🚣🏽",
    "🚣🏾‍♀️",
    "🚣🏾‍♂️",
    "🚣🏾",
    "🚣🏿‍♀️",
    "🚣🏿‍♂️",
    "🚣🏿",
    "🚣‍♀️",
    "🚣‍♂️",
    "🚣",
    "🚤",
    "🚥",
    "🚦",
    "🚧",
    "🚨",
    "🚩",
    "🚪",
    "🚫",
    "🚬",
    "🚭",
    "🚮",
    "🚯",
    "🚰",
    "🚱",
    "🚲",
    "🚳",
    "🚴🏻‍♀️",
    "🚴🏻‍♂️",
    "🚴🏻",
    "🚴🏼‍♀️",
    "🚴🏼‍♂️",
    "🚴🏼",
    "🚴🏽‍♀️",
    "🚴🏽‍♂️",
    "🚴🏽",
    "🚴🏾‍♀️",
    "🚴🏾‍♂️",
    "🚴🏾",
    "🚴🏿‍♀️",
    "🚴🏿‍♂️",
    "🚴🏿",
    "🚴‍♀️",
    "🚴‍♂️",
    "🚴",
    "🚵🏻‍♀️",
    "🚵🏻‍♂️",
    "🚵🏻",
    "🚵🏼‍♀️",
    "🚵🏼‍♂️",
    "🚵🏼",
    "🚵🏽‍♀️",
    "🚵🏽‍♂️",
    "🚵🏽",
    "🚵🏾‍♀️",
    "🚵🏾‍♂️",
    "🚵🏾",
    "🚵🏿‍♀️",
    "🚵🏿‍♂️",
    "🚵🏿",
    "🚵‍♀️",
    "🚵‍♂️",
    "🚵",
    "🚶🏻‍♀️",
    "🚶🏻‍♂️",
    "🚶🏻",
    "🚶🏼‍♀️",
    "🚶🏼‍♂️",
    "🚶🏼",
    "🚶🏽‍♀️",
    "🚶🏽‍♂️",
    "🚶🏽",
    "🚶🏾‍♀️",
    "🚶🏾‍♂️",
    "🚶🏾",
    "🚶🏿‍♀️",
    "🚶🏿‍♂️",
    "🚶🏿",
    "🚶‍♀️",
    "🚶‍♂️",
    "🚶",
    "🚷",
    "🚸",
    "🚹",
    "🚺",
    "🚻",
    "🚼",
    "🚽",
    "🚾",
    "🚿",
    "🛀🏻",
    "🛀🏼",
    "🛀🏽",
    "🛀🏾",
    "🛀🏿",
    "🛀",
    "🛁",
    "🛂",
    "🛃",
    "🛄",
    "🛅",
    "🛋️",
    "🛌🏻",
    "🛌🏼",
    "🛌🏽",
    "🛌🏾",
    "🛌🏿",
    "🛌",
    "🛍️",
    "🛎️",
    "🛏️",
    "🛐",
    "🛑",
    "🛒",
    "🛕",
    "🛖",
    "🛗",
    "🛠️",
    "🛡️",
    "🛢️",
    "🛣️",
    "🛤️",
    "🛥️",
    "🛩️",
    "🛫",
    "🛬",
    "🛰️",
    "🛳️",
    "🛴",
    "🛵",
    "🛶",
    "🛷",
    "🛸",
    "🛹",
    "🛺",
    "🛻",
    "🛼",
    "🟠",
    "🟡",
    "🟢",
    "🟣",
    "🟤",
    "🟥",
    "🟦",
    "🟧",
    "🟨",
    "🟩",
    "🟪",
    "🟫",
    "🤌🏻",
    "🤌🏼",
    "🤌🏽",
    "🤌🏾",
    "🤌🏿",
    "🤌",
    "🤍",
    "🤎",
    "🤏🏻",
    "🤏🏼",
    "🤏🏽",
    "🤏🏾",
    "🤏🏿",
    "🤏",
    "🤐",
    "🤑",
    "🤒",
    "🤓",
    "🤔",
    "🤕",
    "🤖",
    "🤗",
    "🤘🏻",
    "🤘🏼",
    "🤘🏽",
    "🤘🏾",
    "🤘🏿",
    "🤘",
    "🤙🏻",
    "🤙🏼",
    "🤙🏽",
    "🤙🏾",
    "🤙🏿",
    "🤙",
    "🤚🏻",
    "🤚🏼",
    "🤚🏽",
    "🤚🏾",
    "🤚🏿",
    "🤚",
    "🤛🏻",
    "🤛🏼",
    "🤛🏽",
    "🤛🏾",
    "🤛🏿",
    "🤛",
    "🤜🏻",
    "🤜🏼",
    "🤜🏽",
    "🤜🏾",
    "🤜🏿",
    "🤜",
    "🤝",
    "🤞🏻",
    "🤞🏼",
    "🤞🏽",
    "🤞🏾",
    "🤞🏿",
    "🤞",
    "🤟🏻",
    "🤟🏼",
    "🤟🏽",
    "🤟🏾",
    "🤟🏿",
    "🤟",
    "🤠",
    "🤡",
    "🤢",
    "🤣",
    "🤤",
    "🤥",
    "🤦🏻‍♀️",
    "🤦🏻‍♂️",
    "🤦🏻",
    "🤦🏼‍♀️",
    "🤦🏼‍♂️",
    "🤦🏼",
    "🤦🏽‍♀️",
    "🤦🏽‍♂️",
    "🤦🏽",
    "🤦🏾‍♀️",
    "🤦🏾‍♂️",
    "🤦🏾",
    "🤦🏿‍♀️",
    "🤦🏿‍♂️",
    "🤦🏿",
    "🤦‍♀️",
    "🤦‍♂️",
    "🤦",
    "🤧",
    "🤨",
    "🤩",
    "🤪",
    "🤫",
    "🤬",
    "🤭",
    "🤮",
    "🤯",
    "🤰🏻",
    "🤰🏼",
    "🤰🏽",
    "🤰🏾",
    "🤰🏿",
    "🤰",
    "🤱🏻",
    "🤱🏼",
    "🤱🏽",
    "🤱🏾",
    "🤱🏿",
    "🤱",
    "🤲🏻",
    "🤲🏼",
    "🤲🏽",
    "🤲🏾",
    "🤲🏿",
    "🤲",
    "🤳🏻",
    "🤳🏼",
    "🤳🏽",
    "🤳🏾",
    "🤳🏿",
    "🤳",
    "🤴🏻",
    "🤴🏼",
    "🤴🏽",
    "🤴🏾",
    "🤴🏿",
    "🤴",
    "🤵🏻‍♀️",
    "🤵🏻‍♂️",
    "🤵🏻",
    "🤵🏼‍♀️",
    "🤵🏼‍♂️",
    "🤵🏼",
    "🤵🏽‍♀️",
    "🤵🏽‍♂️",
    "🤵🏽",
    "🤵🏾‍♀️",
    "🤵🏾‍♂️",
    "🤵🏾",
    "🤵🏿‍♀️",
    "🤵🏿‍♂️",
    "🤵🏿",
    "🤵‍♀️",
    "🤵‍♂️",
    "🤵",
    "🤶🏻",
    "🤶🏼",
    "🤶🏽",
    "🤶🏾",
    "🤶🏿",
    "🤶",
    "🤷🏻‍♀️",
    "🤷🏻‍♂️",
    "🤷🏻",
    "🤷🏼‍♀️",
    "🤷🏼‍♂️",
    "🤷🏼",
    "🤷🏽‍♀️",
    "🤷🏽‍♂️",
    "🤷🏽",
    "🤷🏾‍♀️",
    "🤷🏾‍♂️",
    "🤷🏾",
    "🤷🏿‍♀️",
    "🤷🏿‍♂️",
    "🤷🏿",
    "🤷‍♀️",
    "🤷‍♂️",
    "🤷",
    "🤸🏻‍♀️",
    "🤸🏻‍♂️",
    "🤸🏻",
    "🤸🏼‍♀️",
    "🤸🏼‍♂️",
    "🤸🏼",
    "🤸🏽‍♀️",
    "🤸🏽‍♂️",
    "🤸🏽",
    "🤸🏾‍♀️",
    "🤸🏾‍♂️",
    "🤸🏾",
    "🤸🏿‍♀️",
    "🤸🏿‍♂️",
    "🤸🏿",
    "🤸‍♀️",
    "🤸‍♂️",
    "🤸",
    "🤹🏻‍♀️",
    "🤹🏻‍♂️",
    "🤹🏻",
    "🤹🏼‍♀️",
    "🤹🏼‍♂️",
    "🤹🏼",
    "🤹🏽‍♀️",
    "🤹🏽‍♂️",
    "🤹🏽",
    "🤹🏾‍♀️",
    "🤹🏾‍♂️",
    "🤹🏾",
    "🤹🏿‍♀️",
    "🤹🏿‍♂️",
    "🤹🏿",
    "🤹‍♀️",
    "🤹‍♂️",
    "🤹",
    "🤺",
    "🤼‍♀️",
    "🤼‍♂️",
    "🤼",
    "🤽🏻‍♀️",
    "🤽🏻‍♂️",
    "🤽🏻",
    "🤽🏼‍♀️",
    "🤽🏼‍♂️",
    "🤽🏼",
    "🤽🏽‍♀️",
    "🤽🏽‍♂️",
    "🤽🏽",
    "🤽🏾‍♀️",
    "🤽🏾‍♂️",
    "🤽🏾",
    "🤽🏿‍♀️",
    "🤽🏿‍♂️",
    "🤽🏿",
    "🤽‍♀️",
    "🤽‍♂️",
    "🤽",
    "🤾🏻‍♀️",
    "🤾🏻‍♂️",
    "🤾🏻",
    "🤾🏼‍♀️",
    "🤾🏼‍♂️",
    "🤾🏼",
    "🤾🏽‍♀️",
    "🤾🏽‍♂️",
    "🤾🏽",
    "🤾🏾‍♀️",
    "🤾🏾‍♂️",
    "🤾🏾",
    "🤾🏿‍♀️",
    "🤾🏿‍♂️",
    "🤾🏿",
    "🤾‍♀️",
    "🤾‍♂️",
    "🤾",
    "🤿",
    "🥀",
    "🥁",
    "🥂",
    "🥃",
    "🥄",
    "🥅",
    "🥇",
    "🥈",
    "🥉",
    "🥊",
    "🥋",
    "🥌",
    "🥍",
    "🥎",
    "🥏",
    "🥐",
    "🥑",
    "🥒",
    "🥓",
    "🥔",
    "🥕",
    "🥖",
    "🥗",
    "🥘",
    "🥙",
    "🥚",
    "🥛",
    "🥜",
    "🥝",
    "🥞",
    "🥟",
    "🥠",
    "🥡",
    "🥢",
    "🥣",
    "🥤",
    "🥥",
    "🥦",
    "🥧",
    "🥨",
    "🥩",
    "🥪",
    "🥫",
    "🥬",
    "🥭",
    "🥮",
    "🥯",
    "🥰",
    "🥱",
    "🥲",
    "🥳",
    "🥴",
    "🥵",
    "🥶",
    "🥷🏻",
    "🥷🏼",
    "🥷🏽",
    "🥷🏾",
    "🥷🏿",
    "🥷",
    "🥸",
    "🥺",
    "🥻",
    "🥼",
    "🥽",
    "🥾",
    "🥿",
    "🦀",
    "🦁",
    "🦂",
    "🦃",
    "🦄",
    "🦅",
    "🦆",
    "🦇",
    "🦈",
    "🦉",
    "🦊",
    "🦋",
    "🦌",
    "🦍",
    "🦎",
    "🦏",
    "🦐",
    "🦑",
    "🦒",
    "🦓",
    "🦔",
    "🦕",
    "🦖",
    "🦗",
    "🦘",
    "🦙",
    "🦚",
    "🦛",
    "🦜",
    "🦝",
    "🦞",
    "🦟",
    "🦠",
    "🦡",
    "🦢",
    "🦣",
    "🦤",
    "🦥",
    "🦦",
    "🦧",
    "🦨",
    "🦩",
    "🦪",
    "🦫",
    "🦬",
    "🦭",
    "🦮",
    "🦯",
    "🦰",
    "🦱",
    "🦲",
    "🦳",
    "🦴",
    "🦵🏻",
    "🦵🏼",
    "🦵🏽",
    "🦵🏾",
    "🦵🏿",
    "🦵",
    "🦶🏻",
    "🦶🏼",
    "🦶🏽",
    "🦶🏾",
    "🦶🏿",
    "🦶",
    "🦷",
    "🦸🏻‍♀️",
    "🦸🏻‍♂️",
    "🦸🏻",
    "🦸🏼‍♀️",
    "🦸🏼‍♂️",
    "🦸🏼",
    "🦸🏽‍♀️",
    "🦸🏽‍♂️",
    "🦸🏽",
    "🦸🏾‍♀️",
    "🦸🏾‍♂️",
    "🦸🏾",
    "🦸🏿‍♀️",
    "🦸🏿‍♂️",
    "🦸🏿",
    "🦸‍♀️",
    "🦸‍♂️",
    "🦸",
    "🦹🏻‍♀️",
    "🦹🏻‍♂️",
    "🦹🏻",
    "🦹🏼‍♀️",
    "🦹🏼‍♂️",
    "🦹🏼",
    "🦹🏽‍♀️",
    "🦹🏽‍♂️",
    "🦹🏽",
    "🦹🏾‍♀️",
    "🦹🏾‍♂️",
    "🦹🏾",
    "🦹🏿‍♀️",
    "🦹🏿‍♂️",
    "🦹🏿",
    "🦹‍♀️",
    "🦹‍♂️",
    "🦹",
    "🦺",
    "🦻🏻",
    "🦻🏼",
    "🦻🏽",
    "🦻🏾",
    "🦻🏿",
    "🦻",
    "🦼",
    "🦽",
    "🦾",
    "🦿",
    "🧀",
    "🧁",
    "🧂",
    "🧃",
    "🧄",
    "🧅",
    "🧆",
    "🧇",
    "🧈",
    "🧉",
    "🧊",
    "🧋",
    "🧍🏻‍♀️",
    "🧍🏻‍♂️",
    "🧍🏻",
    "🧍🏼‍♀️",
    "🧍🏼‍♂️",
    "🧍🏼",
    "🧍🏽‍♀️",
    "🧍🏽‍♂️",
    "🧍🏽",
    "🧍🏾‍♀️",
    "🧍🏾‍♂️",
    "🧍🏾",
    "🧍🏿‍♀️",
    "🧍🏿‍♂️",
    "🧍🏿",
    "🧍‍♀️",
    "🧍‍♂️",
    "🧍",
    "🧎🏻‍♀️",
    "🧎🏻‍♂️",
    "🧎🏻",
    "🧎🏼‍♀️",
    "🧎🏼‍♂️",
    "🧎🏼",
    "🧎🏽‍♀️",
    "🧎🏽‍♂️",
    "🧎🏽",
    "🧎🏾‍♀️",
    "🧎🏾‍♂️",
    "🧎🏾",
    "🧎🏿‍♀️",
    "🧎🏿‍♂️",
    "🧎🏿",
    "🧎‍♀️",
    "🧎‍♂️",
    "🧎",
    "🧏🏻‍♀️",
    "🧏🏻‍♂️",
    "🧏🏻",
    "🧏🏼‍♀️",
    "🧏🏼‍♂️",
    "🧏🏼",
    "🧏🏽‍♀️",
    "🧏🏽‍♂️",
    "🧏🏽",
    "🧏🏾‍♀️",
    "🧏🏾‍♂️",
    "🧏🏾",
    "🧏🏿‍♀️",
    "🧏🏿‍♂️",
    "🧏🏿",
    "🧏‍♀️",
    "🧏‍♂️",
    "🧏",
    "🧐",
    "🧑🏻‍🌾",
    "🧑🏻‍🍳",
    "🧑🏻‍🍼",
    "🧑🏻‍🎄",
    "🧑🏻‍🎓",
    "🧑🏻‍🎤",
    "🧑🏻‍🎨",
    "🧑🏻‍🏫",
    "🧑🏻‍🏭",
    "🧑🏻‍💻",
    "🧑🏻‍💼",
    "🧑🏻‍🔧",
    "🧑🏻‍🔬",
    "🧑🏻‍🚀",
    "🧑🏻‍🚒",
    "🧑🏻‍🤝‍🧑🏻",
    "🧑🏻‍🤝‍🧑🏼",
    "🧑🏻‍🤝‍🧑🏽",
    "🧑🏻‍🤝‍🧑🏾",
    "🧑🏻‍🤝‍🧑🏿",
    "🧑🏻‍🦯",
    "🧑🏻‍🦰",
    "🧑🏻‍🦱",
    "🧑🏻‍🦲",
    "🧑🏻‍🦳",
    "🧑🏻‍🦼",
    "🧑🏻‍🦽",
    "🧑🏻‍⚕️",
    "🧑🏻‍⚖️",
    "🧑🏻‍✈️",
    "🧑🏻",
    "🧑🏼‍🌾",
    "🧑🏼‍🍳",
    "🧑🏼‍🍼",
    "🧑🏼‍🎄",
    "🧑🏼‍🎓",
    "🧑🏼‍🎤",
    "🧑🏼‍🎨",
    "🧑🏼‍🏫",
    "🧑🏼‍🏭",
    "🧑🏼‍💻",
    "🧑🏼‍💼",
    "🧑🏼‍🔧",
    "🧑🏼‍🔬",
    "🧑🏼‍🚀",
    "🧑🏼‍🚒",
    "🧑🏼‍🤝‍🧑🏻",
    "🧑🏼‍🤝‍🧑🏼",
    "🧑🏼‍🤝‍🧑🏽",
    "🧑🏼‍🤝‍🧑🏾",
    "🧑🏼‍🤝‍🧑🏿",
    "🧑🏼‍🦯",
    "🧑🏼‍🦰",
    "🧑🏼‍🦱",
    "🧑🏼‍🦲",
    "🧑🏼‍🦳",
    "🧑🏼‍🦼",
    "🧑🏼‍🦽",
    "🧑🏼‍⚕️",
    "🧑🏼‍⚖️",
    "🧑🏼‍✈️",
    "🧑🏼",
    "🧑🏽‍🌾",
    "🧑🏽‍🍳",
    "🧑🏽‍🍼",
    "🧑🏽‍🎄",
    "🧑🏽‍🎓",
    "🧑🏽‍🎤",
    "🧑🏽‍🎨",
    "🧑🏽‍🏫",
    "🧑🏽‍🏭",
    "🧑🏽‍💻",
    "🧑🏽‍💼",
    "🧑🏽‍🔧",
    "🧑🏽‍🔬",
    "🧑🏽‍🚀",
    "🧑🏽‍🚒",
    "🧑🏽‍🤝‍🧑🏻",
    "🧑🏽‍🤝‍🧑🏼",
    "🧑🏽‍🤝‍🧑🏽",
    "🧑🏽‍🤝‍🧑🏾",
    "🧑🏽‍🤝‍🧑🏿",
    "🧑🏽‍🦯",
    "🧑🏽‍🦰",
    "🧑🏽‍🦱",
    "🧑🏽‍🦲",
    "🧑🏽‍🦳",
    "🧑🏽‍🦼",
    "🧑🏽‍🦽",
    "🧑🏽‍⚕️",
    "🧑🏽‍⚖️",
    "🧑🏽‍✈️",
    "🧑🏽",
    "🧑🏾‍🌾",
    "🧑🏾‍🍳",
    "🧑🏾‍🍼",
    "🧑🏾‍🎄",
    "🧑🏾‍🎓",
    "🧑🏾‍🎤",
    "🧑🏾‍🎨",
    "🧑🏾‍🏫",
    "🧑🏾‍🏭",
    "🧑🏾‍💻",
    "🧑🏾‍💼",
    "🧑🏾‍🔧",
    "🧑🏾‍🔬",
    "🧑🏾‍🚀",
    "🧑🏾‍🚒",
    "🧑🏾‍🤝‍🧑🏻",
    "🧑🏾‍🤝‍🧑🏼",
    "🧑🏾‍🤝‍🧑🏽",
    "🧑🏾‍🤝‍🧑🏾",
    "🧑🏾‍🤝‍🧑🏿",
    "🧑🏾‍🦯",
    "🧑🏾‍🦰",
    "🧑🏾‍🦱",
    "🧑🏾‍🦲",
    "🧑🏾‍🦳",
    "🧑🏾‍🦼",
    "🧑🏾‍🦽",
    "🧑🏾‍⚕️",
    "🧑🏾‍⚖️",
    "🧑🏾‍✈️",
    "🧑🏾",
    "🧑🏿‍🌾",
    "🧑🏿‍🍳",
    "🧑🏿‍🍼",
    "🧑🏿‍🎄",
    "🧑🏿‍🎓",
    "🧑🏿‍🎤",
    "🧑🏿‍🎨",
    "🧑🏿‍🏫",
    "🧑🏿‍🏭",
    "🧑🏿‍💻",
    "🧑🏿‍💼",
    "🧑🏿‍🔧",
    "🧑🏿‍🔬",
    "🧑🏿‍🚀",
    "🧑🏿‍🚒",
    "🧑🏿‍🤝‍🧑🏻",
    "🧑🏿‍🤝‍🧑🏼",
    "🧑🏿‍🤝‍🧑🏽",
    "🧑🏿‍🤝‍🧑🏾",
    "🧑🏿‍🤝‍🧑🏿",
    "🧑🏿‍🦯",
    "🧑🏿‍🦰",
    "🧑🏿‍🦱",
    "🧑🏿‍🦲",
    "🧑🏿‍🦳",
    "🧑🏿‍🦼",
    "🧑🏿‍🦽",
    "🧑🏿‍⚕️",
    "🧑🏿‍⚖️",
    "🧑🏿‍✈️",
    "🧑🏿",
    "🧑‍🌾",
    "🧑‍🍳",
    "🧑‍🍼",
    "🧑‍🎄",
    "🧑‍🎓",
    "🧑‍🎤",
    "🧑‍🎨",
    "🧑‍🏫",
    "🧑‍🏭",
    "🧑‍💻",
    "🧑‍💼",
    "🧑‍🔧",
    "🧑‍🔬",
    "🧑‍🚀",
    "🧑‍🚒",
    "🧑‍🤝‍🧑",
    "🧑‍🦯",
    "🧑‍🦰",
    "🧑‍🦱",
    "🧑‍🦲",
    "🧑‍🦳",
    "🧑‍🦼",
    "🧑‍🦽",
    "🧑‍⚕️",
    "🧑‍⚖️",
    "🧑‍✈️",
    "🧑",
    "🧒🏻",
    "🧒🏼",
    "🧒🏽",
    "🧒🏾",
    "🧒🏿",
    "🧒",
    "🧓🏻",
    "🧓🏼",
    "🧓🏽",
    "🧓🏾",
    "🧓🏿",
    "🧓",
    "🧔🏻",
    "🧔🏼",
    "🧔🏽",
    "🧔🏾",
    "🧔🏿",
    "🧔",
    "🧕🏻",
    "🧕🏼",
    "🧕🏽",
    "🧕🏾",
    "🧕🏿",
    "🧕",
    "🧖🏻‍♀️",
    "🧖🏻‍♂️",
    "🧖🏻",
    "🧖🏼‍♀️",
    "🧖🏼‍♂️",
    "🧖🏼",
    "🧖🏽‍♀️",
    "🧖🏽‍♂️",
    "🧖🏽",
    "🧖🏾‍♀️",
    "🧖🏾‍♂️",
    "🧖🏾",
    "🧖🏿‍♀️",
    "🧖🏿‍♂️",
    "🧖🏿",
    "🧖‍♀️",
    "🧖‍♂️",
    "🧖",
    "🧗🏻‍♀️",
    "🧗🏻‍♂️",
    "🧗🏻",
    "🧗🏼‍♀️",
    "🧗🏼‍♂️",
    "🧗🏼",
    "🧗🏽‍♀️",
    "🧗🏽‍♂️",
    "🧗🏽",
    "🧗🏾‍♀️",
    "🧗🏾‍♂️",
    "🧗🏾",
    "🧗🏿‍♀️",
    "🧗🏿‍♂️",
    "🧗🏿",
    "🧗‍♀️",
    "🧗‍♂️",
    "🧗",
    "🧘🏻‍♀️",
    "🧘🏻‍♂️",
    "🧘🏻",
    "🧘🏼‍♀️",
    "🧘🏼‍♂️",
    "🧘🏼",
    "🧘🏽‍♀️",
    "🧘🏽‍♂️",
    "🧘🏽",
    "🧘🏾‍♀️",
    "🧘🏾‍♂️",
    "🧘🏾",
    "🧘🏿‍♀️",
    "🧘🏿‍♂️",
    "🧘🏿",
    "🧘‍♀️",
    "🧘‍♂️",
    "🧘",
    "🧙🏻‍♀️",
    "🧙🏻‍♂️",
    "🧙🏻",
    "🧙🏼‍♀️",
    "🧙🏼‍♂️",
    "🧙🏼",
    "🧙🏽‍♀️",
    "🧙🏽‍♂️",
    "🧙🏽",
    "🧙🏾‍♀️",
    "🧙🏾‍♂️",
    "🧙🏾",
    "🧙🏿‍♀️",
    "🧙🏿‍♂️",
    "🧙🏿",
    "🧙‍♀️",
    "🧙‍♂️",
    "🧙",
    "🧚🏻‍♀️",
    "🧚🏻‍♂️",
    "🧚🏻",
    "🧚🏼‍♀️",
    "🧚🏼‍♂️",
    "🧚🏼",
    "🧚🏽‍♀️",
    "🧚🏽‍♂️",
    "🧚🏽",
    "🧚🏾‍♀️",
    "🧚🏾‍♂️",
    "🧚🏾",
    "🧚🏿‍♀️",
    "🧚🏿‍♂️",
    "🧚🏿",
    "🧚‍♀️",
    "🧚‍♂️",
    "🧚",
    "🧛🏻‍♀️",
    "🧛🏻‍♂️",
    "🧛🏻",
    "🧛🏼‍♀️",
    "🧛🏼‍♂️",
    "🧛🏼",
    "🧛🏽‍♀️",
    "🧛🏽‍♂️",
    "🧛🏽",
    "🧛🏾‍♀️",
    "🧛🏾‍♂️",
    "🧛🏾",
    "🧛🏿‍♀️",
    "🧛🏿‍♂️",
    "🧛🏿",
    "🧛‍♀️",
    "🧛‍♂️",
    "🧛",
    "🧜🏻‍♀️",
    "🧜🏻‍♂️",
    "🧜🏻",
    "🧜🏼‍♀️",
    "🧜🏼‍♂️",
    "🧜🏼",
    "🧜🏽‍♀️",
    "🧜🏽‍♂️",
    "🧜🏽",
    "🧜🏾‍♀️",
    "🧜🏾‍♂️",
    "🧜🏾",
    "🧜🏿‍♀️",
    "🧜🏿‍♂️",
    "🧜🏿",
    "🧜‍♀️",
    "🧜‍♂️",
    "🧜",
    "🧝🏻‍♀️",
    "🧝🏻‍♂️",
    "🧝🏻",
    "🧝🏼‍♀️",
    "🧝🏼‍♂️",
    "🧝🏼",
    "🧝🏽‍♀️",
    "🧝🏽‍♂️",
    "🧝🏽",
    "🧝🏾‍♀️",
    "🧝🏾‍♂️",
    "🧝🏾",
    "🧝🏿‍♀️",
    "🧝🏿‍♂️",
    "🧝🏿",
    "🧝‍♀️",
    "🧝‍♂️",
    "🧝",
    "🧞‍♀️",
    "🧞‍♂️",
    "🧞",
    "🧟‍♀️",
    "🧟‍♂️",
    "🧟",
    "🧠",
    "🧡",
    "🧢",
    "🧣",
    "🧤",
    "🧥",
    "🧦",
    "🧧",
    "🧨",
    "🧩",
    "🧪",
    "🧫",
    "🧬",
    "🧭",
    "🧮",
    "🧯",
    "🧰",
    "🧱",
    "🧲",
    "🧳",
    "🧴",
    "🧵",
    "🧶",
    "🧷",
    "🧸",
    "🧹",
    "🧺",
    "🧻",
    "🧼",
    "🧽",
    "🧾",
    "🧿",
    "🩰",
    "🩱",
    "🩲",
    "🩳",
    "🩴",
    "🩸",
    "🩹",
    "🩺",
    "🪀",
    "🪁",
    "🪂",
    "🪃",
    "🪄",
    "🪅",
    "🪆",
    "🪐",
    "🪑",
    "🪒",
    "🪓",
    "🪔",
    "🪕",
    "🪖",
    "🪗",
    "🪘",
    "🪙",
    "🪚",
    "🪛",
    "🪜",
    "🪝",
    "🪞",
    "🪟",
    "🪠",
    "🪡",
    "🪢",
    "🪣",
    "🪤",
    "🪥",
    "🪦",
    "🪧",
    "🪨",
    "🪰",
    "🪱",
    "🪲",
    "🪳",
    "🪴",
    "🪵",
    "🪶",
    "🫀",
    "🫁",
    "🫂",
    "🫐",
    "🫑",
    "🫒",
    "🫓",
    "🫔",
    "🫕",
    "🫖",
    "‼️",
    "⁉️",
    "™️",
    "ℹ️",
    "↔️",
    "↕️",
    "↖️",
    "↗️",
    "↘️",
    "↙️",
    "↩️",
    "↪️",
    "#⃣",
    "⌚️",
    "⌛️",
    "⌨️",
    "⏏️",
    "⏩",
    "⏪",
    "⏫",
    "⏬",
    "⏭️",
    "⏮️",
    "⏯️",
    "⏰",
    "⏱️",
    "⏲️",
    "⏳",
    "⏸️",
    "⏹️",
    "⏺️",
    "Ⓜ️",
    "▪️",
    "▫️",
    "▶️",
    "◀️",
    "◻️",
    "◼️",
    "◽️",
    "◾️",
    "☀️",
    "☁️",
    "☂️",
    "☃️",
    "☄️",
    "☎️",
    "☑️",
    "☔️",
    "☕️",
    "☘️",
    "☝🏻",
    "☝🏼",
    "☝🏽",
    "☝🏾",
    "☝🏿",
    "☝️",
    "☠️",
    "☢️",
    "☣️",
    "☦️",
    "☪️",
    "☮️",
    "☯️",
    "☸️",
    "☹️",
    "☺️",
    "♀️",
    "♂️",
    "♈️",
    "♉️",
    "♊️",
    "♋️",
    "♌️",
    "♍️",
    "♎️",
    "♏️",
    "♐️",
    "♑️",
    "♒️",
    "♓️",
    "♟️",
    "♠️",
    "♣️",
    "♥️",
    "♦️",
    "♨️",
    "♻️",
    "♾",
    "♿️",
    "⚒️",
    "⚓️",
    "⚔️",
    "⚕️",
    "⚖️",
    "⚗️",
    "⚙️",
    "⚛️",
    "⚜️",
    "⚠️",
    "⚡️",
    "⚧️",
    "⚪️",
    "⚫️",
    "⚰️",
    "⚱️",
    "⚽️",
    "⚾️",
    "⛄️",
    "⛅️",
    "⛈️",
    "⛎",
    "⛏️",
    "⛑️",
    "⛓️",
    "⛔️",
    "⛩️",
    "⛪️",
    "⛰️",
    "⛱️",
    "⛲️",
    "⛳️",
    "⛴️",
    "⛵️",
    "⛷🏻",
    "⛷🏼",
    "⛷🏽",
    "⛷🏾",
    "⛷🏿",
    "⛷️",
    "⛸️",
    "⛹🏻‍♀️",
    "⛹🏻‍♂️",
    "⛹🏻",
    "⛹🏼‍♀️",
    "⛹🏼‍♂️",
    "⛹🏼",
    "⛹🏽‍♀️",
    "⛹🏽‍♂️",
    "⛹🏽",
    "⛹🏾‍♀️",
    "⛹🏾‍♂️",
    "⛹🏾",
    "⛹🏿‍♀️",
    "⛹🏿‍♂️",
    "⛹🏿",
    "⛹️‍♀️",
    "⛹️‍♂️",
    "⛹️",
    "⛺️",
    "⛽️",
    "✂️",
    "✅",
    "✈️",
    "✉️",
    "✊🏻",
    "✊🏼",
    "✊🏽",
    "✊🏾",
    "✊🏿",
    "✊",
    "✋🏻",
    "✋🏼",
    "✋🏽",
    "✋🏾",
    "✋🏿",
    "✋",
    "✌🏻",
    "✌🏼",
    "✌🏽",
    "✌🏾",
    "✌🏿",
    "✌️",
    "✍🏻",
    "✍🏼",
    "✍🏽",
    "✍🏾",
    "✍🏿",
    "✍️",
    "✏️",
    "✒️",
    "✔️",
    "✖️",
    "✝️",
    "✡️",
    "✨",
    "✳️",
    "✴️",
    "❄️",
    "❇️",
    "❌",
    "❎",
    "❓",
    "❔",
    "❕",
    "❗️",
    "❣️",
    "❤️",
    "➕",
    "➖",
    "➗",
    "➡️",
    "➰",
    "➿",
    "⤴️",
    "⤵️",
    "*⃣",
    "⬅️",
    "⬆️",
    "⬇️",
    "⬛️",
    "⬜️",
    "⭐️",
    "⭕️",
    "0⃣",
    "〰️",
    "〽️",
    "1⃣",
    "2⃣",
    "㊗️",
    "㊙️",
    "3⃣",
    "4⃣",
    "5⃣",
    "6⃣",
    "7⃣",
    "8⃣",
    "9⃣",
    "©️",
    "®️",
    ""
)