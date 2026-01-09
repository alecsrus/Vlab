package com.virtuallabs.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.virtuallabs.data.CatalogRepository
import com.virtuallabs.premium.PremiumManager
import com.virtuallabs.tutor.LocalTutor
import com.virtuallabs.ui.screens.HomeScreen
import com.virtuallabs.ui.screens.LabHostScreen
import com.virtuallabs.ui.screens.PaywallScreen
import com.virtuallabs.ui.screens.SettingsScreen
import com.virtuallabs.ui.screens.SubjectScreen
import com.virtuallabs.ui.screens.TutorScreen

data class AppServices(
    val catalog: CatalogRepository,
    val premium: PremiumManager,
    val tutor: LocalTutor
)

@Composable
fun rememberAppServices(): AppServices {
    val context = LocalContext.current.applicationContext
    val catalog = remember { CatalogRepository(context) }
    val premium = remember { PremiumManager(context) }
    val tutor = remember { LocalTutor(context) }
    return remember { AppServices(catalog, premium, tutor) }
}

object Routes {
    const val HOME = "home"
    const val SUBJECT = "subject"
    const val LAB = "lab"
    const val TUTOR = "tutor"
    const val PAYWALL = "paywall"
    const val SETTINGS = "settings"

    fun subject(subjectId: String) = "$SUBJECT/$subjectId"
    fun lab(topicId: String) = "$LAB/$topicId"
    fun paywall(topicId: String) = "$PAYWALL/$topicId"
    fun tutor(prefill: String? = null): String {
        return if (prefill.isNullOrBlank()) {
            TUTOR
        } else {
            TUTOR + "?prefill=" + Uri.encode(prefill)
        }
    }
}

@Composable
fun AppNav() {
    val navController = rememberNavController()
    val services = rememberAppServices()

    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                catalog = services.catalog,
                premiumManager = services.premium,
                onOpenSubject = { subjectId -> navController.navigate(Routes.subject(subjectId)) },
                onOpenTutor = { navController.navigate(Routes.tutor()) },
                onOpenSettings = { navController.navigate(Routes.SETTINGS) }
            )
        }

        composable(
            route = "${Routes.SUBJECT}/{subjectId}",
            arguments = listOf(navArgument("subjectId") { type = NavType.StringType })
        ) { backStackEntry ->
            val subjectId = backStackEntry.arguments?.getString("subjectId") ?: return@composable
            SubjectScreen(
                subjectId = subjectId,
                catalog = services.catalog,
                premiumManager = services.premium,
                onBack = { navController.popBackStack() },
                onOpenLab = { topicId -> navController.navigate(Routes.lab(topicId)) },
                onOpenPaywall = { topicId -> navController.navigate(Routes.paywall(topicId)) },
                onOpenTutor = { prefill -> navController.navigate(Routes.tutor(prefill)) },
                onOpenSettings = { navController.navigate(Routes.SETTINGS) }
            )
        }

        composable(
            route = "${Routes.LAB}/{topicId}",
            arguments = listOf(navArgument("topicId") { type = NavType.StringType })
        ) { backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId") ?: return@composable
            LabHostScreen(
                topicId = topicId,
                catalog = services.catalog,
                premiumManager = services.premium,
                onBack = { navController.popBackStack() },
                onOpenPaywall = { navController.navigate(Routes.paywall(topicId)) },
                onOpenTutor = { prefill -> navController.navigate(Routes.tutor(prefill)) },
                onOpenSettings = { navController.navigate(Routes.SETTINGS) }
            )
        }

        composable(
            route = "${Routes.PAYWALL}/{topicId}",
            arguments = listOf(navArgument("topicId") { type = NavType.StringType })
        ) { backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId") ?: return@composable
            PaywallScreen(
                topicId = topicId,
                catalog = services.catalog,
                premiumManager = services.premium,
                onBack = { navController.popBackStack() },
                onUnlocked = {
                    // После разблокировки пробуем перейти в лабораторию
                    navController.navigate(Routes.lab(topicId)) {
                        popUpTo(Routes.HOME) { inclusive = false }
                    }
                }
            )
        }

        composable(
            route = Routes.TUTOR + "?prefill={prefill}",
            arguments = listOf(
                navArgument("prefill") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val prefill = backStackEntry.arguments?.getString("prefill")
            TutorScreen(
                tutor = services.tutor,
                prefill = prefill,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                premiumManager = services.premium,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
