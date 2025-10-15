package com.example.w05

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CounterApp()
                    Spacer(modifier = Modifier.height(32.dp))
                    StopWatchApp()
                }
            }
        }
    }
}

@Composable
fun CounterApp() {
    var count by remember { mutableStateOf(0) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Count: $count",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )
        Row {
            Button(onClick = { count++ }) {
                Text("Increase")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { count = 0 }) {
                Text("Reset")
            }
        }
    }
}

@Composable
fun StopWatchApp() {
    var timeMillis by remember { mutableStateOf(0L) }
    var isRunning by remember { mutableStateOf(false) }

    LaunchedEffect(isRunning) {
        while (isRunning) {
            delay(10L) // 10ms 단위로 업데이트
            timeMillis += 10L
        }
    }

    val totalSeconds = timeMillis / 1000.0
    val minutes = (totalSeconds / 60).toInt()
    val secondsWithDecimal = totalSeconds % 60

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = String.format("%02d:%05.2f", minutes, secondsWithDecimal),
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )
        Row {
            Button(onClick = { isRunning = true }) {
                Text("Start")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { isRunning = false }) {
                Text("Stop")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                isRunning = false
                timeMillis = 0L
            }) {
                Text("Reset")
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CounterAppPreview() {
    CounterApp()
}

@Preview(showBackground = true)
@Composable
fun StopWatchPreview() {
    StopWatchApp()
}

@Composable
fun ColorToggleButtonApp() {
    // 배경색 상태를 저장하는 변수. 초기값은 Color.Red.
    // 'by' 키워드를 사용하여 MutableState<Color>의 value 속성에 직접 접근하도록 함.
    var currentColor by remember { mutableStateOf(Color.Red) }

    // 원형 버튼을 화면 중앙에 배치하기 위한 외부 Box
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // 클릭 가능한 원형 버튼 역할을 하는 내부 Box
        Box(
            modifier = Modifier
                .clip(CircleShape) // 모양을 원형으로 자름
                .background(currentColor) // 현재 색상으로 배경 설정
                .clickable { // 클릭 이벤트 처리
                    // 현재 색상이 빨간색이면 파란색으로, 아니면 빨간색으로 변경
                    currentColor = if (currentColor == Color.Red) Color.Blue else Color.Red
                }
                .padding(30.dp), // 원 안쪽에 여백을 줘서 텍스트와 경계 사이에 공간을 둠
            contentAlignment = Alignment.Center // Box 안의 내용을 중앙에 정렬
        ) {
            Text(
                text = "Click Me",
                color = Color.White, // 텍스트 색상은 흰색으로 고정
                fontSize = 30.sp
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 300, heightDp = 300)
@Composable
fun ColorToggleButtonAppPreview() {
    ColorToggleButtonApp()
}
