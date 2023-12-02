package com.example.c322p10sensorsgestures

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter

class AccelerometerActivity : ComponentActivity() {
    // Sensor variables
    private val pos = mutableStateOf(Offset(0f, 0f))

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize sensor manager and accelerometer sensor
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        // Set up the content with accelerometer gestures
        setContent {
            SensorMoveGestures()
        }
    }

    override fun onResume() {
        super.onResume()
        // Register the accelerometer sensor listener
        accelerometer?.let {
            sensorManager.registerListener(
                accelerometerListener,
                it,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }
/*
Composable
@param none
create gesture area
 */
    @Composable
    fun SensorMoveGestures() {
        var screenWidth by remember { mutableStateOf(0) }
        var screenHeight by remember { mutableStateOf(0) }

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Green)
                .onGloballyPositioned {
                    screenWidth = it.size.width
                    screenHeight = it.size.height
                }
        ) {
            Row(Modifier.fillMaxSize()) {
                SensorMoveGesturesContent(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    pos = pos.value
                )
            }
        }
    }
/*
@param modifier:Modifier, position:Offset
create gesture ball
 */
    @Composable
    fun SensorMoveGesturesContent(modifier: Modifier, pos: Offset) {
        BoxWithConstraints(
            modifier = modifier
                .background(Color.Green)
        ) {
            val initX = constraints.maxWidth/2f
            val initY = constraints.maxHeight/2f

            Image(
                painter = rememberImagePainter(R.drawable.ic_launcher_foreground),
                contentDescription = null,
                modifier = Modifier
                    .graphicsLayer(
                        translationX = pos.x+initX,
                        translationY = pos.y+initY
                    )
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Color.Red)
            )
        }
    }
//listens to accelerometer
    private val accelerometerListener = object : SensorEventListener {
        /*
        @param event:Sensor event
        changes current position of ball if accelerometer values were changed
         */
        override fun onSensorChanged(event: SensorEvent?) {
            event?.let {
                val x = it.values[0]
                val y = it.values[1]
                val sensitivity = 10f
                pos.value=(Offset(x * sensitivity, y * sensitivity))
            }
        }
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }
}