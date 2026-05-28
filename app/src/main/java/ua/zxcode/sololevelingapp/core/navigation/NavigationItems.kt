package ua.zxcode.sololevelingapp.core.navigation

import ua.zxcode.sololevelingapp.R

sealed class Screen(val route: String, val iconResId: Int) {
    object Stats : Screen(
        route = "stats",
        iconResId = R.drawable.navbarstatscscreen
    )

    object Home : Screen(
        route = "home",
        iconResId = R.drawable.navbarhomescreen
    )

    object Achievements : Screen(
        route = "achievements",
        iconResId = R.drawable.navbarachievementsscreen
    )
}