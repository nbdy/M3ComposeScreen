package io.eberlein.composescreentest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import io.eberlein.composescreen.AScreen
import io.eberlein.composescreen.FABObject
import io.eberlein.composescreen.IconObject
import io.eberlein.composescreen.ScreenController
import io.eberlein.composescreen.ScreenInfo
import io.eberlein.composescreentest.ui.theme.ComposeScreenTestTheme

class MyFirstScreen : AScreen(
    ScreenInfo(R.string.Screen_First, IconObject(Icons.Filled.Info, null), null)
) {
    @ExperimentalMaterial3Api
    @Composable
    override fun Draw(paddingValues: PaddingValues, bundle: Bundle?) {
        Column(modifier = Modifier.padding(paddingValues)) {
            ListItem(headlineText = { Text(text = "First Screen") })
        }
    }
}

class MySecondScreen : AScreen(
    ScreenInfo(R.string.Screen_Second, IconObject(Icons.Filled.Send, null), null)
) {
    @ExperimentalMaterial3Api
    @Composable
    override fun Draw(paddingValues: PaddingValues, bundle: Bundle?) {
        Column(modifier = Modifier.padding(paddingValues)) {
            ListItem(headlineText = { Text(text = "Second Screen") })
        }
    }
}

class NumberScreen(
    private var currentNumber: MutableState<Int> = mutableStateOf(0)
) : AScreen(
    ScreenInfo(
        R.string.Screen_Number,
        IconObject(Icons.Filled.Notifications, null),
        FABObject(IconObject(Icons.Filled.Star, null)),
        navArguments = listOf(navArgument("number") {})
    )
) {
    init {
        info.fabObject!!.callback = { currentNumber.value++ }
    }

    @Composable override fun getTitle(): String = stringResource(info.title, currentNumber.value)

    @ExperimentalMaterial3Api
    @Composable
    override fun Draw(paddingValues: PaddingValues, bundle: Bundle?) {
        Column(modifier = Modifier.padding(paddingValues)) {
            ListItem(headlineText = { Text(text = "Number: ${currentNumber.value}") })
        }
    }
}

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            val screenController = remember {
                ScreenController(navController, mutableMapOf(
                    "first" to MyFirstScreen(),
                    "second" to MySecondScreen(),
                    "number/{number}" to NumberScreen()
                ))
            }

            ComposeScreenTestTheme {
                screenController.Draw()
            }
        }
    }
}
