package com.example.w06

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.random.Random

// 데이터 클래스의 프로퍼티는 'val'로 선언하는 것이 좋습니다.
// 상태 변경이 필요할 때 copy()를 사용해 새 객체를 만듭니다.
data class Bubble(
    val id: Int,
    val position: Offset,
    val radius: Dp,
    val color: Color,
    val creationTime: Long = System.currentTimeMillis(),
    val velocity: Offset // X와 Y 속도를 Offset으로 통합
)

class GameState(initialBubbles: List<Bubble> = emptyList()) {
    var bubbles by mutableStateOf(initialBubbles)
    var score by mutableStateOf(0)
    var isGameOver by mutableStateOf(false)
    var timeLeft by mutableStateOf(60)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BubbleGameScreen()
                }
            }
        }
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun BubbleGameScreen() {
    val gameState = remember { GameState() }
    var showDialog by remember { mutableStateOf(false) }

    // 게임 타이머 및 시간 초과 로직
    LaunchedEffect(gameState.isGameOver) {
        if (!gameState.isGameOver) {
            while (isActive && gameState.timeLeft > 0) {
                delay(1000L)
                gameState.timeLeft--

                // 3초 지난 버블 제거
                val currentTime = System.currentTimeMillis()
                gameState.bubbles = gameState.bubbles.filter {
                    currentTime - it.creationTime < 3000
                }
            }
            if (gameState.timeLeft <= 0) {
                onGameOver(gameState) { showDialog = true }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(top = 24.dp)) {
        GameStatusRow(score = gameState.score, timeLeft = gameState.timeLeft)

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val maxWidth = this.maxWidth
            val maxHeight = this.maxHeight

            // 버블 생성 및 이동 로직 (게임 루프)
            LaunchedEffect(gameState.isGameOver) {
                if (!gameState.isGameOver) {
                    while (isActive) {
                        delay(16) // ~60 FPS

                        // 버블이 없으면 새로 생성
                        if (gameState.bubbles.isEmpty()) {
                            gameState.bubbles = List(3) { makeNewBubble(maxWidth, maxHeight) }
                        }

                        // 확률적으로 새 버블 추가 (최대 15개)
                        if (Random.nextFloat() < 0.05f && gameState.bubbles.size < 15) {
                            val newBubble = makeNewBubble(maxWidth, maxHeight)
                            gameState.bubbles = gameState.bubbles + newBubble
                        }

                        // 버블 위치 업데이트
                        gameState.bubbles = updateBubblePositions(
                            bubbles = gameState.bubbles,
                            maxWidth = maxWidth,
                            maxHeight = maxHeight
                        )
                    }
                }
            }

            // 버블 그리기
            gameState.bubbles.forEach { bubble ->
                BubbleComposable(bubble = bubble) {
                    gameState.score++
                    gameState.bubbles = gameState.bubbles.filterNot { it.id == bubble.id }
                }
            }
        }
    }

    // 게임 오버 다이얼로그
    if (showDialog) {
        GameOverDialog(
            score = gameState.score,
            onRestart = {
                restartGame(gameState)
                showDialog = false
            },
            onExit = {
                // 실제 앱에서는 Activity를 종료하는 코드가 필요할 수 있습니다.
                showDialog = false
            }
        )
    }
}

@Composable
fun GameStatusRow(score: Int, timeLeft: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Score: $score", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(text = "Time: ${timeLeft}s", fontSize = 24.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun BubbleComposable(bubble: Bubble, onClick: () -> Unit) {
    Canvas(
        modifier = Modifier
            .size(bubble.radius * 2) // Dp 단위를 직접 사용
            .offset(
                x = bubble.position.x.dp, // 올바른 Dp 사용법
                y = bubble.position.y.dp  // 올바른 Dp 사용법
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
    ) {
        drawCircle(
            color = bubble.color,
            radius = size.width / 2,
            center = center
        )
    }
}

@Composable
fun GameOverDialog(score: Int, onRestart: () -> Unit, onExit: () -> Unit) {
    AlertDialog(
        onDismissRequest = {},
        title = { Text("게임 오버") },
        text = { Text("당신의 점수는 $score 점입니다.") },
        confirmButton = {
            TextButton(onClick = onRestart) { Text("다시 시작") }
        },
        dismissButton = {
            TextButton(onClick = onExit) { Text("종료") }
        }
    )
}

fun makeNewBubble(maxWidth: Dp, maxHeight: Dp): Bubble {
    val radius = (Random.nextFloat() * 25 + 25).dp // 25dp에서 50dp 사이의 반지름
    return Bubble(
        id = Random.nextInt(),
        position = Offset(
            x = Random.nextFloat() * (maxWidth.value - radius.value * 2),
            y = Random.nextFloat() * (maxHeight.value - radius.value * 2)
        ),
        radius = radius,
        velocity = Offset(
            x = (Random.nextFloat() - 0.5f) * 4, // -2.0에서 +2.0 사이의 속도
            y = (Random.nextFloat() - 0.5f) * 4
        ),
        color = Color(
            red = Random.nextInt(256),
            green = Random.nextInt(256),
            blue = Random.nextInt(256),
            alpha = 200
        )
    )
}

// Dp 단위만 사용하여 위치를 업데이트하는 단순화된 함수
fun updateBubblePositions(bubbles: List<Bubble>, maxWidth: Dp, maxHeight: Dp): List<Bubble> {
    return bubbles.map { bubble ->
        var newX = bubble.position.x + bubble.velocity.x
        var newY = bubble.position.y + bubble.velocity.y
        var newVx = bubble.velocity.x
        var newVy = bubble.velocity.y

        val radiusValue = bubble.radius.value
        val maxWidthValue = maxWidth.value
        val maxHeightValue = maxHeight.value

        // 화면 경계 충돌 처리 (오프셋은 버블의 좌상단 기준이므로 반지름을 고려해야 함)
        if (newX < 0 || newX > maxWidthValue - radiusValue * 2) {
            newVx *= -1
            newX = newX.coerceIn(0f, maxWidthValue - radiusValue * 2)
        }
        if (newY < 0 || newY > maxHeightValue - radiusValue * 2) {
            newVy *= -1
            newY = newY.coerceIn(0f, maxHeightValue - radiusValue * 2)
        }

        bubble.copy(
            position = Offset(newX, newY),
            velocity = Offset(newVx, newVy)
        )
    }
}

fun onGameOver(gameState: GameState, showDialog: () -> Unit) {
    gameState.isGameOver = true
    showDialog()
}

fun restartGame(gameState: GameState) {
    gameState.score = 0
    gameState.timeLeft = 60
    gameState.isGameOver = false
    gameState.bubbles = emptyList()
}

@Preview(showBackground = true)
@Composable
fun BubbleGamePreview() {
    MaterialTheme {
        BubbleGameScreen()
    }
}