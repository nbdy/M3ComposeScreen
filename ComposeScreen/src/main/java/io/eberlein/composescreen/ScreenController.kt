package io.eberlein.composescreen

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch

data class IconObject(
    val icon: ImageVector,
    val description: String? = null
) {
    @Composable fun Draw() { Icon(icon, description) }
}

data class FABObject(
    val icon: IconObject,
    var callback: (NavHostController) -> Unit = {},
) {
    @Composable fun Draw(navController: NavHostController) {
        FloatingActionButton(
            onClick = { callback(navController) },
            containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
        ) {
            icon.Draw()
        }
    }
}

data class ScreenInfo(
    val title: Int,
    val icon: IconObject? = null,
    val fabObject: FABObject? = null,
    val navArguments: List<NamedNavArgument> = listOf()
) {
    @Composable fun getTitle() = stringResource(title)
}

interface IScreen {
    @ExperimentalMaterial3Api
    @Composable
    fun Draw(navController: NavController, paddingValues: PaddingValues)
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
    private var startDestination: String = screens.keys.first(),
) {
    private var currentRoute by mutableStateOf<String?>(startDestination)

    private fun getCurrentScreen() = screens[currentRoute]!!

    @ExperimentalMaterial3Api
    @Composable
    fun Draw() {
        val snackBarHostState = remember { SnackbarHostState() }
        val coroutineScope = rememberCoroutineScope()

        fun showSnackBar(message: String) {
            coroutineScope.launch { snackBarHostState.showSnackbar(message) }
        }

        NavHost(navController, startDestination = startDestination) {
            screens.forEach { entry ->
                composable(entry.key, arguments = entry.value.info.navArguments) {
                    currentRoute = entry.key
                }
            }
        }

        Scaffold(
            topBar = { TopAppBar(title = { Text(getCurrentScreen().getTitle()) }) },
            bottomBar = {
                BottomAppBar(
                    actions = {
                        screens.forEach { entry ->
                            if (entry.value.info.icon != null) {
                                IconButton(onClick = { navController.navigate(entry.key) }) {
                                    entry.value.info.icon!!.Draw()
                                }
                            }
                        }
                    },
                    floatingActionButton = { getCurrentScreen().info.fabObject?.Draw(navController) }
                )
            },
            snackbarHost = {
                val error = getCurrentScreen().getError()
                if (error != null) {
                    showSnackBar(error)
                }
            }
        ) { paddingValues -> getCurrentScreen().Draw(navController, paddingValues) }
    }
}

@Composable
fun rememberScreenController(
    navController: NavHostController = rememberNavController(),
    screens: MutableMap<String, AScreen>
) = remember { ScreenController(navController, screens) }
