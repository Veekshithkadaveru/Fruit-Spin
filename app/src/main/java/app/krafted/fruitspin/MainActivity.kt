package app.krafted.fruitspin

import app.krafted.fruitspin.ui.theme.FruitSpinTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.krafted.fruitspin.ui.GameOverScreen
import app.krafted.fruitspin.ui.GameScreen
import app.krafted.fruitspin.ui.HomeScreen
import app.krafted.fruitspin.ui.SplashScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FruitSpinTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = "splash"
                    ) {
                        composable("splash") {
                            SplashScreen(
                                onSplashFinished = {
                                    navController.navigate("home") {
                                        popUpTo("splash") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("home") {
                            HomeScreen(
                                onPlayClick = {
                                    navController.navigate("game")
                                }
                            )
                        }
                        composable("game") {
                            GameScreen(
                                onGameOver = { finalScore ->
                                    navController.navigate("game_over/$finalScore") {
                                        popUpTo("game") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("game_over/{score}") { backStackEntry ->
                            val score = backStackEntry.arguments?.getString("score")?.toIntOrNull() ?: 0
                            GameOverScreen(
                                score = score,
                                onPlayAgain = {
                                    navController.navigate("game") {
                                        popUpTo("home") { inclusive = false }
                                    }
                                },
                                onMainMenu = {
                                    navController.navigate("home") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}