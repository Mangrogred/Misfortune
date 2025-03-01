package com.mangro.misfortune.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.tooling.preview.devices.WearDevices
import com.mangro.misfortune.R
import com.mangro.misfortune.data.Fortunes
import com.mangro.misfortune.presentation.theme.MisfortuneTheme
import androidx.compose.ui.unit.dp
import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.Arrangement

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setTheme(android.R.style.Theme_DeviceDefault)
        setContent {
            WearApp()
        }
    }
}

@Composable
fun WearApp() {
    MisfortuneTheme {
        val infiniteTransition = rememberInfiniteTransition(label = "background")
        val scale by infiniteTransition.animateFloat(
            initialValue = 1.0f,
            targetValue = 1.2f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulse"
        )

        var isPressed by remember { mutableStateOf(false) }
        val backgroundAlpha by animateFloatAsState(
            targetValue = if (isPressed) 0.15f else 0.1f,
            animationSpec = tween(500),
            label = "alpha"
        )

        var prediction by remember { mutableStateOf(Fortunes.getRandomPrediction()) }
        var rotationState by remember { mutableStateOf(0f) }
        
        val rotation by animateFloatAsState(
            targetValue = rotationState,
            animationSpec = tween(500),
            label = "rotation"
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        alpha = backgroundAlpha
                    }
                    .background(
                        color = MaterialTheme.colors.primary,
                        shape = CircleShape
                    )
                    .fillMaxSize(0.8f)
            )

            TimeText()
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .graphicsLayer {
                        rotationZ = rotation
                    }
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = {
                                rotationState += 360f
                                prediction = Fortunes.getRandomPrediction()
                                isPressed = false
                            },
                            onPress = {
                                isPressed = true
                                tryAwaitRelease()
                                isPressed = false
                            }
                        )
                    }
            ) {
                Text(
                    text = "Misfortune",
                    color = MaterialTheme.colors.primary.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.caption2,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = prediction,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colors.primary,
                    style = MaterialTheme.typography.title2,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                )
            }
        }
    }
}

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp()
}