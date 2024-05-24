package com.lengo.uni.ui

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController
import com.lengo.model.data.Lection
import com.lengo.model.data.Pack
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

val LocalAppState = compositionLocalOf<MainViewState> { error("No active user found!") }
val LocalNavigator = staticCompositionLocalOf<NavHostController> { throw Exception("Compose Navigator not set. Did you forgot to call \"Navigator.Setup\"?") }

sealed class Screen(val route: String) {

    data object Dashboard : Screen("dashboard")
    data object OnBoardingMainScreen : Screen("main_screen")
    data object OnboardingSubscription : Screen("OnboardingSubscription")
    data object OnboardingSelectLang : Screen("OnboardingSelectLang")

    data object CategoryDetails : Screen("category_details/{categoryName}") {
        fun createRoute(categoryName: String): String {
            val enCategoryName = URLEncoder.encode(categoryName, StandardCharsets.UTF_8.toString())
            return "category_details/$enCategoryName"
        }
    }

    data object PacksDetails : Screen("pack_details/{name}/{pck}/{owner}/{type}/{lang}") {
        fun createRoute(pack: Pack): String {
            val title = URLEncoder.encode(pack.title, StandardCharsets.UTF_8.toString())
            return "pack_details/${if (title == "") "--" else title}/${pack.pck}/${pack.owner}/${pack.type}/${pack.lang}"
        }
    }

    data object WordList : Screen("wordslist/{lectionName}/{packName}/{lec}/{pck}/{owner}/{type}/{lang}/{packEmoji}") {
        fun createRoute(pack: Pack, lec: Lection): String {
            val title = URLEncoder.encode(lec.title, StandardCharsets.UTF_8.toString())
            val packName = URLEncoder.encode(pack.title, StandardCharsets.UTF_8.toString())
            val packEmoji = URLEncoder.encode(pack.emoji, StandardCharsets.UTF_8.toString())
            return "wordslist/${if (title == "") "--" else title}/${if (packName == "") "--" else packName}/${lec.lec}/${lec.pck}/${lec.owner}/${lec.type}/${lec.lang}/${packEmoji}"
        }
        fun createRoute(packName: String, pakEmoji: String, lec: Lection): String {
            val title = URLEncoder.encode(lec.title, StandardCharsets.UTF_8.toString())
            val packName = URLEncoder.encode(packName, StandardCharsets.UTF_8.toString())
            val packEmoji = URLEncoder.encode(pakEmoji, StandardCharsets.UTF_8.toString())
            return "wordslist/${if (title == "") "--" else title}/${if (packName == "") "--" else packName}/${lec.lec}/${lec.pck}/${lec.owner}/${lec.type}/${lec.lang}/${packEmoji}"
        }
    }
    data object Quiz :
        Screen("quiz/{wordColor}/{lectionName}/{packName}/{lec}/{pck}/{owner}/{type}/{lang}") {
        fun createRoute(wordColor: Int,
                        lectionName: String,
                        packName: String,
                        pck: Long,
                        lec: Long,
                        owner: Long,
                        type: String,
                        lang: String): String {
            val lecName = URLEncoder.encode(lectionName, StandardCharsets.UTF_8.toString())
            val paName = URLEncoder.encode(packName, StandardCharsets.UTF_8.toString())
            return "quiz/${wordColor}/${if (lecName == "") "--" else lecName}/${if (paName == "") "--" else paName}/${lec}/${pck}/${owner}/${type}/${lang}"
        }
    }

    data object MyWordDetail : Screen("mywordsdetail/{name}/{word_color}") {
        fun createRoute(wordsName: String, wordsColor: Int): String {
            val enwordsName = URLEncoder.encode(wordsName, StandardCharsets.UTF_8.toString())
            return "mywordsdetail/${enwordsName}/${wordsColor}"
        }
    }

    data object WebPage : Screen("webpage/{url}") {
        fun createRoute(url: String): String {
            val encodeUrl = URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
            return "webpage/${encodeUrl}"
        }
    }
    data object Profile : Screen("profile")

    data object RankingList : Screen("rankinglist")

}

/**
 * If the lifecycle is not resumed it means this NavBackStackEntry already processed a nav event.
 *
 * This is used to de-duplicate navigation events.
 */
//fun NavBackStackEntry.lifecycleIsResumed() =
//    this.lifecycle.currentState == Lifecycle.State.RESUMED