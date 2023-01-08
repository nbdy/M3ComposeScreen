package io.eberlein.composescreentest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import io.eberlein.composescreen.AScreen
import io.eberlein.composescreen.FABObject
import io.eberlein.composescreen.IconObject
import io.eberlein.composescreen.ScreenInfo
import io.eberlein.composescreen.rememberScreenController
import io.eberlein.composescreentest.ui.theme.ComposeScreenTestTheme

class MyFirstScreen : AScreen(
    ScreenInfo(R.string.Screen_First, IconObject(Icons.Filled.Info))
) {
    @ExperimentalMaterial3Api
    @Composable
    override fun Draw(navController: NavController, paddingValues: PaddingValues) {
        Column(modifier = Modifier.padding(paddingValues)) {
            ListItem(headlineText = { Text(text = "First Screen") })
        }
    }
}

class MySecondScreen : AScreen(
    ScreenInfo(R.string.Screen_Second, IconObject(Icons.Filled.Send))
) {
    @ExperimentalMaterial3Api
    @Composable
    override fun Draw(navController: NavController, paddingValues: PaddingValues) {
        Column(modifier = Modifier.padding(paddingValues)) {
            ListItem(headlineText = { Text(text = "Second Screen") })
        }
    }
}

class NumbersScreen(
    private var currentNumber: MutableState<Int> = mutableStateOf(0)
) : AScreen(
    ScreenInfo(
        R.string.Screen_Number,
        IconObject(Icons.Filled.Notifications),
        FABObject(IconObject(Icons.Filled.Star))
    )
) {
    init {
        info.fabObject!!.callback = { currentNumber.value++ }
    }

    @Composable override fun getTitle(): String = stringResource(info.title, currentNumber.value)

    @ExperimentalMaterial3Api
    @Composable
    override fun Draw(navController: NavController, paddingValues: PaddingValues) {
        Column(modifier = Modifier.padding(paddingValues)) {
            (0..currentNumber.value).forEach {
                ListItem(
                    headlineText = { Text(it.toString()) },
                    modifier = Modifier.clickable { navController.navigate("number/$it") }
                )
            }
        }
    }
}

class NumberScreen : AScreen(
    ScreenInfo(
        R.string.Placeholder,
        navArguments = listOf(
            navArgument("number") { type = NavType.IntType }
        )
    )
) {
    private var currentNumber by mutableStateOf(0)

    @Composable
    override fun getTitle(): String = stringResource(R.string.Placeholder, currentNumber)

    @ExperimentalMaterial3Api
    @Composable
    override fun Draw(navController: NavController, paddingValues: PaddingValues) {
        currentNumber = navController.currentBackStackEntry?.arguments?.getInt("number")!!

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(currentNumber.toString())
        }
    }
}

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val screenController = rememberScreenController(
                screens = mutableMapOf(
                    "first" to MyFirstScreen(),
                    "second" to MySecondScreen(),
                    "numbers" to NumbersScreen(),
                    "number/{number}" to NumberScreen()
                )
            )

            ComposeScreenTestTheme {
                screenController.Draw()
            }
        }
    }
}
