package com.example.c322p10sensorsgestures

import android.content.res.Configuration
import android.graphics.Matrix
import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

class GestureActivity:ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GestureActivityContent()
        }
    }
}
/*
@Composable
@param none
create gesture area and log area
 */
@Composable
fun GestureActivityContent(){
    //variables
    val config = LocalConfiguration.current
    val pos = remember {
        Animatable(Offset(0f,0f), Offset.VectorConverter)
    }
    var gestureLog by remember {
        mutableStateOf(listOf<String>())
    }
    val coroutineScope = rememberCoroutineScope()

    //changes view based on orientation
    //landscape/horizontal
    if (config.orientation==Configuration.ORIENTATION_LANDSCAPE){
        Row(Modifier.fillMaxSize()) {
            Gestures(modifier= Modifier
                .weight(1f)
                .fillMaxHeight(),
                //move ball/add to log when gesture is detected
                onGesDetect={dx,dy->
                    val describe = describeMove(dx,dy)
                    gestureLog = listOf(describe) + gestureLog.take(14)
                    val targetPos = Offset(pos.value.x +dx, pos.value.y+dy)
                    coroutineScope.launch { pos.animateTo(targetPos, animationSpec = tween(durationMillis = 600)) }
                })
            //log with movement descriptions
            GestureLog(gestureLog,modifier=Modifier.weight(1f))
        }
    }
    //vertical orientation
    else{
        Column(Modifier.fillMaxSize()) {
            Gestures(
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
                //move ball/add to log when gesture is detected
                onGesDetect={dx,dy->
                    val describe = describeMove(dx,dy)
                    gestureLog = listOf(describe) + gestureLog.take(14)
                    val targetPosition = Offset(pos.value.x + dx, pos.value.y + dy)
                    coroutineScope.launch { pos.animateTo(targetPosition, animationSpec = tween(durationMillis = 600)) }
                })
            //log with movement description
            GestureLog(gestureLog, Modifier.weight(1f))
        }
    }
}
/*
@Composable
@param modifier:Modifier, onGestureDetected:(Float,Float)->Unit
identify gestures, move ball
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Gestures(modifier:Modifier,onGesDetect:(Float,Float)->Unit){
    //variables
    val density = LocalDensity.current
    var start by remember {
        mutableStateOf(Offset.Zero)
    }
    var currMatrix by remember {
        mutableStateOf(Matrix())
    }
    var hasLoggedMove by remember {
        mutableStateOf(false)
    }
    var screenWidth by remember {
        mutableStateOf(0)
    }
    var screenHeight by remember {
        mutableStateOf(0)
    }

    BoxWithConstraints(modifier= modifier
        .background(Color.Green)
        .onGloballyPositioned {
            screenWidth = it.size.width
            screenHeight = it.size.height
        }
        .pointerInteropFilter { motionEvent ->
            //when gesture is detected:
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    //down movement
                    start = Offset(motionEvent.x, motionEvent.y)
                    hasLoggedMove = false
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    //movement action between up/down
                    val dx = (motionEvent.x - start.x) / density.density
                    val dy = (motionEvent.y - start.y) / density.density

                    //checks to make sure ball is within screen
                    if (isWithinBound(currMatrix, dx, dy,screenWidth,screenHeight)) {
                        currMatrix.postTranslate(dx, dy)

                        //logs move
                        if (!hasLoggedMove) {
                            onGesDetect(dx, dy)
                            hasLoggedMove = true
                        }
                    }
                    true
                }
                MotionEvent.ACTION_UP -> {
                    //up movement
                    start = Offset.Zero
                    hasLoggedMove = false
                    true
                }
                else -> false
            }
        }) {
        //ball image with icon
        Image(painter = rememberImagePainter(R.drawable.ic_launcher_foreground),
            contentDescription = null,
        modifier = Modifier
            .graphicsLayer(
                translationX = currMatrix.getTranslateX(),
                translationY = currMatrix.getTranslateY()
            )
            .size(50.dp)
            .clip(CircleShape)
            .background(Color.Red)
        )
    }
}
/*
@param none
@return Float
translate x axis for matrix
 */
fun Matrix.getTranslateX():Float{
    val values = FloatArray(9)
    getValues(values)
    return values[Matrix.MTRANS_X]
}
/*
@param none
@return Float
translate y axis for matrix
 */
fun Matrix.getTranslateY():Float{
    val values = FloatArray(9)
    getValues(values)
    return values[Matrix.MTRANS_Y]
}
/*
@param matrix:Matrix, dx:Float, dy:Float, screenWidth:Int, screenHeight:Int
@return Boolean
returns whether or not the ball is going out of bounds
 */
fun isWithinBound(matrix:Matrix,dx:Float,dy:Float,screenW:Int, screenH:Int):Boolean{
    val values = FloatArray(9)
    matrix.getValues(values)
    val x = values[Matrix.MTRANS_X]+dx
    val y = values[Matrix.MTRANS_Y]+dy
    var ballSize = 50.dp.value
    return x>=0 && x<=screenW-ballSize && y>=0 && y<=screenH-ballSize
}
/*
@Composable
@param gestureLog:List<String>, modifier:Modifier
creates and display a log of all completed gestures
 */
@Composable
fun GestureLog(gestureLog:List<String>, modifier: Modifier){
    Box(modifier = modifier.fillMaxSize(),
        Alignment.TopCenter){
    Column(modifier = modifier
        .background(Color.LightGray)
        .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Gesture Log", fontSize = 30.sp)
        Spacer(modifier = Modifier.height(20.dp))
        gestureLog.forEach{gesture->Text(gesture)}
    }}
}
/*
@param dx:Float, dy:Float
@return String
detects what kind of gesture was made, creates corresponding string for log
 */
fun describeMove(dx:Float, dy:Float):String{
    val absDx = dx.absoluteValue
    val absDy = dy.absoluteValue
    if (absDx > absDy+absDy*.5f && dx>0)
        return "Swiped right"
    else if (absDx > absDy+absDy*.5f && dx<0)
        return "Swiped left"
    else if (absDy > absDx+absDx*.5f && dy>0)
        return "Swiped down"
    else if (absDy > absDx+absDx*.5f && dy<0)
        return "Swiped up"
    else if (absDx > absDy - absDy * .5f && absDx < absDy + absDy * .5f && dx > 0){
        if (dy>0)
            return "Swiped bottom-right"
        else
            return "Swiped top-right"
        }
    else if (absDx > absDy - absDy * .5f && absDx < absDy + absDy * .5f && dx < 0){
        if (dy>0)
            return "Swiped bottom-left"
        else
            return "Swiped top-left"
    }
    else return "No swipe"
}