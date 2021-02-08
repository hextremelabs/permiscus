# Permiscus

Android runtime permissions were introduced in Android 6.0 Marshmallow. They are unquestionably 
a boon for Android users, but can be a pain for developers. 

With runtime permissions, Android app developers can no longer assume that the app has permission
to access contacts, accounts, camera or any other permissions categorised as 'dangerous.'
The app therefore need to check for permissions each time it wants to perform an operation that
requires a permission, and to ask the user to grant the permission if the app does not already 
posses it.

This is somewhat cumbersome and requires a great deal of boilerplate code. The library aims at
removing complexities and making permissions request fun again, allowing the developer to 
focus on the actual app functionality instead of Android technicalities.

### Example

Google's RuntimePermissionsBasic sample illustrates how to ask permission to access the camera. 
The sample project contains code for checking if the permission is already granted, 
checking if the app should show a permission rationale, requesting the permission, evaluation 
the result of the request and finally launching a camera preview activity if the permission 
is granted.

Even stripped of JavaDoc and comments, the sample code still takes up approximately 100 lines 
of code, almost all dealing with the single permission request. 
[Take a look at the code here](https://github.com/googlesamples/android-RuntimePermissionsBasic/blob/master/Application/src/main/java/com/example/android/basicpermissions/MainActivity.java)


The same operation using this library is as simple as:

```kotlin
private val permissionManager = PermissionManager.create(this)

private fun showCameraPreview() {
    val callback = SimplePermissionCallback.with(layout)
        .rationale("Camera permission is required to take your pictures")
        .instructions("Open permissions and tap on Camera to enable it")
        .onPermissionsGranted { startActivity(Intent(this, CameraPreviewActivity::class.java)) }
        .create()
    permissionManager.with(Manifest.permission.CAMERA)
        .usingRequestCode(PERMISSION_REQUEST_CAMERA)
        .onCallback(callback)
        .request()
}

override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
    permissionManager.handlePermissionResult(requestCode, grantResults)
}
```

This not only reduces the amount of code by about half, but also greatly improves readability.

## Usage

### Download

Gradle
```gradle
dependencies {
    implementation 'com.hextremelabs.permiscus:permiscus:0.2.0'
}
```

Maven
```xml
<dependency>
    <groupId>com.hextremelabs.permiscus</groupId>
    <artifactId>permiscus</artifactId>
    <version>0.2.0</version>
</dependency>
```

[Download Latest AAR](https://search.maven.org/remote_content?g=com.hextremelabs.permiscus&a=permiscus&v=LATEST&e=aar)

Snapshots of developement versions are available 
on [Sonatype snapshots repository](https://oss.sonatype.org/content/groups/public/)

### Creating a PermissionManager

Each Activity/Fragment need to implement the appropriate OnRequestPermissionsResultCallback 
from the support library and delegate the onRequestPermissionsResult to a PermissionManager 
instance.

For activities:


```kotlin
class MainActivity : AppCompatActivity(), OnRequestPermissionsResultCallback {

    private val permissionManager = PermissionManager.create(this)

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        permissionManager.handlePermissionResult(requestCode, grantResults)
    }

    // ...
}
```

For fragments:

```kotlin
class ContactRationaleFragment : Fragment(), ActivityCompat.OnRequestPermissionsResultCallback {

    private val permissionManager = PermissionManager.create(this)

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        permissionManager.handlePermissionResult(requestCode, grantResults)
    }

    // ...
}
```

NOTE: Please do NOT make the PermissionManager instance static, or you'll risk introducing memory 
leaks in you activities.

### Performing requests and handling callbacks

A permission request is performed using a syntax inspired by Glide and similar libraries. 

```kotlin
// Start building a new request using the with() method. 
// The method takes either a single permission or a list of permissions.
// Specify multiple permissions in case you need to request both 
// read and write access to the contacts at the same time, for example.
permissionManager.with(...)
        // Optionally, specify a request code value for the request
        .usingRequestCode(REQUEST_CODE) 
        
        // Optionally, specify a callback handler for all three callbacks
        .onCallback(object : OnPermissionCallback {...})

        // OR specify handlers for each callback separately
        .onPermissionGranted { ... }
        .onPermissionDenied { neverAskAgain -> ... }
        .onPermissionShowRationale { request -> ... }
        
        // Finally, perform the request
        .request()
```

If the app already has the requested permission then the onPermissionGranted callback is invoked
at once. Similarly, if the user has denied the permission and checked the 'never ask again option'
the onPermissionDenied callback is invoked immediately. The onPermissionShowRationale callback is 
invoked if the app should show a rationale before asking the user to grant the permission. 
If neither of these conditions are met, then the permissionManager requests the permission and the 
onPermissionGranted/onPermissionDenied callbacks called once the user has answered.

Alternatively, it is also possible to check for the permission 'silently':

```kotlin
permissionManager.with(...)
        .onPermissionGranted(...)
        .onPermissionDenied(...)
        .check()
```

The check() method will not ask the user for permission if it is not already granted, 
thus the onPermissionShowRationale callback is irrelevant. It will always perform the check and 
invoke either the onPermissionGranted or the onPermissionDenied callback at once.

### Callback interfaces

The callbacks are simple single methods interfaces - with the exception of the aggregate 
OnPermissionCallback interface:

```kotlin
fun interface OnPermissionDeniedCallback {
    fun onPermissionDenied(neverAskAgain: Boolean)
}

fun interface OnPermissionGrantedCallback {
    fun onPermissionGranted()
}

fun interface OnPermissionShowRationaleCallback {
    fun onPermissionShowRationale(permissionRequest: PermissionRequest)
}

interface OnPermissionCallback :
    OnPermissionGrantedCallback,
    OnPermissionDeniedCallback,
    OnPermissionShowRationaleCallback
```

When the onPermissionShowRationale callback is called, the app is expected to show some kind of 
rationale to the user, perhaps in the form of a Snackbar. The rationale should include 
a way for the user to indicate acceptance of the rationale, typically in the form of a
"OK" og "GOT IT" button. 

The PermissionRequest instance provided by the onPermissionShowRationale callback contains a 
single public method. This method lets the permission manager know that the user has accepted the 
rationale. If the rationale is accepted then the permission manager automatically tries to request 
the permission again. 

```kotlin
override fun onPermissionShowRationale(permissionRequest: PermissionRequest) {
    ...
    
    okButton.setOnClickListener { permissionRequest.acceptPermissionRationale() }
}
```

When the user answers the permission prompt, the onPermissionGranted or onPermissionDenied 
callbacks are called, just as if the rationale had not been shown.

### Known issues and limitations

In order to avoid memory leaks, the callbacks (OnPermissionGranted/OnPermissionDenied/
OnPermissionShowRationale) aren't kept in memory during configuration changes or when
Android kills an activity to claim its memory. The callbacks are destroyed with the activity.

Therefore, if for example a user presses the 'show camera' button in the sample app - and then 
rotates the device while the permission prompt is visible - the permission callbacks are lost
when the activity is recreated and before onRequestPermissionsResult is called.

There are a couple of ways to deal with this limitation:

* Ignore it. The situation will probably arise rather seldom, and if the permission request
is invoked as a result of a button press, then the user only need to press the button again.
On the second button press, the permission will either already be granted, in which case the 
OnPermissionGranted will fire at once, or the permission will be denied and the 
OnPermissionShowRationale callback is likely to be invoked. 

* Handle onRequestPermissionsResult yourself. If the 
permissionManager.handlePermissionResult(...) method returns false then the library failed to 
find a callback for the permission result. You might at this point use the check() method
to ensure that the proper callback is still invoked. This, unfortunately, involves a bit of  
code duplication and for you to set the request codes by calling usingRequestCode(...) when
requesting a permission. For example:

```kotlin
    permissionManager.with(Manifest.permission.CAMERA)
        .usingRequestCode(MY_REQUEST_CODE) 
        .onCallback(...)
        .request()

    ...

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        val handled = permissionManager.handlePermissionResult(requestCode, grantResults)
        if (handled) return

        when (requestCode) {
            MY_REQUEST_CODE ->
                permissionManager.with(Manifest.permission.CAMERA)
                    .onPermissionGranted(...)
                    .onPermissionDenied(...)
                    .check()
            MY_OTHER_REQUEST_CODE ->
                ...
        }
    }
```


## Author

Kingsley Adio<br/>
Email: <kingsley@hextremelabs.com><br/>

<h3>Originally maintained by:</h3>
Nicolai Buch-Andersen<br/>
Google+ <https://google.com/+NicolaiBuchAndersen><br/>
Email: <nicolai.buch.andersen@gmail.com><br/>

## License

    Copyright 2016 Hextremelabs Ltd.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
