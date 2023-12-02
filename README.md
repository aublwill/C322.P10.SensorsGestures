# C322.p10.SensorsGestures
This project functions as a sensor display and gestures playground

## Testing Link
Developer account under review, will upload asap

## Functionality 
The following **required** functionality is completed:
* [] user is asked location permission
* [] if allowed, the screen displays the users current location (state and city), temperature, and air pressure
* [] user can also enter their name on the screen
* [] below the information, the user can see two buttons
* [] if a swipe/fling motion is completed on the "gesture playground" button, the user will be directed there
* [] within the gesture playground, the user can fling up,down,sideways,diagnally, and the ball will follow
* [] once a esture is completed, the gesture log below will be updated with what kind of motion was done
* [] if the "accelerometer ball" button is pressed (on the sensors screen), user will be directed there
* [] here, the user can use the accelerometer to move the mall around, there is no log here

The folowing **extensions** are implemented:

* import android.content.Context
* import android.hardware.*
* import android.os.Bundle
* import androidx.activity.ComponentActivity
* import androidx.activity.compose.setContent
* import androidx.compose.foundation.*
* import androidx.compose.foundation.layout.*
* import androidx.compose.foundation.shape.CircleShape
* import androidx.compose.runtime.*
* import androidx.compose.ui.Modifier
* import coil.compose.rememberImagePainter
* import android.content.res.Configuration
* import android.graphics.Matrix
* import android.os.Bundle
* import android.view.MotionEvent
* import androidx.activity.ComponentActivity
* import androidx.activity.compose.setContent
* import androidx.compose.animation.core.*
* import androidx.compose.material3.Text
* import androidx.compose.ui.*
* import androidx.compose.ui.draw.clip
* import androidx.compose.ui.geometry.Offset
* import androidx.compose.ui.graphics.*
* import androidx.compose.ui.input.pointer.pointerInteropFilter
* import androidx.compose.ui.layout.onGloballyPositioned
* import androidx.compose.ui.platform.*
* import androidx.compose.ui.unit.*
* import com.example.c322p10sensorsgestures.R
* import kotlinx.coroutines.launch
* import kotlin.math.absoluteValue
* import java.util.Locale
* import android.content.Intent
* import android.content.pm.PackageManager
* import android.hardware.*
* import android.location.*
* import android.os.Bundle
* import androidx.activity.ComponentActivity
* import androidx.activity.compose.setContent
* import androidx.compose.foundation.gestures.detectDragGestures
* import androidx.compose.foundation.layout.*
* import androidx.compose.material3.*
* import androidx.compose.runtime.*
* import androidx.compose.ui.*
* import androidx.compose.ui.geometry.Offset
* import androidx.compose.ui.input.pointer.pointerInput
* import androidx.compose.ui.platform.LocalContext
  
## Video Walkthrough 



https://github.com/aublwill/C322.p10.SensorsGestures/assets/143005409/53322aca-cf3e-4893-9401-896793dd91fe





## Notes
* Ball sometimes flings harder than it should
* Location on my emmulator is stuck in Cali

## License
Copyright [2023] [Aubrey Williams]

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
