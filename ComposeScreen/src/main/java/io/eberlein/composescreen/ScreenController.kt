package io.eberlein.composescreen

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
    val callback: () -> Unit,
    val icon: IconObject
) {
    @Composable fun Draw() { FloatingActionButton(onClick = { callback() }) { icon.Draw() } }
}

data class ScreenInfo(
    val title: Int,
    val icon: IconObject,
    val fabObject: FABObject? = null
)

interface IScreen {
    @ExperimentalMaterial3Api
    @Composable
    fun Draw(paddingValues: PaddingValues)
    @Composable
    fun getError(): String?
    fun getInfo(): ScreenInfo
}

abstract class AScreen(
    private val info: ScreenInfo,
    @StringRes private val error: Int? = null
) : IScreen {
    override fun getInfo() = info
    @Composable
    override fun getError() = if (error != null) {
        stringResource(error)
    } else {
        null
    }
}

class ScreenController(
    private val navController: NavHostController,
    private val screens: MutableMap<String, IScreen>,
    private var currentRoute: MutableState<String> = mutableStateOf(screens.keys.first()),
) {
    @Composable
    fun getScreenTitle(): String {
        return if (screens[currentRoute.value] == null) {
            stringResource(R.string.Screen_Unknown)
        } else {
            stringResource(screens[currentRoute.value]!!.getInfo().title)
        }
    }

    fun GetCurrentScreen() = screens[currentRoute.value]!!

    @ExperimentalMaterial3Api
    @Composable
    fun Draw() {
        val snackBarHostState = remember { SnackbarHostState() }
        val coroutineScope = rememberCoroutineScope()
        val fab = remember { GetCurrentScreen().getInfo().fabObject }

        fun showSnackBar(message: String) {
            coroutineScope.launch { snackBarHostState.showSnackbar(message) }
        }

        NavHost(navController, startDestination = currentRoute.value) {
            screens.forEach { entry -> composable(entry.key) { currentRoute.value = entry.key } }
        }

        Scaffold(
            topBar = { TopAppBar(title = { Text(getScreenTitle()) }) },
            bottomBar = {
                BottomAppBar(
                    actions = {
                        screens.forEach { entry ->
                            IconButton(onClick = { navController.navigate(entry.key) }) {
                                entry.value.getInfo().icon.Draw()
                            }
                        }
                    },
                    floatingActionButton = { fab?.Draw() }
                )
            },
            snackbarHost = {
                val error = GetCurrentScreen().getError()
                if (error != null) {
                    showSnackBar(error)
                }
            }
        ) { paddingValues ->
            GetCurrentScreen().Draw(paddingValues)
        }
    }
}
