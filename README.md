# M3ComposeScreen

[![](https://jitpack.io/v/nbdy/M3ComposeScreen.svg)](https://jitpack.io/#nbdy/M3ComposeScreen)

A little wrapper around Scaffold with Top-/BottomAppBar, NavHost and Snackbar.

The full implementation is located [here](ComposeScreen/src/main/java/io/eberlein/composescreen/ScreenController.kt).

A working example app can be found [here](app/src/main/java/io/eberlein/composescreentest/MainActivity.kt).

## Sample code

```kotlin
/*
 * Define a new screen
 * The constructor of your new screen is also a good place to inject a ViewModel.
 */
class MyFirstScreen : AScreen(
    /* 
     * If you want this screen to appear in the BottomAppBar, specify a IconObject.
     *
     * If your screen has arguments you can specify them via the navArguments parameter.
     */
    ScreenInfo(R.string.Screen_First, IconObject(Icons.Filled.Info))
) {
    @ExperimentalMaterial3Api
    @Composable
    /* Override the Draw function and supply your own logic */
    override fun Draw(navController: NavController, paddingValues: PaddingValues) {
        Column(modifier = Modifier.padding(paddingValues)) {
            ListItem(headlineText = { Text(text = "First Screen") })
        }
    }
}

/*
 * Your generic MainActivity
 */
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            /* Get a ScreenController via the rememberScreenController function */
            val screenController = rememberScreenController(
                /* 
                 * Register your screens
                 * Key is the route, arguments can be specified as usual with {MyArg}
                 */
                screens = mutableMapOf(
                    "first" to MyFirstScreen()
                )
            )

            ComposeScreenTestTheme {
                screenController.Draw()
            }
        }
    }
}
```
