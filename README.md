# PhotoKit Library

## How to use:

- Add it in your root build.gradle at the end of repositories:
```gragle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
- Add the dependency
```gradle
dependencies {
    implementation 'com.github.prongbang:photolib:1.0.0'
}
```

- Select Image from Gallery
```kotlin
PhotoKit.create(this@MainActivity, BuildConfig.APPLICATION_ID)
    .gallery()
    .addOnPhotoListener(object : PhotoKit.OnPhotoListener {
        override fun onResult(data: Uri?) {
            ivPreview.setImageURI(data)
        }
    })
```

- Select Image by options

```kotlin
PhotoKit.create(this@MainActivity, BuildConfig.APPLICATION_ID)
    .addOnPhotoListener(object : PhotoKit.OnPhotoListener {
        override fun onResult(data: Uri?) {
            ivPreview.setImageURI(data)
        }
    })
    .selectImage()
```

- Take a Photo

```kotlin
PhotoKit.create(this@MainActivity, BuildConfig.APPLICATION_ID)
    .takePhoto()
    .addOnCameraListener(object : PhotoKit.OnCameraListener {
        override fun onResult(bitmap: Bitmap?) {
            ivPreview.setImageBitmap(bitmap)
        }
    })
```

- On Activity Result

```kotlin
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)

    PhotoKit.onActivityResult(requestCode, resultCode, data)
}

```
