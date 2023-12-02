package com.example.c322p10sensorsgestures

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.Locale


class SensorActivity:ComponentActivity(), SensorEventListener, LocationListener {

    //variables
    private lateinit var sensorManager:SensorManager
    private lateinit var locManager:LocationManager
    private lateinit var geocoder:Geocoder
    private var liveTemp = mutableStateOf(0f)
    private var liveAirP = mutableStateOf(0f)
    private var liveState = mutableStateOf("")
    private var liveCity = mutableStateOf("")
    companion object{
        private const val permissionRequest = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //declare sensor/location managers
        sensorManager = getSystemService(Context.SENSOR_SERVICE)as SensorManager
        locManager = getSystemService(Context.LOCATION_SERVICE)as LocationManager
        geocoder = Geocoder(this, Locale.getDefault())

        //register temperature to the sensor manager
        sensorManager.registerListener(this,
            sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE),
            SensorManager.SENSOR_DELAY_NORMAL)

        //register air pressure to the sensor manager
        sensorManager.registerListener(this,
            sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE),
            SensorManager.SENSOR_DELAY_NORMAL)

        //update location
        update()

        //set up content composable
        setContent {
            SensorContent(
                city = liveCity.value,
                state = liveState.value,
                temp = liveTemp.value,
                airP = liveAirP.value
            )
        }
    }
    /*
    @param none
    updates the current location of the device using Geocoder
    locality = city
    adminArea = state
     */
    private fun update(){
        //checks for permissions
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            val locListener = LocationListener { location->
                //retrieve data, set variables
                val address = geocoder!!.getFromLocation(location.latitude,location.longitude,1)
                val ad = address?.firstOrNull()
                liveCity.value = "${ad?.locality}"
                liveState.value = "${ad?.adminArea}"
            }
            locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L,0f,locListener)
        }
        else
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), permissionRequest)
    }
    /*
    @param e:Sensor event
    updates temperature and air pressure values when sensor detects a change
     */
    override fun onSensorChanged(e: SensorEvent) {
        when(e.sensor.type){
            Sensor.TYPE_AMBIENT_TEMPERATURE->liveTemp.value= e.values[0]
            Sensor.TYPE_PRESSURE->liveAirP.value = e.values[0]
        }
    }
    /*
    @param l:Location
    updates city and state when location is changed
     */
    override fun onLocationChanged(l: Location) {
        val address = geocoder.getFromLocation(l.latitude, l.longitude, 1)
        val ad = address?.firstOrNull()
        liveCity.value = "${ad?.locality}"
        liveState.value = "${ad?.adminArea}"
    }
    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}
}

/*
Composable
@param state:String, city:String, temperature:Float, air pressure:Float
use values to create display
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SensorContent(state:String,city:String, temp:Float, airP:Float){
    val context = LocalContext.current
    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var text by remember {
            mutableStateOf("")
        }
        //title
        Text("Sensor Playground", fontSize = 20.sp)
        Spacer(Modifier.height(20.dp))
        //field to enter name
        TextField(value = text,
            onValueChange = {text = it},
            label = {Text("Name")})
        //location: state and city
        Text("Location: ")
        Text("City: $city")
        Text("State: $state")
        Spacer(Modifier.height(20.dp))
        //temperature in current location
        Text("Temperature: ${(temp.dec()*(9/5))+32}")
        Spacer(Modifier.height(20.dp))
        //air pressure in current location
        Text("Air Pressure: $airP")
        Spacer(Modifier.height(40.dp))
        //start gesture activity when fling is detected
        FlingButton(onFling={
            val intent = Intent(context, GestureActivity::class.java)
            context.startActivity(intent)
        })
        //start accelerometer gesture activity when clicked
        //extra credit
        Button(onClick = { val intent = Intent(context, AccelerometerActivity::class.java)
            context.startActivity(intent)}) {
            Text(text = "Accelerometer Ball")
        }
    }
}
/*
Composable
@param onFling:()->Unit
identifies fling gesture
creates Gesture Playground button
non-clickable
 */
@Composable
fun FlingButton(onFling:()->Unit){
    var offset by remember {
        mutableStateOf(Offset.Zero)
    }
    val gestureModifier = Modifier.pointerInput(Unit){
        detectDragGestures { change, dragAmount ->
            offset += dragAmount
            val value = 10
            if (offset.x>value)
                onFling()
        }
    }
    Button(onClick = {}, modifier = gestureModifier) {
        Text("Gesture Playground")
    }
}
