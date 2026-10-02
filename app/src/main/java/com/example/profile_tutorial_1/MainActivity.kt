
package com.example.profile_tutorial_1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

data class Flashcard(
    val question: String,
    val isHack: Boolean,
    val explanation: String
)

val sampleFlashcards = listOf(
    Flashcard(
        question = "Putting a wooden spoon over a boiling pot stops it from boiling over.",
        isHack = true,
        explanation = "Correct! The wooden spoon pops bubbles and absorbs steam, preventing quick boil-overs."
    ),
    Flashcard(
        question = "Cracking your knuckles causes arthritis.",
        isHack = false,
        explanation = "Myth! Studies show popping knuckles release gas bubbles in synovial fluid and does not cause arthritis."
    ),
    Flashcard(
        question = "Using binder clips can organize messy cables on your desk.",
        isHack = true,
        explanation = "Correct! Attach binder clips to the edge of a desk and feed charging cables through the metal loops."
    )
)

object Routes {
    const val WELCOME = "welcome_screen"
    const val FLASHCARD = "flashcard_screen"
    const val SCORE = "score_screen"
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    AppNavigation(navController = navController)
                }
            }
        }
    }
}

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.WELCOME
    ) {
        composable(Routes.WELCOME) {
            WelcomeScreen(
                onStartClick = {
                    navController.navigate(Routes.FLASHCARD)
                }
            )
        }

        composable(Routes.FLASHCARD) {
            FlashcardScreen(
                cards = sampleFlashcards,
                onQuizComplete = { finalScore, total ->
                    navController.navigate(
                        "${Routes.SCORE}/$finalScore/$total"
                    ) {
                        popUpTo(Routes.WELCOME)
                    }
                }
            )
        }

        composable("${Routes.SCORE}/{score}/{total}") { backStackEntry ->
            val score = backStackEntry.arguments
                ?.getString("score")
                ?.toIntOrNull() ?: 0

            val total = backStackEntry.arguments
                ?.getString("total")
                ?.toIntOrNull() ?: 0

            ScoreScreen(
                score = score,
                total = total,
                onRestartClick = {
                    navController.navigate(Routes.WELCOME) {
                        popUpTo(Routes.WELCOME) {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun WelcomeScreen(onStartClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Life Hack or Urban Myth?",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Test your knowledge on common everyday tips and facts!",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onStartClick,
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            Text(
                text = "Start Quiz",
                fontSize = 18.sp
            )
        }
    }
}

@Composable
fun FlashcardScreen(
    cards: List<Flashcard>,
    onQuizComplete: (score: Int, total: Int) -> Unit
) {
    var currentIndex by remember {
        mutableIntStateOf(0)
    }

    var currentScore by remember {
        mutableIntStateOf(0)
    }

    var userFeedback by remember {
        mutableStateOf<String?>(null)
    }

    var isAnswered by remember {
        mutableStateOf(false)
    }

    val currentCard = cards[currentIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Question ${currentIndex + 1} of ${cards.size}",
            style = MaterialTheme.typography.titleMedium
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 16.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = currentCard.question,
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )
            }
        }

        userFeedback?.let { feedback ->
            Text(
                text = feedback,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        if (!isAnswered) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        val isCorrect = currentCard.isHack

                        if (isCorrect) {
                            currentScore++
                        }

                        userFeedback =
                            if (isCorrect) {
                                "Correct! ${currentCard.explanation}"
                            } else {
                                "Incorrect! ${currentCard.explanation}"
                            }

                        isAnswered = true
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                ) {
                    Text("Hack")
                }

                Button(
                    onClick = {
                        val isCorrect = !currentCard.isHack

                        if (isCorrect) {
                            currentScore++
                        }

                        userFeedback =
                            if (isCorrect) {
                                "Correct! ${currentCard.explanation}"
                            } else {
                                "Incorrect! ${currentCard.explanation}"
                            }

                        isAnswered = true
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                ) {
                    Text("Myth")
                }
            }
        } else {
            Button(
                onClick = {
                    if (currentIndex < cards.size - 1) {
                        currentIndex++
                        isAnswered = false
                        userFeedback = null
                    } else {
                        onQuizComplete(currentScore, cards.size)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (currentIndex < cards.size - 1) {
                        "Next Question"
                    } else {
                        "View Score"
                    }
                )
            }
        }
    }
}

@Composable
fun ScoreScreen(
    score: Int,
    total: Int,
    onRestartClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Quiz Completed!",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Your Score: $score / $total",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = onRestartClick) {
            Text("Restart Quiz")
        }
    }
}
