package io.eberlein.composescreen

import android.os.Bundle
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import kotlinx.coroutines.launch

data class IconObject(
    val icon: ImageVector,
    val description: String? = null
) {
    @Composable fun Draw() { Icon(icon, description) }
}

data class FABObject(
    val icon: IconObject,
    var callback: () -> Unit = {},
) {
    @Composable fun Draw() {
        FloatingActionButton(
            onClick = { callback() },
            containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
        ) {
            icon.Draw()
        }
    }
}

data class ScreenInfo(
    val title: Int,
    val icon: IconObject,
    val fabObject: FABObject?,
    val navArguments: List<NamedNavArgument> = listOf()
) {
    @Composable fun getTitle() = stringResource(title)
}

interface IScreen {
    @ExperimentalMaterial3Api
    @Composable
    fun Draw(paddingValues: PaddingValues, bundle: Bundle?)
    @Composable
    fun getError(): String?
    @Composable fun getTitle(): String
}

abstract class AScreen(
    val info: ScreenInfo,
    @StringRes val error: Int? = null
) : IScreen {
    @Composable
    override fun getError() = if (error != null) {
        stringResource(error)
    } else {
        null
    }
    @Composable
    override fun getTitle() = info.getTitle()
}

class ScreenController(
    private val navController: NavHostController,
    private val screens: MutableMap<String, AScreen>,
    private var currentRoute: MutableState<String> = mutableStateOf(screens.keys.first()),
) {
    private fun getCurrentScreen() = screens[currentRoute.value]!!

    @ExperimentalMaterial3Api
    @Composable
    fun Draw() {
        val snackBarHostState = remember { SnackbarHostState() }
        val coroutineScope = rememberCoroutineScope()
        val bundle = remember { mutableStateOf<Bundle?>(null) }

        fun showSnackBar(message: String) {
            coroutineScope.launch { snackBarHostState.showSnackbar(message) }
        }

        NavHost(navController, startDestination = currentRoute.value) {
            screens.forEach { entry ->
                val route = entry.key
                composable(
                    route,
                    arguments = entry.value.info.navArguments
                ) { backStackEntry ->
                    bundle.value = backStackEntry.arguments
                    currentRoute.value = route
                }
            }
        }

        Scaffold(
            topBar = { TopAppBar(title = { Text(getCurrentScreen().getTitle()) }) },
            bottomBar = {
                BottomAppBar(
                    actions = {
                        screens.forEach { entry ->
                            IconButton(onClick = { navController.navigate(entry.key) }) {
                                entry.value.info.icon.Draw()
                            }
                        }
                    },
                    floatingActionButton = { getCurrentScreen().info.fabObject?.Draw() }
                )
            },
            snackbarHost = {
                val error = getCurrentScreen().getError()
                if (error != null) {
                    showSnackBar(error)
                }
            }
        ) { paddingValues -> getCurrentScreen().Draw(paddingValues, bundle.value) }
    }
}
