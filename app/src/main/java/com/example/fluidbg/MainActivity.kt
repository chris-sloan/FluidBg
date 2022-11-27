package com.example.fluidbg

import android.graphics.Matrix
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fluidbg.ui.theme.FluidBgTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FluidBgTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var size by remember { mutableStateOf(Size.Zero) }

                    val shaderA = LinearGradientShader(
                        Offset(size.width / 2f, 0f),
                        Offset(size.width / 2f, size.height),
                        listOf(
                            Color.Red,
                            Color.Yellow,
                        ),
                        listOf(0f, 1f)
                    )

                    val shaderB = LinearGradientShader(
                        Offset(size.width / 2f, 0f),
                        Offset(size.width / 2f, size.height),
                        listOf(
                            Color.Magenta,
                            Color.Green,
                        ),
                        listOf(0f, 1f)
                    )

                    val shaderMask = LinearGradientShader(
                        Offset(size.width / 2f, 0f),
                        Offset(size.width / 2f, size.height),
                        listOf(
                            Color.White,
                            Color.Transparent,
                        ),
                        listOf(0f, 1f)
                    )

                    val brushA by animateBrushRotation(shaderA, size, 20_000, 1)
                    val brushB by animateBrushRotation(shaderB, size, 12_000, -1)
                    val brushMask by animateBrushRotation(shaderMask, size, 15_000, 1)

                    Box(
                        modifier = Modifier
                            .requiredSize(300.dp)
                            .onSizeChanged {
                                size = Size(it.width.toFloat(), it.height.toFloat())
                            }
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, Color.White, RoundedCornerShape(16.dp))
                            .drawBehind {
                                drawRect(brushA)
                                drawRect(brushMask, blendMode = BlendMode.DstOut)
                                drawRect(brushB, blendMode = BlendMode.DstAtop)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            modifier = Modifier
                                .border(1.dp, Color.White, RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            text = "FLUID",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Light
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun animateBrushRotation(
    shader: Shader,
    size: Size,
    duration: Int,
    direction: Int
): State<ShaderBrush> {
    val infiniteTransition = rememberInfiniteTransition()
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = direction * 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(duration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    return remember(shader, size) {
        derivedStateOf {
            val matrix = Matrix().apply {
                postRotate(angle, size.width / 2, size.height / 2)
            }
            shader.setLocalMatrix(matrix)
            ShaderBrush(shader)
        }
    }
}