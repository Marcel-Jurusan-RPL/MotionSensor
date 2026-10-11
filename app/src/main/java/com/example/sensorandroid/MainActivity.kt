package com.example.sensorandroid

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                BolaSensorScreen()
            }
        }
    }
}

@Composable
fun BolaSensorScreen() {
    val context = LocalContext.current
    val density = LocalDensity.current

    val ballSize = 50.dp
    val ballRadiusPx = with(density) { (ballSize / 2).toPx() }

    var ballX by remember { mutableFloatStateOf(0f) }
    var ballY by remember { mutableFloatStateOf(0f) }
    var containerSize by remember { mutableStateOf(IntSize.Zero) }

    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    if (it.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                        val sx = it.values[0]
                        val sy = it.values[1]
                        val sensitivitas = 2.8f

                        val nextX = ballX - (sx * sensitivitas)
                        val nextY = ballY + (sy * sensitivitas)

                        if (containerSize.width > 0 && containerSize.height > 0) {
                            val minX = ballRadiusPx
                            val maxX = containerSize.width.toFloat() - ballRadiusPx
                            val minY = ballRadiusPx
                            val maxY = containerSize.height.toFloat() - ballRadiusPx

                            ballX = nextX.coerceIn(minX, maxX)
                            ballY = nextY.coerceIn(minY, maxY)
                        } else {
                            ballX = nextX
                            ballY = nextY
                        }
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        if (accelerometer != null) {
            sensorManager.registerListener(
                listener,
                accelerometer,
                SensorManager.SENSOR_DELAY_GAME
            )
        }

        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F8FC))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp, vertical = 28.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Balance Ball",
                        fontSize = 27.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF202532)
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = "Control the ball with your phone",
                        fontSize = 13.sp,
                        color = Color(0xFF8A91A1)
                    )
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xFFEAF5EE))
                        .padding(horizontal = 11.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .background(Color(0xFF4CAF72), CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Sensor ready",
                        fontSize = 11.sp,
                        color = Color(0xFF43855C),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color.White)
                    .border(
                        width = 1.dp,
                        color = Color(0xFFE8EAF0),
                        shape = RoundedCornerShape(28.dp)
                    )
                    .onGloballyPositioned { coordinates ->
                        val newSize = coordinates.size
                        if (containerSize != newSize) {
                            containerSize = newSize
                            if (ballX == 0f && ballY == 0f) {
                                ballX = newSize.width / 2f
                                ballY = newSize.height / 2f
                            }
                        }
                    }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val centerX = size.width / 2f
                    val centerY = size.height / 2f

                    drawCircle(
                        color = Color(0xFFF0F3F8),
                        radius = 60.dp.toPx(),
                        center = Offset(centerX, centerY)
                    )
                    drawCircle(
                        color = Color(0xFFE8EDF5),
                        radius = 35.dp.toPx(),
                        center = Offset(centerX, centerY)
                    )
                    drawCircle(
                        color = Color(0xFFD6DCE7),
                        radius = 3.dp.toPx(),
                        center = Offset(centerX, centerY)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(ballSize)
                        .graphicsLayer {
                            translationX = ballX - ballRadiusPx
                            translationY = ballY - ballRadiusPx
                        }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .blur(8.dp)
                            .background(
                                Color(0xFF6D9EEB).copy(alpha = 0.22f),
                                CircleShape
                            )
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color.White,
                                        Color(0xFFA8C7F5),
                                        Color(0xFF5F91DD)
                                    )
                                )
                            )
                            .border(
                                width = 1.dp,
                                color = Color.White,
                                shape = CircleShape
                            )
                    )
                }

                Text(
                    text = "Tilt your phone",
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 18.dp),
                    color = Color(0xFFB1B6C2),
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFFEFF4FC))
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFDCE9FC)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "↗",
                        color = Color(0xFF4F83D1),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Cara menggunakan",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF303746)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Miringkan HP ke kiri atau kanan.",
                        fontSize = 11.sp,
                        color = Color(0xFF858C9A)
                    )
                }
            }
        }
    }
}
