package com.lengo.uni.ui

import android.graphics.Color
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.lengo.common.FLAVOUR_TYPE_ALL
import com.lengo.common.TestIdlingResource
import com.lengo.common.extension.rememberFlowWithLifecycle
import com.lengo.common.ui.theme.LENGOTheme
import com.lengo.common.ui.theme.LocalDarkModeEnable
import com.lengo.common.ui.theme.darkScrim
import com.lengo.common.ui.theme.lightScrim
import com.lengo.uni.BuildConfig
import com.lengo.uni.ui.bottomsheet.LanguageSelectionContent
import com.lengo.uni.ui.categorydetails.CategoryDetails
import com.lengo.uni.ui.dashboard.Dashboard
import com.lengo.uni.ui.mywordsdetail.MyWordsDetail
import com.lengo.uni.ui.onboarding.OnboardingMain
import com.lengo.uni.ui.packdetail.PackDetails
import com.lengo.uni.ui.profile.Profile
import com.lengo.uni.ui.quiz.QuizMain
import com.lengo.uni.ui.rankinglist.RankingList
import com.lengo.uni.ui.sheet.FreeTrailSheet
import com.lengo.uni.ui.webpage.WebPage
import com.lengo.uni.ui.wordlist.WordList
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun MainScreen(
    windowSize: WindowWidthSizeClass,
    splashScreenVisibleCondition: (SplashScreen.KeepOnScreenCondition) -> Unit,
) {
    val activity = LocalContext.current as MainActivity
    val mainViewModel = activity.mainViewModel
    val navController = rememberNavController()

    val viewState by rememberFlowWithLifecycle(mainViewModel.uiState)
        .collectAsState(initial = MainViewState.Empty)

    if (viewState.initialScreen != null && viewState.settingModel.darkThemeEnable != null && viewState.userSelectedLang != null) {
        splashScreenVisibleCondition { false }
        TestIdlingResource.decrement()
        val darkTheme = viewState.settingModel.darkThemeEnable!!

        DisposableEffect(darkTheme) {
            activity.enableEdgeToEdge(
                statusBarStyle = SystemBarStyle.auto(
                    Color.TRANSPARENT,
                    Color.TRANSPARENT,
                ) { darkTheme },
                navigationBarStyle = SystemBarStyle.auto(
                    lightScrim,
                    darkScrim,
                ) { darkTheme },
            )
            onDispose {}
        }


        LENGOTheme(viewState.userSelectedLang, darkTheme) {
            CompositionLocalProvider(LocalAppState provides viewState) {
                CompositionLocalProvider(LocalDarkModeEnable provides viewState.settingModel.darkThemeEnable!!) {
                    CompositionLocalProvider(LocalNavigator provides navController) {
                        NavHost(
                            navController = navController,
                            startDestination = viewState.initialScreen!!.route,
                            modifier = Modifier.background(MaterialTheme.colors.background),
                            enterTransition = { slideInHorizontally { it } },
                            exitTransition = { fadeOut(animationSpec = tween(300)) + slideOutHorizontally() },
                            popEnterTransition = { fadeIn(animationSpec = tween(300)) + slideInHorizontally() },
                            popExitTransition = { slideOutHorizontally { it } }
                        ) {
                            composable(Screen.Dashboard.route) { entry ->
                                Dashboard(viewState.currentMenuScreen, windowSize)
                            }
                            composable(Screen.OnBoardingMainScreen.route) { entry ->
                                OnboardingMain(
                                    hiltViewModel(),
                                    hiltViewModel(),
                                    hiltViewModel()
                                )
                            }
                            composable(Screen.OnboardingSubscription.route) { entry ->
                                FreeTrailSheet()
                            }
                            composable(Screen.OnboardingSelectLang.route) { entry ->
                                LanguageSelectionContent(viewState.allLanguage, {
                                    mainViewModel.markLangSheetShown()
                                    if (BuildConfig.FLAVOR_TYPE == FLAVOUR_TYPE_ALL) {
                                        mainViewModel.selLanguageSelected(it)
                                        navController.navigate(Screen.Dashboard.route)
                                    }
                                }) {
                                    mainViewModel.markLangSheetShown()
                                    navController.navigate(Screen.Dashboard.route)
                                }
                            }
                            composable(Screen.PacksDetails.route) { entry ->
                                PackDetails()
                            }
                            composable(Screen.WordList.route) { entry ->
                                WordList()
                            }
                            composable(Screen.Quiz.route) { entry ->
                                QuizMain()
                            }
                            composable(Screen.CategoryDetails.route) { entry ->
                                CategoryDetails()
                            }
                            composable(Screen.WebPage.route) { entry ->
                                val url = URLDecoder.decode(
                                    entry.arguments?.getString("url")!!,
                                    StandardCharsets.UTF_8.toString()
                                )
                                WebPage(url)
                            }
                            composable(Screen.Profile.route) { entry ->
                                Profile()
                            }
                            composable(Screen.MyWordDetail.route) { entry ->
                                MyWordsDetail()
                            }
                            composable(Screen.RankingList.route) { entry ->
                                RankingList()
                            }
                        }

                    }
                }
            }
        }
    } else {
        splashScreenVisibleCondition { true }
    }


}