package com.toyou.toyouandroid.ui.base

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.ui.navigation.Route
import com.toyou.toyouandroid.ui.navigation.ToYouNavHost

data class BottomNavItem(
    val route: Route,
    val iconRes: Int
)

private val bottomNavItems = listOf(
    BottomNavItem(
        route = Route.Home,
        iconRes = R.drawable.lets_icons_home
    ),
    BottomNavItem(
        route = Route.Social,
        iconRes = R.drawable.lucide_pencil_line
    ),
    BottomNavItem(
        route = Route.Record,
        iconRes = R.drawable.quill_calendar
    ),
    BottomNavItem(
        route = Route.Mypage,
        iconRes = R.drawable.flowbite_user_outline
    )
)

@Composable
fun ToYouApp(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    var showBottomBar by remember { mutableStateOf(false) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars),
        bottomBar = {
            if (showBottomBar) {
                ToYouBottomNavigation(
                    items = bottomNavItems,
                    currentRoute = currentDestination?.route,
                    onItemClick = { item ->
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding)
        ) {
            ToYouNavHost(
                navController = navController,
                onShowBottomBar = { show -> showBottomBar = show }
            )
        }
    }
}

@Composable
private fun ToYouBottomNavigation(
    items: List<BottomNavItem>,
    currentRoute: String?,
    onItemClick: (BottomNavItem) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = Color.White,
        contentColor = MaterialTheme.colorScheme.primary
    ) {
        items.forEach { item ->
            val isSelected = currentRoute?.contains(item.route::class.simpleName ?: "") == true

            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = item.iconRes),
                        contentDescription = null,
                        tint = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                },
                selected = isSelected,
                onClick = { onItemClick(item) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = Color.Gray,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}
